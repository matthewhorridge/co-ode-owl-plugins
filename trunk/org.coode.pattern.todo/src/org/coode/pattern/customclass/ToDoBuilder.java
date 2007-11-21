package org.coode.pattern.customclass;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 3, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class ToDoBuilder extends OWLObjectVisitorAdapter {

    private ToDoPatternDescriptor descr;
    private ToDoPattern pattern;
    private OWLModelManager mngr;
    private String searchString;

    private Pattern regexp;

    public ToDoBuilder(ToDoPatternDescriptor descr, OWLModelManager mngr) {
        this.descr = descr;
        this.mngr = mngr;
        searchString = "(.*)[tT][oO][dD][oO](.*)";
        regexp = Pattern.compile(searchString);

    }

    public void visit(OWLClass owlClass) {
        visitEntity(owlClass);
    }

    public void visit(OWLObjectProperty owlObjectProperty) {
        visitEntity(owlObjectProperty);
    }

    public void visit(OWLDataProperty owlDataProperty) {
        visitEntity(owlDataProperty);
    }

    public void visit(OWLIndividual owlIndividual) {
        visitEntity(owlIndividual);
    }

    public void visitEntity(OWLEntity entity) {
        URI p = descr.getMarkerAnnotation(mngr);
        if (p != null){
            for (OWLOntology ont : mngr.getActiveOntologies()){
                for (OWLAnnotation annot : entity.getAnnotations(ont, p)) {
                    if (regexp.matcher(annot.getAnnotationValueAsConstant().getLiteral()).matches()){
                        pattern = new ToDoPattern(entity, descr, mngr);
                        return;
                    }
                }
            }
        }
        else{
            for (OWLOntology ont : mngr.getActiveOntologies()){
                for (OWLAnnotation annot : entity.getAnnotations(ont)) {
                    if (regexp.matcher(annot.getAnnotationValueAsConstant().getLiteral()).matches()){
                        pattern = new ToDoPattern(entity, descr, mngr);
                        return;
                    }
                }
            }
        }

    }

    public ToDoPattern getCustomClass(){
        return pattern;
    }
}
