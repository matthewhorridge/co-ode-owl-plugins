package org.coode.pattern.ui;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternEditor;
import org.coode.pattern.impl.AbstractPatternDescriptor;
import org.coode.pattern.impl.AbstractPatternEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
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
 * Date: Nov 16, 2007<br><br>
 */
public class PatternEditorKit {

    private static Map<OWLEditorKit, PatternEditorKit> factoryMap = new HashMap<OWLEditorKit, PatternEditorKit>();

    public static PatternEditorKit getPatternEditorKit(OWLEditorKit editorKit) {

        PatternEditorKit instance = factoryMap.get(editorKit);

        if (instance == null){
            instance = new PatternEditorKit(editorKit);
            factoryMap.put(editorKit, instance);
        }

        return instance;
    }

    private AbstractPatternRenderer defaultRenderer;
    private AbstractPatternEditor defaultEditor;

    private final OWLEditorKit eKit;
    private Map<PatternDescriptor, AbstractPatternRenderer> renderers = new HashMap<PatternDescriptor, AbstractPatternRenderer>();
//    private Map<PatternDescriptor, AbstractPatternEditor> editors = new HashMap<PatternDescriptor, AbstractPatternEditor>();

    public PatternEditorKit(OWLEditorKit editorKit) {
        this.eKit = editorKit;

        if (defaultRenderer == null){
            defaultRenderer = new BaseEntityPatternRenderer();
        }

        if (defaultEditor == null){
            defaultEditor = new EntityEditor();
        }
    }

    public PatternEditor getEditor(PatternDescriptor descriptor){
        AbstractPatternEditor editor;
        editor = ((AbstractPatternDescriptor)descriptor).getEditor();
        if (editor == null){
            editor = defaultEditor;
        }
        editor.init(eKit, descriptor);

        return editor;
    }

    public PatternRenderer getRenderer(PatternDescriptor descriptor){
        AbstractPatternRenderer renderer;
        if (renderers.containsKey(descriptor)){
            renderer = renderers.get(descriptor);
        }
        else{
            renderer = ((AbstractPatternDescriptor)descriptor).getRenderer();
            if (renderer == null){
                renderer = defaultRenderer;
            }
            renderer.init(eKit);
            renderers.put(descriptor, renderer);
        }
        return renderer;
    }

    public JComponent getPropertiesEditor(PatternDescriptor patternDescr) {
        return null; // @@TODO implement
    }

    public OWLEntityRenderer getOWLEntityRenderer(){
        return eKit.getModelManager().getOWLEntityRenderer();
    }

    class BaseEntityPatternRenderer extends AbstractPatternRenderer{
        public String render(Pattern pattern) {
            if (pattern.getBase() != null){
                return eKit.getModelManager().getRendering(pattern.getBase());
            }
            else{
                return pattern.toString();
            }
        }
    }
}
