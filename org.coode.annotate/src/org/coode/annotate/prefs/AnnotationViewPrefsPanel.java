package org.coode.annotate.prefs;

import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.frame.AnnotationURIList;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
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
 * Date: Apr 1, 2008<br><br>
 */
public class AnnotationViewPrefsPanel extends OWLPreferencesPanel {

    private static final String TEMPLATE_EXT = "template";
    
    private JTable table;
    private JToolBar toolbar;

    private MyTableModel model;

    private boolean dirty = false;

    private Action addAction = new AbstractAction("Add Annotation", OWLIcons.getIcon("property.annotation.add.png")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleAddAnnotation();
        }
    };

    private Action removeAction = new AbstractAction("Remove Annotation", OWLIcons.getIcon("property.annotation.remove.png")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleRemoveAnnotation();
        }
    };

    private Action upAction = new AbstractAction("Move Up", Icons.getIcon("object.move_up.gif")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleMoveUp();
        }
    };

    private Action downAction = new AbstractAction("Move Down", Icons.getIcon("object.move_down.gif")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleMoveDown();
        }

    };

    private Action importAction = new AbstractAction("Import from file", Icons.getIcon("project.open.gif")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleImport();
        }

    };


    private Action exportAction = new AbstractAction("Export to file", Icons.getIcon("project.save.gif")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleExport();
        }

    };

    public void applyChanges() {
        if (dirty){
            AnnotationViewPrefs.getInstance().putValues(getValuesFromTable());
            dirty = false;
        }
    }


    private List<String> getValuesFromTable() {
        List<String> values = new ArrayList<String>();
        for (int i=0; i<model.getRowCount(); i++){
            String str = "";
            for (int j=0; j<model.getColumnCount(); j++){
                if (j>0){
                    str+=",";
                }
                final Object v = model.getValueAt(i, j);
                if (v != null){
                    str+= v;
                }
            }
            values.add(str);
        }
        return values;
    }


    public void initialise() throws Exception {

        setLayout(new BorderLayout());

        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        addToolbarAction(addAction);
        addToolbarAction(removeAction);
        toolbar.addSeparator(new Dimension(6, 6));
        addToolbarAction(upAction);
        addToolbarAction(downAction);
        toolbar.addSeparator(new Dimension(6, 6));
        addToolbarAction(importAction);
        addToolbarAction(exportAction);

        add(toolbar, BorderLayout.NORTH);

        List<String> rows = AnnotationViewPrefs.getInstance().getValues();
        model = new MyTableModel(rows);
        table = new JTable(model);
        table.setShowVerticalLines(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.getColumnModel().getColumn(0).setWidth(200);
        final JScrollPane scroller = new JScrollPane(table);
//        scroller.setPreferredSize(new Dimension(400, 300));
        add(scroller, BorderLayout.CENTER);
    }


    private void addToolbarAction(Action action) {
        JButton button = new JButton(action);
        button.setToolTipText((String)action.getValue(Action.NAME));
        button.setText(null);
        button.setBorder(new EmptyBorder(4, 4, 4, 4));
        toolbar.add(button);
    }


    public void dispose() throws Exception {
        // do nothing
    }

    private void handleAddAnnotation() {
        AnnotationURIList list = new AnnotationURIList(getOWLEditorKit());
        list.rebuildAnnotationURIList();
        final JScrollPane scroller = new JScrollPane(list);
        scroller.setPreferredSize(new Dimension(400, 300));
        if (JOptionPane.showConfirmDialog(AnnotationViewPrefsPanel.this, scroller, "Pick an annotation",
                                          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION){
            Object[] rowData = new Object[]{list.getSelectedURI(), null};
            model.addRow(rowData);
            table.getSelectionModel().setSelectionInterval(model.getRowCount()-1, model.getRowCount()-1);
            dirty = true;
        }
    }


    private void handleRemoveAnnotation() {
        final int row = table.getSelectedRow();
        if (row != -1){
            model.removeRow(row);
            if (row < model.getRowCount()){
                table.getSelectionModel().setSelectionInterval(row, row);
            }
            else if (row-1 > 0){
                table.getSelectionModel().setSelectionInterval(row-1, row-1);
            }
            dirty = true;
        }
    }

    private void handleMoveUp() {
        final int row = table.getSelectedRow();
        if (row > 0){
            model.moveRow(row, row, row-1);
            table.getSelectionModel().setSelectionInterval(row-1, row-1);
            dirty = true;
        }
    }

    private void handleMoveDown() {
        final int row = table.getSelectedRow();
        if (row < model.getRowCount()-1){
            model.moveRow(row, row, row+1);
            table.getSelectionModel().setSelectionInterval(row+1, row+1);
            dirty = true;
        }
    }


    private void handleImport() {
        JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent());
        if (f == null) {
            f = new JFrame();
        }

        File importFile = UIUtil.openFile(f, "Import a template file for your annotations", Collections.EMPTY_SET);
        try {
            FileInputStream inStream = new FileInputStream(importFile);
            List<String> rows = AnnotationViewPrefs.parseStream(inStream);
            model = new MyTableModel(rows);
            table.setModel(model);
            dirty = true;
            inStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleExport() {
        JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent());
        if (f == null) {
            f = new JFrame();
        }

        File importFile = UIUtil.saveFile(f, "Export a template file for your annotations", Collections.EMPTY_SET);
        try {
            PrintStream outStream = new PrintStream(new FileOutputStream(importFile));
            for (String row : getValuesFromTable()){
                outStream.println(row);
            }
            outStream.flush();
            outStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class MyTableModel extends DefaultTableModel {

        private MyTableModel(List<String> rows) {
            addColumn("uri");
            addColumn("params");

            for (String row : rows){
                addRow(row.split(","));
            }
        }
    }
}
