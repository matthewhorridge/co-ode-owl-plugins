package org.coode.annotate;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;

import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.io.*;

/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 16, 2007<br><br>
 */
public class AnnotateViewModel {

    private static final Logger logger = Logger.getLogger(AnnotateViewModel.class.getName());

    public static final String TEXT = "text";
    public static final String ENTITY = "entity";
    public static final String MULTILINE = "multiline";

    private static final String PROPS_FILENAME = "resources/default.template";

    // really need an ordered Map, but for now, maintain a separate list
    private final List<URI> uriList = new ArrayList<URI>();
    
    private final Map<URI, Set<String>> templateMap = new HashMap<URI, Set<String>>();

    private final java.util.List<AnnotationRow> cList = new ArrayList<AnnotationRow>();

    private final AnnotationComponentComparator comparator = new AnnotationComponentComparator();

    private final Set<AnnotationModelListener> listeners = new HashSet<AnnotationModelListener>();

    private final OWLModelManager mngr;

    private OWLEntity currentEntity;

    private boolean needsRefresh;

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){
        public void ontologiesChanged(List<? extends OWLOntologyChange> list) throws OWLException {
            handleOntologyChanges(list);
        }
    };


    public AnnotateViewModel(OWLModelManager mngr) throws IOException {
        this.mngr = mngr;
        InputStream stream = getClass().getClassLoader().getResourceAsStream(PROPS_FILENAME);
        load(stream);

        mngr.addOntologyChangeListener(ontChangeListener);
    }

    private void load(InputStream stream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while(reader.ready()){
            String line = reader.readLine();
            if (line != null && !"".equals(line) && !line.startsWith("//") && !line.startsWith("#")){
                String[] tokens = line.split(",");
                try {
                    URI uri = new URI(tokens[0].trim());
                    Set<String> paramlist = new HashSet<String>();
                    for (int i=1; i<tokens.length; i++){
                        paramlist.add(tokens[i].trim());
                    }
                    templateMap.put(uri, paramlist);
                    uriList.add(uri);
                }
                catch (URISyntaxException e) {
                    logger.error(e);
                }
            }
        }
    }

    public String getComponentType(URI uri){
        Set<String> params = templateMap.get(uri);

        // the order of these will determine precedence if multiple component types are specified
        if (params.contains(ENTITY)){
            return ENTITY;
        }
        else if (params.contains(MULTILINE)){
            return MULTILINE;
        }
        else {// default to text (params.contains(TEXT)){
            return TEXT;
        }
    }

    public Set<OWLAnnotationAxiom> getAnnotations(OWLEntity entity) {
        Set<OWLAnnotationAxiom> annotations = new HashSet<OWLAnnotationAxiom>();
        for (OWLOntology ont : mngr.getActiveOntologies()){
            annotations.addAll(entity.getAnnotationAxioms(ont));
        }
        return annotations;
    }

    public OWLModelManager getOWLModelManager() {
        return mngr;
    }

    public OWLEntity getEntity() {
        return currentEntity;
    }

    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        needsRefresh = false;

        for (OWLOntologyChange change : changes){
            OWLAxiom ax = change.getAxiom();
            ax.accept(new OWLAxiomVisitorAdapter(){
                public void visit(OWLEntityAnnotationAxiom owlEntityAnnotationAxiom) {
                    if (owlEntityAnnotationAxiom.getSubject().equals(currentEntity)){
                        needsRefresh = true;
                    }
                }
            });
        }

        if (needsRefresh){
            refresh();
            needsRefresh = false;
        }
    }

    private void refresh(){
        setEntity(currentEntity);
    }

    public void setEntity(OWLEntity entity) {
        currentEntity = entity;
        cList.clear();

        if (entity != null){
            Set<OWLAnnotationAxiom> annots = getAnnotations(entity);
            Set<URI> usedURIs = new HashSet<URI>();
            for (OWLAnnotationAxiom annot : annots){
                final URI annotationURI = annot.getAnnotation().getAnnotationURI();
                if (uriList.contains(annotationURI)){
                    usedURIs.add(annotationURI);
                    cList.add(new AnnotationRow(annot, this));
                }
            }

            for (URI uri : uriList){
                if (!usedURIs.contains(uri)){
                    cList.add(new AnnotationRow(entity, uri, this));
                }
            }

            Collections.sort(cList, comparator);
        }

        notifyStructureChanged();
    }

    public List<AnnotationRow> getComponents() {
        return Collections.unmodifiableList(cList);
    }

    public void addRow(URI uri) {
        cList.add(new AnnotationRow(currentEntity, uri, this));
        Collections.sort(cList, comparator);
        notifyStructureChanged();
    }

    public void removeRow(AnnotationRow c) {
        c.setValue(null);
        cList.remove(c);
        notifyStructureChanged();
    }

    private void notifyStructureChanged() {
        for (AnnotationModelListener l : listeners){
            l.modelStructureChanged();
        }
    }

    public void addModelListener(AnnotationModelListener l){
        listeners.add(l);
    }

    public void removeModelListener(AnnotationModelListener l){
        listeners.remove(l);
    }

    /**
     * Called by the rows when they make changes
     * (so that the table can manage itself without having to refresh completely)
     * This implementation disables the ontology change listeners temporarily
     *
     * @param changes the set of changes to apply
     */
    public void requestApplyChanges(List<OWLOntologyChange> changes) {
        mngr.removeOntologyChangeListener(ontChangeListener);
        mngr.applyChanges(changes);
        mngr.addOntologyChangeListener(ontChangeListener);
    }

    public void dispose() {
        mngr.removeOntologyChangeListener(ontChangeListener);
        listeners.clear();
    }

    class AnnotationComponentComparator implements Comparator<AnnotationRow> {

    public int compare(AnnotationRow c1, AnnotationRow c2) {
        URI uri1 = c1.getURI();
        URI uri2 = c2.getURI();
        for (URI uri : uriList){
            if (uri.equals(uri1)){
                return -1;
            }
            if (uri.equals(uri2)){
                return 1;
            }
        }
        return 0;
    }
}
}
