package org.coode.annotate.prefs;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

    private AnnotationTemplateDescriptor descriptor;

    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();


    public static AnnotationTemplatePrefs getInstance(){
        if (instance == null){
            instance = new AnnotationTemplatePrefs();
        }
        return instance;
    }

    
    public AnnotationTemplateDescriptor getDefaultDescriptor(OWLDataFactory df) {
        if (descriptor == null){
            Preferences prefs = PreferencesManager.getInstance().getPreferencesForSet(ANNOTATION_VIEW_PREFERENCES_PANEL, ANNOTATE_PREFS);

            descriptor = new AnnotationTemplateDescriptor(prefs, TEMPLATE, df);

            if (descriptor.isEmpty()){
                InputStream stream = getClass().getClassLoader().getResourceAsStream(PROPS_FILENAME);
                try {
                    descriptor = new AnnotationTemplateDescriptor(stream, df);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return descriptor;
    }


    public void setDefaultDescriptor(AnnotationTemplateDescriptor descriptor){
        Preferences prefs = PreferencesManager.getInstance().getPreferencesForSet(ANNOTATION_VIEW_PREFERENCES_PANEL, ANNOTATE_PREFS);
        prefs.putStringList(TEMPLATE, descriptor.exportStringList());
        this.descriptor = descriptor;

        final ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener l : listeners){
            l.stateChanged(event);
        }
    }


    public void addChangeListener(ChangeListener l){
        listeners.add(l);
    }


    public void removeChangeListener(ChangeListener l){
        listeners.remove(l);
    }
}
