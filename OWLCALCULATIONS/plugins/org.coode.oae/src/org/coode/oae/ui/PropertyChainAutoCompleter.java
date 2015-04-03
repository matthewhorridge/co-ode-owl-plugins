package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
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
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.AutoCompleterMatcher;
import org.protege.editor.owl.ui.clsdescriptioneditor.AutoCompleterMatcherImpl;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.OWLObject;

import uk.ac.manchester.mae.evaluation.PropertyChainModel;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 4, 2006<br>
 * <br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br>
 * <br>
 */
public class PropertyChainAutoCompleter {
	private static Logger logger = Logger
			.getLogger(PropertyChainAutoCompleter.class);
	public static final int DEFAULT_MAX_ENTRIES = 100;
	private OWLEditorKit owlEditorKit;
	protected JTextComponent textComponent;
	private Set<String> wordDelimeters;
	private AutoCompleterMatcher matcher;
	private JList popupList;
	protected JWindow popupWindow;
	public static final int POPUP_WIDTH = 350;
	public static final int POPUP_HEIGHT = 300;
	private OWLExpressionChecker<PropertyChainModel> checker;
	protected String lastTextUpdate = "*";
	private int maxEntries = DEFAULT_MAX_ENTRIES;
	private KeyListener keyListener = new KeyAdapter() {
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
	};
	protected ComponentAdapter componentListener = new ComponentAdapter() {
		@Override
		public void componentHidden(ComponentEvent event) {
			hidePopup();
		}

		@Override
		public void componentResized(ComponentEvent event) {
			hidePopup();
		}

		@Override
		public void componentMoved(ComponentEvent event) {
			hidePopup();
		}
	};
	private HierarchyListener hierarchyListener = new HierarchyListener() {
		/**
		 * Called when the hierarchy has been changed. To discern the actual
		 * type of change, call <code>HierarchyEvent.getChangeFlags()</code>.
		 * 
		 * @see java.awt.event.HierarchyEvent#getChangeFlags()
		 */
		@Override
        public void hierarchyChanged(HierarchyEvent e) {
			if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
				createPopupWindow();
				Container frame = textComponent
						.getTopLevelAncestor();
				if (frame != null) {
					frame
							.addComponentListener(componentListener);
				}
			}
		}
	};
	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				completeWithPopupSelection();
			}
		}
	};
	private FocusListener focusListener = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent event) {
			hidePopup();
		}
	};

	public PropertyChainAutoCompleter(OWLEditorKit owlEditorKit,
			JTextComponent tc, OWLExpressionChecker<PropertyChainModel> checker) {
		this.owlEditorKit = owlEditorKit;
		this.checker = checker;
		textComponent = tc;
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
		matcher = new AutoCompleterMatcherImpl(owlEditorKit
				.getModelManager());
		popupList = new JList();
		popupList.setAutoscrolls(true);
		popupList.setCellRenderer(owlEditorKit.getWorkspace()
				.createOWLCellRenderer());
		popupList.addMouseListener(mouseListener);
		popupList.setRequestFocusEnabled(false);
		textComponent.addKeyListener(keyListener);
		textComponent.addHierarchyListener(hierarchyListener);
		// moving or resizing the text component or dialog closes the popup
		textComponent.addComponentListener(componentListener);
		// switching focus to another component closes the popup
		textComponent.addFocusListener(focusListener);
		createPopupWindow();
	}

	public void cancel() {
		hidePopup();
	}

	protected void processKeyPressed(KeyEvent e) {
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

	protected void completeWithPopupSelection() {
		if (popupWindow.isVisible()) {
			Object selObject = popupList.getSelectedValue();
			if (selObject != null) {
				insertWord(getInsertText(selObject));
				hidePopup();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected List getMatches() {
		// We need to determine if the matches should be classes, individuals
		// etc.
		try {
			int wordIndex = getWordIndex();
			String expression = textComponent.getDocument().getText(0,
					wordIndex);
			try {
				checker.check(expression);
			} catch (OWLExpressionParserException e) {
				Set<OWLObject> matches = matcher.getMatches("", e
						.isOWLClassExpected(), e.isOWLObjectPropertyExpected(),
						e.isOWLDataPropertyExpected(), false, false);
				List kwMatches = new ArrayList(matches.size() + 10);
				for (String s : e.getExpectedKeyWords()) {
					kwMatches.add(s);
				}
				kwMatches.addAll(matches);
				return kwMatches;
			}
		} catch (BadLocationException e) {
			Logger.getLogger(getClass()).warn(e);
		}
		return Collections.EMPTY_LIST;
	}

	protected void createPopupWindow() {
		JScrollPane sp = ComponentFactory.createScrollPane(popupList);
		popupWindow = new JWindow((Window) SwingUtilities
				.getAncestorOfClass(Window.class, textComponent));
		// popupWindow.setAlwaysOnTop(true); // this doesn't appear to work with
		// certain Windows/java combinations
		popupWindow.getContentPane().setLayout(new BorderLayout());
		popupWindow.getContentPane().add(sp, BorderLayout.CENTER);
		popupWindow.setFocusableWindowState(false);
	}

	@SuppressWarnings("unchecked")
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
			// remove any currently selected text - this is the default
			// behaviour
			// of the editor when typing manually
			int selStart = textComponent.getSelectionStart();
			int selEnd = textComponent.getSelectionEnd();
			int selLen = selEnd - selStart;
			if (selLen > 0) {
				textComponent.getDocument().remove(selStart, selLen);
			}
			int index = getWordIndex();
			int caretIndex = textComponent.getCaretPosition();
			if (caretIndex > 0 && caretIndex > index) {
				textComponent.getDocument().remove(index,
						caretIndex - index);
			}
			textComponent.getDocument().insertString(index, word, null);
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
				Point p = new Point(0, 0); // default for when the doc is empty
				if (wordIndex > 0) {
					p = textComponent.modelToView(wordIndex).getLocation();
				}
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

	protected void hidePopup() {
		popupWindow.setVisible(false);
		popupList.setListData(new Object[0]);
	}

	@SuppressWarnings("unchecked")
	protected void updatePopup(List matches) {
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
		int index = getEscapedWordIndex();
		if (index == -1) {
			index = getUnbrokenWordIndex();
		}
		return Math.max(0, index);
	}

	// determines if we are currently inside an escaped name (if there are an
	// uneven number of escape characters)
	private int getEscapedWordIndex() {
		try {
			int caretPos = Math.max(0, getEffectiveCaretPosition() - 1);
			String expression = textComponent.getDocument().getText(0,
					caretPos);
			int escapeEnd = -1;
			do {
				int escapeStart = expression.indexOf("'", escapeEnd + 1);
				if (escapeStart != -1) {
					escapeEnd = expression.indexOf("'", escapeStart + 1);
					if (escapeEnd == -1) {
						return escapeStart;
					}
				} else {
					return -1;
				}
			} while (true);
		} catch (BadLocationException e) {
			logger.error(e);
		}
		return -1;
	}

	private int getUnbrokenWordIndex() {
		try {
			int caretPos = Math.max(0, getEffectiveCaretPosition() - 1);
			if (caretPos > 0) {
				for (int index = caretPos; index > -1; index--) {
					if (wordDelimeters.contains(textComponent
							.getDocument().getText(index, 1))) {
						return index + 1;
					}
					if (index == 0) {
						return 0;
					}
				}
			}
		} catch (BadLocationException e) {
			logger.error(e);
		}
		return -1;
	}

	private String getInsertText(Object o) {
		if (o instanceof OWLObject) {
			OWLModelManager mngr = owlEditorKit.getModelManager();
			return mngr.getRendering((OWLObject) o);
		} else {
			return o.toString();
		}
	}

	// private String getWordToComplete() {
	// try {
	// int index = getWordIndex();
	// int caretIndex = getEffectiveCaretPosition();
	// return this.textComponent.getDocument().getText(index,
	// caretIndex - index);
	// } catch (BadLocationException e) {
	// return "";
	// }
	// }
	// the caret pos should be read as the start of the selection if there is
	// one
	private int getEffectiveCaretPosition() {
		int startSel = textComponent.getSelectionStart();
		if (startSel >= 0) {
			return startSel;
		}
		return textComponent.getCaretPosition();
	}

	public void uninstall() {
		hidePopup();
		textComponent.removeKeyListener(keyListener);
		textComponent.removeComponentListener(componentListener);
		textComponent.removeFocusListener(focusListener);
		textComponent.removeHierarchyListener(hierarchyListener);
	}
}
