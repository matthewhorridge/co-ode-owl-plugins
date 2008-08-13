package org.coode.annotate.prefs;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * Date: Apr 2, 2008<br><br>
 */
public class AnnotationTemplatePrefs {

    private static final String PROPS_FILENAME = "resources/default.template";
    private static final String ANNOTATE_PREFS = "org.coode.annotate";
    private static final String TEMPLATE = "template";

    private static AnnotationTemplatePrefs instance;

    private static final String ANNOTATION_VIEW_PREFERENCES_PANEL = "AnnotationViewPreferencesPanel";

    // really need an ordered Map, but for now, maintain a separate list
    private List<URI> uriList;

    private Map<URI, Set<String>> templateMap;


    public static AnnotationTemplatePrefs getInstance(){
        if (instance == null){
            instance = new AnnotationTemplatePrefs();
        }
        return instance;
    }

    public List<String> getValues() {
        Preferences prefs = PreferencesManager.getInstance().getPreferencesForSet(ANNOTATION_VIEW_PREFERENCES_PANEL, ANNOTATE_PREFS);
        java.util.List<String> template = new ArrayList<String>();
        template = prefs.getStringList(TEMPLATE, template);
        if (template.isEmpty()){
            InputStream stream = getClass().getClassLoader().getResourceAsStream(PROPS_FILENAME);
            try {
                template = parseStream(stream);
                putValues(template);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return template;
    }


    public void putValues(List<String> values){
        Preferences prefs = PreferencesManager.getInstance().getPreferencesForSet(ANNOTATION_VIEW_PREFERENCES_PANEL, ANNOTATE_PREFS);
        prefs.putStringList(TEMPLATE, values);
        clear();
    }


    private void clear() {
        templateMap = null;
        uriList = null;
    }


    public static List<String> parseStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        List<String> list = new ArrayList<String>();
        while(reader.ready()){
            String line = reader.readLine();
            if (line != null && !"".equals(line) && !line.startsWith("//") && !line.startsWith("#")){
                list.add(line);
            }
        }
        return list;
    }


    private void refresh() {
        final List<String> lines = getValues(); // must get this first as it may reset the templateMap
        templateMap = new HashMap<URI, Set<String>>();
        uriList = new ArrayList<URI>();
        for (String line : lines){
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
                e.printStackTrace();
            }
        }
    }

//    public void importFromStream(InputStream stream) throws IOException {
//        List<String> list = parseStream(stream);
//        putValues(list);
//    }
//
//    public void exportToStream(PrintStream stream){
//        for (String value : getValues()){
//            stream.println(value);
//        }
//    }

    public Set<String> getParams(URI uri) {
        if (templateMap == null){
            refresh();
        }
        return templateMap.get(uri);
    }


    public List<URI> getURIList() {
        if (uriList == null){
            refresh();
        }
        return uriList;
    }
}
