package org.coode.annotate.prefs;

import org.apache.log4j.Logger;
import org.coode.annotate.EditorType;
import org.protege.editor.core.prefs.Preferences;

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


    private List<URI> uris = new ArrayList<URI>();

    private Map<URI, EditorType> uri2EditorMap = new HashMap<URI, EditorType>();


    public AnnotationTemplateDescriptor(InputStream stream) throws IOException {
        parse(stream);
    }


    public AnnotationTemplateDescriptor(Preferences prefs, String key) {
        for (String line : prefs.getStringList(key, new ArrayList<String>())){
            parseLine(line);
        }
    }


    public AnnotationTemplateDescriptor(AnnotationTemplateDescriptor descriptor) {
        uris = new ArrayList<URI>(descriptor.uris);
        uri2EditorMap = new HashMap<URI, EditorType>(descriptor.uri2EditorMap);
    }


    public void addRow(URI uri, EditorType editorType) {
        if (!uris.contains(uri)){
            uris.add(uri);
            uri2EditorMap.put(uri, editorType);
        }
    }


    private void parse(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while(reader.ready()){
            String line = reader.readLine();
            parseLine(line);
        }
    }


    private void parseLine(String line) {
        if (line != null && !"".equals(line) && !line.startsWith("//") && !line.startsWith("#")){
            String[] rawData = line.split(",");
            try {
                URI uri = new URI(rawData[0].trim());
                EditorType editorType = EditorType.text; // default is text
                if (rawData.length > 1){
                    try{
                        editorType = EditorType.valueOf(rawData[1].trim());
                    }
                    catch(Exception e){
                        logger.warn("Could not determine editor type from param: " + rawData[1].trim());
                    }
                }
                addRow(uri, editorType);
            }
            catch (URISyntaxException e) {
                logger.warn("Could not import template row for annotation: " + rawData[0].trim() + " (not a URI)");
            }
        }
    }


    public void export(PrintStream out) {
        for (URI uri : uris){
            out.println(uri + ", " + uri2EditorMap.get(uri));
        }
    }


    public boolean isEmpty() {
        return uris.isEmpty();
    }


    public List<String> exportStringList() {
        List<String> list = new ArrayList<String>();
        for (URI uri : uris){
            list.add(uri + ", " + uri2EditorMap.get(uri));
        }
        return list;
    }


    public List<URI> getURIs() {
        return Collections.unmodifiableList(uris);
    }


    public EditorType getEditor(URI uri){
        return uri2EditorMap.get(uri);
    }


    public void changeURI(URI uri, URI uri2) {
        int index = uris.indexOf(uri);
        if (index >= 0){
            uris.set(index, uri2);
            EditorType type = uri2EditorMap.remove(uri);
            uri2EditorMap.put(uri2, type);
        }
    }


    public void setEditor(URI uri, EditorType editorType) {
        uri2EditorMap.put(uri, editorType);
    }


    public void move(int start, int end, int to) {
        List<URI> sel = new ArrayList<URI>(uris.subList(start, end+1));
        uris.removeAll(sel);
        uris.addAll(to, sel);
    }


    public void remove(URI uri) {
        uris.remove(uri);
        uri2EditorMap.remove(uri);
    }
}
