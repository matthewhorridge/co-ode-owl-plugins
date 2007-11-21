package org.coode.pattern.owllist;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.AutoCompleterMatcher;
import org.protege.editor.owl.ui.clsdescriptioneditor.AutoCompleterMatcherImpl;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObject;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParser;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 8, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public abstract class OWLAutoCompleter {


    private static Logger logger = Logger.getLogger(OWLAutoCompleter.class);

    private OWLModelManager owlModelManager;

    private JTextComponent textComponent;

    private KeyListener keyListener;

    private Set<String> wordDelimeters;

    private AutoCompleterMatcher matcher;

    private JList popupList;

    private JWindow popupWindow;

    public static final int POPUP_WIDTH = 350;

    public static final int POPUP_HEIGHT = 300;

    protected abstract ListExpressionParser getParser();

    public OWLAutoCompleter(OWLEditorKit eKit, JTextComponent textComponent) {
        this.owlModelManager = eKit.getOWLModelManager();
        this.textComponent = textComponent;
        keyListener = new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                processKeyPressed(e);
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP == false &&
                    e.getKeyCode() == KeyEvent.VK_DOWN == false) {
                    if (popupWindow.isVisible()) {
                        updatePopup(getMatches());
                    }
                }
            }
        };
        textComponent.addKeyListener(keyListener);
        wordDelimeters = new HashSet<String>();
        wordDelimeters.add(" ");
        wordDelimeters.add("\n");
        wordDelimeters.add("[");
        wordDelimeters.add("]");
        wordDelimeters.add("{");
        wordDelimeters.add("}");
        wordDelimeters.add("(");
        wordDelimeters.add(")");
        wordDelimeters.add(",");
        matcher = new AutoCompleterMatcherImpl(owlModelManager);
        popupList = new JList();
        popupList.setAutoscrolls(true);
        popupList.setCellRenderer(new OWLCellRenderer(eKit));
        popupList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    completeWithPopupSelection();
                }
            }
        });
        JScrollPane sp = ComponentFactory.createScrollPane(popupList);
        popupWindow = new JWindow();
        popupWindow.getContentPane().setLayout(new BorderLayout());
        popupWindow.getContentPane().add(sp, BorderLayout.CENTER);
    }


    private void processKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && e.isControlDown()) {
            // Show popup
            performAutoCompletion();
        }
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (popupWindow.isVisible()) {
                // Hide popup
                e.consume();
                hidePopup();
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (popupWindow.isVisible()) {
                // Complete
                e.consume();
                completeWithPopupSelection();
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            e.consume();
            incrementSelection();
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
            e.consume();
            decrementSelection();
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            hidePopup();
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            hidePopup();
        }
    }

    private void completeWithPopupSelection() {
        if (popupWindow.isVisible()) {
            Object selObject = popupList.getSelectedValue();
            if (selObject != null) {
                if (selObject instanceof OWLEntity) {
                    insertWord(getInsertText(selObject));
                }
                else {
                    insertWord(getInsertText(selObject));
                }
                hidePopup();
            }
        }
    }

    private Collection getMatches() {
        // We need to determine if the matches should be classes, individuals etc.

        int wordIndex = getWordIndex();
        if (wordIndex > -1) {
            try {
                String expression = textComponent.getDocument().getText(0, wordIndex);
                // Add a bit to the end to force a parse error
                expression += "**";
                ListExpressionParser parser = getParser();
                try {
                    parser.isWellFormed(expression);
                }
                catch (OWLExpressionParserException e) {
                    String word = getWordToComplete();
                    Set matches = matcher.getMatches(word,
                                                     e.isOWLClassExpected(),
                                                     e.isOWLObjectPropertyExpected(),
                                                     e.isOWLDataPropertyExpected(),
                                                     e.isOWLIndividualExpected(),
                                                     e.isDataTypeExpected());
                    java.util.List kwMatches = new ArrayList(matches.size() + 10);
                    if (e.getExpectedKeyWords() != null){
                    for (String s : e.getExpectedKeyWords()) {
                        if (s.toLowerCase().startsWith(word.toLowerCase())) {
                            kwMatches.add(s);
                        }
                    }
                    }
                    kwMatches.addAll(matches);
                    return kwMatches;
                }
                catch (OWLException owlEx) {
                    owlEx.printStackTrace();
                }
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_SET;

    }


    private void performAutoCompletion() {
        Collection matches = getMatches();
        if (matches.size() == 1) {
            // Don't show popup
            insertWord(getInsertText(matches.iterator().next()));
        }
        else {
            // Show popup
            showPopup();
            updatePopup(getMatches());
        }
    }

    private void insertWord(String word) {
        try {
            int index = getWordIndex();
            int caretIndex = textComponent.getCaretPosition();
            textComponent.getDocument().remove(index, caretIndex - index);
            textComponent.getDocument().insertString(index, word, null);
        }
        catch (BadLocationException e) {
            logger.error(e);
        }
    }

    private void showPopup() {
        System.out.println("OWLAutoCompleter.showPopup");
        if (popupWindow.isVisible() == false) {
            System.out.println("SHOWING...");
            popupWindow.setSize(POPUP_WIDTH, POPUP_HEIGHT);
            try {
                int wordIndex = getWordIndex();
                if (wordIndex < 0) {
                    return;
                }
                Point p = textComponent.modelToView(getWordIndex()).getLocation();
                SwingUtilities.convertPointToScreen(p, textComponent);
                p.y = p.y + textComponent.getHeight();
                popupWindow.setLocation(p);
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
            popupWindow.setVisible(true);
        }
    }

    private void hidePopup() {
        popupWindow.setVisible(false);
        popupList.setListData(new Object [0]);
    }

    private void updatePopup(Collection matches) {
        popupList.setListData(matches.toArray());
        popupList.setSelectedIndex(0);

        popupWindow.setSize(POPUP_WIDTH, POPUP_HEIGHT);
    }

    private void incrementSelection() {
        if (popupList.getModel().getSize() > 0) {
            int selIndex = popupList.getSelectedIndex();
            selIndex++;
            if (selIndex > popupList.getModel().getSize() - 1) {
                selIndex = 0;
            }
            popupList.setSelectedIndex(selIndex);
            popupList.scrollRectToVisible(popupList.getCellBounds(selIndex, selIndex));
        }
    }

    private void decrementSelection() {
        if (popupList.getModel().getSize() > 0) {
            int selIndex = popupList.getSelectedIndex();
            selIndex--;
            if (selIndex < 0) {
                selIndex = popupList.getModel().getSize() - 1;
            }
            popupList.setSelectedIndex(selIndex);
            popupList.scrollRectToVisible(popupList.getCellBounds(selIndex, selIndex));
        }
    }

    private int getWordIndex() {
        try {
            int caretPos = textComponent.getCaretPosition() - 1;
            for (int index = caretPos; index > -1; index--) {
                if (wordDelimeters.contains(textComponent.getDocument().getText(index, 1))) {
                    return index + 1;
                }
                if (index == 0) {
                    return 0;
                }
            }

        }
        catch (BadLocationException e) {
            logger.error(e);
        }
        return -1;
    }

    private String getInsertText(Object o) {
        if (o instanceof OWLObject) {
            return owlModelManager.getOWLObjectRenderer().render((OWLObject) o, owlModelManager.getOWLEntityRenderer());
        }
        else {
            return o.toString();
        }
    }

    private String getWordToComplete() {
        try {
            int index = getWordIndex();
            int caretIndex = textComponent.getCaretPosition();
            return textComponent.getDocument().getText(index, caretIndex - index);
        }
        catch (BadLocationException e) {
            return "";
        }
    }

}

