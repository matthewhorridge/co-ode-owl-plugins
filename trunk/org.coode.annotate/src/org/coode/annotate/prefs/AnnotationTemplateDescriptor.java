package org.coode.annotate.prefs;

import org.apache.log4j.Logger;
import org.coode.annotate.EditorType;
import org.protege.editor.core.prefs.Preferences;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 11, 2009<br><br>
 */
public class AnnotationTemplateDescriptor {

    private static final Logger logger = Logger.getLogger(AnnotationTemplateDescriptor.class);


    private List<OWLAnnotationProperty> properties;

    private Map<OWLAnnotationProperty, EditorType> prop2EditorMap;


    public AnnotationTemplateDescriptor(InputStream stream, OWLDataFactory df) throws IOException {
        properties = new ArrayList<OWLAnnotationProperty>();
        prop2EditorMap = new HashMap<OWLAnnotationProperty, EditorType>();
        parse(stream, df);
    }


    public AnnotationTemplateDescriptor(Preferences prefs, String key, OWLDataFactory df) {
        properties = new ArrayList<OWLAnnotationProperty>();
        prop2EditorMap = new HashMap<OWLAnnotationProperty, EditorType>();
        for (String line : prefs.getStringList(key, new ArrayList<String>())){
            parseLine(line, df);
        }
    }


    public AnnotationTemplateDescriptor(AnnotationTemplateDescriptor descriptor) {
        properties = new ArrayList<OWLAnnotationProperty>(descriptor.properties);
        prop2EditorMap = new HashMap<OWLAnnotationProperty, EditorType>(descriptor.prop2EditorMap);
    }


    public void addRow(OWLAnnotationProperty property, EditorType editorType) {
        if (!properties.contains(property)){
            properties.add(property);
            prop2EditorMap.put(property, editorType);
        }
    }


    private void parse(InputStream stream, OWLDataFactory df) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while(reader.ready()){
            String line = reader.readLine();
            parseLine(line, df);
        }
    }


    private void parseLine(String line, OWLDataFactory df) {
        if (line != null && !"".equals(line) && !line.startsWith("//") && !line.startsWith("#")){
            String[] rawData = line.split(",");
            try {
                IRI iri = IRI.create(new URI(rawData[0].trim()));
                OWLAnnotationProperty property = df.getOWLAnnotationProperty(iri);
                EditorType editorType = EditorType.text; // default is text
                if (rawData.length > 1){
                    try{
                        editorType = EditorType.valueOf(rawData[1].trim());
                    }
                    catch(Exception e){
                        logger.warn("Could not determine editor type from param: " + rawData[1].trim());
                    }
                }
                addRow(property, editorType);
            }
            catch (URISyntaxException e) {
                logger.warn("Could not import template row for annotation: " + rawData[0].trim() + " (not a URI)");
            }
        }
    }


    public void export(PrintStream out) {
        for (OWLAnnotationProperty property : properties){
            out.println(property.getIRI() + ", " + prop2EditorMap.get(property));
        }
    }


    public boolean isEmpty() {
        return properties.isEmpty();
    }


    public List<String> exportStringList() {
        List<String> list = new ArrayList<String>();
        for (OWLAnnotationProperty property : properties){
            list.add(property.getIRI() + ", " + prop2EditorMap.get(property));
        }
        return list;
    }


    public List<OWLAnnotationProperty> getProperties() {
        return Collections.unmodifiableList(properties);
    }


    public EditorType getEditor(OWLAnnotationProperty property){
        return prop2EditorMap.get(property);
    }


    public void changeProperty(OWLAnnotationProperty property, OWLAnnotationProperty property2) {
        int index = properties.indexOf(property);
        if (index >= 0){
            properties.set(index, property2);
            EditorType type = prop2EditorMap.remove(property);
            prop2EditorMap.put(property2, type);
        }
    }


    public void setEditor(OWLAnnotationProperty property, EditorType editorType) {
        prop2EditorMap.put(property, editorType);
    }


    public void move(int start, int end, int to) {
        List<OWLAnnotationProperty> sel = new ArrayList<OWLAnnotationProperty>(properties.subList(start, end+1));
        properties.removeAll(sel);
        properties.addAll(to, sel);
    }


    public void remove(OWLAnnotationProperty property) {
        properties.remove(property);
        prop2EditorMap.remove(property);
    }
}
