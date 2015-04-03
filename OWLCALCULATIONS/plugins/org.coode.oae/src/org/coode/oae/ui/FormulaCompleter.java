/**
 * Copyright (C) 2008, University of Manchester
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
package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.coode.oae.utils.ParserFactory;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLObject;

import uk.ac.manchester.mae.parser.ArithmeticsParser;
import uk.ac.manchester.mae.parser.ParseException;

/**
 * @author Luigi Iannone
 * 
 *         Jul 2, 2008
 */
@SuppressWarnings("unchecked")
public class FormulaCompleter {
	protected final class SpecializedKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			processKeyPressed(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() != KeyEvent.VK_UP
					&& e.getKeyCode() != KeyEvent.VK_DOWN) {
				if (popupWindow.isVisible()
						&& !lastTextUpdate
								.equals(textComponent
										.getText())) {
					lastTextUpdate = textComponent
							.getText();
					updatePopup(getMatches());
				}
			}
		}
	}

    private static Logger logger = Logger.getLogger(FormulaCompleter.class);
	public static final int DEFAULT_MAX_ENTRIES = 100;
	private OWLEditorKit owlEditorKit;
	protected JTextComponent textComponent;
	private KeyListener keyListener;
	private Set<String> wordDelimeters;
	// private AutoCompleterMatcher matcher;
	private JList popupList;
	protected JWindow popupWindow;
	public static final int POPUP_WIDTH = 350;
	public static final int POPUP_HEIGHT = 300;
	protected String lastTextUpdate = "*";
	private int maxEntries = DEFAULT_MAX_ENTRIES;

	public FormulaCompleter(OWLEditorKit owlEditorKit, JTextComponent tc,
			OWLExpressionChecker checker) {
		this.owlEditorKit = owlEditorKit;
		textComponent = tc;
		keyListener = new SpecializedKeyAdapter();
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
		wordDelimeters.add("^");
		// this.matcher = new AutoCompleterMatcherImpl(owlEditorKit
		// .getOWLModelManager());
		popupList = new JList();
		popupList.setAutoscrolls(true);
		popupList.setCellRenderer(owlEditorKit.getWorkspace()
				.createOWLCellRenderer());
		popupList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					FormulaCompleter.this.completeWithPopupSelection();
				}
			}
		});
		popupList.setRequestFocusEnabled(false);
		createPopupWindow();
		textComponent.addHierarchyListener(new HierarchyListener() {
			/**
			 * Called when the hierarchy has been changed. To discern the actual
			 * type of change, call <code>HierarchyEvent.getChangeFlags()</code>
			 * .
			 * 
			 * @see java.awt.event.HierarchyEvent#getChangeFlags()
			 */
			@Override
            public void hierarchyChanged(HierarchyEvent e) {
				if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
					FormulaCompleter.this.createPopupWindow();
				}
			}
		});
	}

	protected final void processKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE && e.isControlDown()) {
			// Show popup
			performAutoCompletion();
		} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
			e.consume();
			performAutoCompletion();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (popupWindow.isVisible()) {
				// Hide popup
				e.consume();
				hidePopup();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (popupWindow.isVisible()) {
				// Complete
				e.consume();
				completeWithPopupSelection();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (popupWindow.isVisible()) {
				e.consume();
				incrementSelection();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (popupWindow.isVisible()) {
				e.consume();
				decrementSelection();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			hidePopup();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			hidePopup();
		}
	}

	protected final void completeWithPopupSelection() {
		if (popupWindow.isVisible()) {
			Object selObject = popupList.getSelectedValue();
			if (selObject != null) {
				insertWord(getInsertText(selObject));
				hidePopup();
			}
		}
	}

	protected final List getMatches() {
		ParserFactory.initParser(textComponent.getText(),
				owlEditorKit.getModelManager());
		List completions;
		try {
			ArithmeticsParser.Start();
		} catch (ParseException e) {
		} finally {
			completions = ArithmeticsParser.getCompletions();
		}
		List toReturn = new ArrayList(completions);
		String wordToComplete = getWordToComplete().replaceAll("[<>{}]",
				"");
		wordToComplete = wordToComplete.replaceAll("(\\S)*!", "");
		if (wordToComplete.length() > 0) {
			for (Object object : completions) {
				if (!object.toString().trim().startsWith(wordToComplete)) {
					toReturn.remove(object);
				}
			}
		}
		return toReturn;
	}

	protected final void createPopupWindow() {
		JScrollPane sp = ComponentFactory.createScrollPane(popupList);
		popupWindow = new JWindow((Window) SwingUtilities
				.getAncestorOfClass(Window.class, textComponent));
		popupWindow.setAlwaysOnTop(true);
		popupWindow.getContentPane().setLayout(new BorderLayout());
		popupWindow.getContentPane().add(sp, BorderLayout.CENTER);
		popupWindow.setFocusableWindowState(false);
	}

	private void performAutoCompletion() {
		List matches = getMatches();
		if (matches.size() == 1) {
			// Don't show popup
			insertWord(getInsertText(matches.iterator().next()));
		} else if (matches.size() > 1) {
			// Show popup
			lastTextUpdate = textComponent.getText();
			showPopup();
			updatePopup(matches);
		}
	}

	private void insertWord(String word) {
		try {
			String wordToComplete = getWordToComplete().replaceAll(
					"[<>{}]", "");
			wordToComplete = wordToComplete.replaceAll("(\\S)*!", "");
			int prefixIndex = getWordToComplete().indexOf(wordToComplete);
			String prefix = prefixIndex > 0 ? getWordToComplete()
					.substring(0, prefixIndex) : "";
			if (wordToComplete.length() > 0 && word.startsWith(wordToComplete)) {
				int index = getWordIndex();
				int caretIndex = textComponent.getCaretPosition();
				textComponent.getDocument().remove(index,
						caretIndex - index);
				textComponent.getDocument().insertString(index,
						prefix + word, null);
			} else {
				textComponent.getDocument().insertString(
						textComponent.getText().length(), word, null);
			}
		} catch (BadLocationException e) {
			logger.error(e);
		}
	}

	private void showPopup() {
		if (popupWindow == null) {
			createPopupWindow();
		}
		if (!popupWindow.isVisible()) {
			popupWindow.setSize(POPUP_WIDTH, POPUP_HEIGHT);
			try {
				int wordIndex = getWordIndex();
				if (wordIndex < 0) {
					wordIndex = 0;
				}
				Point p = textComponent.modelToView(wordIndex)
						.getLocation();
				SwingUtilities.convertPointToScreen(p, textComponent);
				p.y = p.y
						+ textComponent.getFontMetrics(
								textComponent.getFont()).getHeight();
				popupWindow.setLocation(p);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			popupWindow.setVisible(true);
		}
	}

	private void hidePopup() {
		popupWindow.setVisible(false);
		popupList.setListData(new Object[0]);
	}

	protected final void updatePopup(List matches) {
		int count = matches.size();
		if (count > maxEntries) {
			count = maxEntries;
		}
		if (!matches.isEmpty()) {
			popupList.setListData(matches.subList(0, count).toArray());
		} else {
			popupList.setListData(matches.toArray());
		}
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
			popupList.scrollRectToVisible(popupList.getCellBounds(
					selIndex, selIndex));
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
			popupList.scrollRectToVisible(popupList.getCellBounds(
					selIndex, selIndex));
		}
	}

	private int getWordIndex() {
		try {
			int caretPos = textComponent.getCaretPosition() - 1;
			for (int index = caretPos; index > -1; index--) {
				if (wordDelimeters.contains(textComponent
						.getDocument().getText(index, 1))) {
					return index + 1;
				}
				if (index == 0) {
					return 0;
				}
			}
		} catch (BadLocationException e) {
			logger.error(e);
		}
		return -1;
	}

	private String getInsertText(Object o) {
		if (o instanceof OWLObject) {
			OWLModelManager owlModelManager = owlEditorKit
					.getModelManager();
            return owlModelManager.getOWLObjectRenderer().render((OWLObject) o);
		} else {
			return o.toString();
		}
	}

	private String getWordToComplete() {
		try {
			int index = getWordIndex();
			int caretIndex = textComponent.getCaretPosition();
			String toReturn = textComponent.getDocument().getText(index,
					caretIndex - index);
			return toReturn;
		} catch (BadLocationException e) {
			return "";
		}
	}

	public void uninstall() {
		textComponent.removeKeyListener(keyListener);
	}
}
