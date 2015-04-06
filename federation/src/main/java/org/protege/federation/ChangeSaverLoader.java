package org.protege.federation;

import org.protege.editor.core.ui.util.UIUtil;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.awt.*;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 26, 2008
 * Time: 5:13:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeSaverLoader {
    protected Component parent;
    protected static final String OPENTITLE = "Load ontology changes";
    protected static final String SAVETITLE = "Save ontology changes to file";
    protected Set<String> extensions = new HashSet<String>(0);  //empty extensions list, so we can name and open and file we like

    public ChangeSaverLoader(JComponent parent) {
        this.parent = parent.getParent();
    }


    public File openFile() {
        FileDialog fileDialog;
        if (parent instanceof Frame) {
            fileDialog = new FileDialog((Frame) parent, OPENTITLE, FileDialog.LOAD);
        } else {
            fileDialog = new FileDialog((Dialog)parent, OPENTITLE, FileDialog.LOAD);
        }
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (extensions.isEmpty()) {
                    return true;
                }
                else {
                    for (String ext : extensions) {
                        if (name.toLowerCase().endsWith(ext.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        });
        fileDialog.setDirectory(UIUtil.getCurrentFileDirectory());
        fileDialog.setVisible(true);
        String fileName = fileDialog.getFile();
        if (fileName != null) {
            UIUtil.setCurrentFileDirectory(fileDialog.getDirectory());
            return new File(fileDialog.getDirectory() + fileName);
        }
        else {
            return null;
        }
    }


    public File saveFile(String initialName) {
        FileDialog fileDialog;
        if (parent instanceof Frame) {
            fileDialog = new FileDialog((Frame) parent, SAVETITLE, FileDialog.SAVE);
        }
        else {
            fileDialog = new FileDialog((Dialog) parent, SAVETITLE, FileDialog.SAVE);
        }
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (extensions.isEmpty()) {
                    return true;
                }
                else {
                    for (String ext : extensions) {
                        if (name.toLowerCase().endsWith(ext.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        });
        fileDialog.setDirectory(UIUtil.getCurrentFileDirectory());
        if (initialName != null) {
            fileDialog.setFile(initialName);
        }
        fileDialog.setVisible(true);

        String fileName = fileDialog.getFile();
        if (fileName != null) {
            UIUtil.setCurrentFileDirectory(fileDialog.getDirectory());
            return new File(fileDialog.getDirectory() + fileName);
        }
        else {
            return null;
        }
    }
}
