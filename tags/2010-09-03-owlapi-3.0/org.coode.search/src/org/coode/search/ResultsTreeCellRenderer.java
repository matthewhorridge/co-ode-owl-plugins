package org.coode.search;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 * Date: Jul 29, 2009<br><br>
 */
public class ResultsTreeCellRenderer extends OWLCellRenderer {

    private List<String> searches = new ArrayList<String>();

    private Map<String, Color> colourMap = new HashMap<String, Color>();

    private Color[] colours = new Color[]{
            Color.yellow,
            Color.green,
            Color.cyan,
            Color.ORANGE,
            Color.PINK
    };

    private int i = 0;

    public ResultsTreeCellRenderer(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
        setPreferredWidth(200);
    }


    public void addSearch(String search){
        searches.add(search);
        colourMap.put(search, getNextColour());
    }


    protected void highlightText(StyledDocument doc) {
        super.highlightText(doc);


        for (String search : searches){

            Style searchHighlight = doc.addStyle("RESULT_" + search, null);
            StyleConstants.setBold(searchHighlight, true);
            StyleConstants.setBackground(searchHighlight, colourMap.get(search));

            Pattern p = Pattern.compile("(?s)(?i)" + search);

            try {
                Matcher matcher = p.matcher(doc.getText(0, doc.getLength()));
                while (matcher.find()){
                    final int start = matcher.start();
                    final int end = matcher.end();
                    doc.setCharacterAttributes(start, end-start, searchHighlight, false);
                }
            }
            catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }

    }


    public void clearSearches() {
        searches.clear();
        colourMap.clear();
        i=0;
    }


    public Color getNextColour() {
        if (i==colours.length){
            return Color.getHSBColor(new Float(Math.random()), 1.0f, new Float(0.5f+(Math.random()*0.5f)));
        }
        return colours[i++];
    }
}
