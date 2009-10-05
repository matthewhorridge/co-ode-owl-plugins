package org.coode.oae.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.tree.TreeCellRenderer;

import org.apache.log4j.Logger;
import org.coode.oae.ui.StaticListModel.StaticListItem;
import org.coode.oae.ui.VariableListModel.VariableListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.OWLEntityColorProvider;
import org.protege.editor.owl.ui.renderer.OWLEntityColorProviderPlugin;
import org.protege.editor.owl.ui.renderer.OWLEntityColorProviderPluginLoader;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.PropertyChainCell;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;

public class RenderableObjectCellRenderer implements TableCellRenderer,
		TreeCellRenderer, ListCellRenderer {
	private static final Logger logger = Logger
			.getLogger(RenderableObjectCellRenderer.class);
	private boolean forceReadOnlyRendering;
	private OWLEditorKit owlEditorKit;
	private boolean renderIcon;
	private boolean renderExpression;
	private boolean strikeThrough;
	protected OWLOntology ontology;
	private Set<OWLObject> equivalentObjects;
	private LinkedObjectComponent linkedObjectComponent;
	private Font plainFont;
	private Font boldFont;
	public static final Color SELECTION_BACKGROUND = UIManager.getDefaults()
			.getColor("List.selectionBackground");
	public static final Color SELECTION_FOREGROUND = UIManager.getDefaults()
			.getColor("List.selectionForeground");
	public static final Color FOREGROUND = UIManager.getDefaults().getColor(
			"List.foreground");
	private boolean gettingCellBounds;
	private List<OWLEntityColorProvider> entityColorProviders;
	// The object that determines which icon should be displayed.
	private OWLObject iconObject;
	// private int leftMargin = 0;
	private int rightMargin = 40;
	protected JComponent componentBeingRendered;
	protected JPanel renderingComponent;
	protected JLabel iconLabel;
	protected JTextPane textPane;
	protected int preferredWidth;
	protected int minTextHeight;
	private OWLEntity focusedEntity;
	private boolean commentedOut;
	private boolean inferred;
	private boolean highlightKeywords;
	private boolean wrap = true;
	private boolean highlightUnsatisfiableClasses = true;
	private boolean highlightUnsatisfiableProperties = true;
	private Set<OWLEntity> crossedOutEntities;
	private boolean focusedEntityIsSelectedEntity;
	private Set<String> unsatisfiableNames;
	private Set<String> boxedNames;
	private Set<String> annotationURINames;
	private int plainFontHeight;
	private boolean opaque = false;

	public RenderableObjectCellRenderer(OWLEditorKit owlEditorKit) {
		this(owlEditorKit, true, true);
	}

	public RenderableObjectCellRenderer(OWLEditorKit owlEditorKit,
			boolean renderExpression, boolean renderIcon) {
		this.owlEditorKit = owlEditorKit;
		this.renderExpression = renderExpression;
		this.renderIcon = renderIcon;
		this.equivalentObjects = new HashSet<OWLObject>();
		this.iconLabel = new JLabel("");
		this.iconLabel.setOpaque(false);
		this.iconLabel.setVerticalAlignment(SwingConstants.CENTER);
		this.textPane = new JTextPane();
		this.textPane.setOpaque(false);
		this.renderingComponent = new JPanel(new OWLCellRendererLayoutManager());
		this.renderingComponent.add(this.iconLabel);
		this.renderingComponent.add(this.textPane);
		this.entityColorProviders = new ArrayList<OWLEntityColorProvider>();
		OWLEntityColorProviderPluginLoader loader = new OWLEntityColorProviderPluginLoader(
				getOWLModelManager());
		for (OWLEntityColorProviderPlugin plugin : loader.getPlugins()) {
			try {
				OWLEntityColorProvider prov = plugin.newInstance();
				prov.initialise();
				this.entityColorProviders.add(prov);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		this.crossedOutEntities = new HashSet<OWLEntity>();
		this.unsatisfiableNames = new HashSet<String>();
		this.boxedNames = new HashSet<String>();
		prepareStyles();
		setupFont();
	}

	/**
	 * @param owlEditorKit
	 *            The editor kit
	 * @param renderExpression
	 *            determines if values are rendered as expressions (i.e. whether
	 *            key words are highlighted or not)
	 * @param renderIcon
	 *            Determines if an icon is shown
	 * @param indentation
	 *            Legacy - has no effect
	 * @deprecated Use OWLCellRenderer(OWLEditorKit, renderExpression,
	 *             renderIcon) Creates a cell renderer
	 */
	@Deprecated
	public RenderableObjectCellRenderer(OWLEditorKit owlEditorKit,
			boolean renderExpression, boolean renderIcon, int indentation) {
		this(owlEditorKit, renderExpression, renderIcon);
	}

	public void setForceReadOnlyRendering(boolean forceReadOnlyRendering) {
		this.forceReadOnlyRendering = forceReadOnlyRendering;
	}

	/**
	 * @deprecated use <code>setOpaque</code>
	 */
	@Deprecated
	public void setTransparent() {
		this.renderingComponent.setOpaque(false);
	}

	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}

	public void setUnsatisfiableNames(Set<String> unsatisfiableNames) {
		this.unsatisfiableNames.clear();
		this.unsatisfiableNames.addAll(unsatisfiableNames);
	}

	public void setHighlightKeywords(boolean hightlighKeywords) {
		this.highlightKeywords = hightlighKeywords;
	}

	public void setHighlightUnsatisfiableClasses(
			boolean highlightUnsatisfiableClasses) {
		this.highlightUnsatisfiableClasses = highlightUnsatisfiableClasses;
	}

	public void setHighlightUnsatisfiableProperties(
			boolean highlightUnsatisfiableProperties) {
		this.highlightUnsatisfiableProperties = highlightUnsatisfiableProperties;
	}

	public void setFocusedEntityIsSelectedEntity(
			boolean focusedEntityIsSelectedEntity) {
		this.focusedEntityIsSelectedEntity = focusedEntityIsSelectedEntity;
	}

	public void setOntology(OWLOntology ont) {
		this.forceReadOnlyRendering = false;
		this.ontology = ont;
	}

	public void setIconObject(OWLObject object) {
		this.iconObject = object;
	}

	public void setCrossedOutEntities(Set<OWLEntity> entities) {
		this.crossedOutEntities.addAll(entities);
	}

	public void addBoxedName(String name) {
		this.boxedNames.add(name);
	}

	public boolean isBoxedName(String name) {
		return this.boxedNames.contains(name);
	}

	public void reset() {
		this.iconObject = null;
		this.rightMargin = 0;
		this.ontology = null;
		this.focusedEntity = null;
		this.commentedOut = false;
		this.inferred = false;
		this.strikeThrough = false;
		this.highlightUnsatisfiableClasses = true;
		this.highlightUnsatisfiableProperties = true;
		this.crossedOutEntities.clear();
		this.focusedEntityIsSelectedEntity = false;
		this.unsatisfiableNames.clear();
		this.boxedNames.clear();
		this.annotationURINames = null;
	}

	public void setFocusedEntity(OWLEntity entity) {
		this.focusedEntity = entity;
	}

	/**
	 * Sets equivalent objects for the object being rendered. For example, if
	 * the object being rendered is A, and B and C are equivalent to A, then
	 * setting the equivalent objects to {B, C} will cause the rendering to have
	 * (= B = C) appended to it
	 * 
	 * @param objects
	 *            The objects that are equivalent to the object being rendered
	 */
	public void setEquivalentObjects(Set<OWLObject> objects) {
		this.equivalentObjects.clear();
		this.equivalentObjects.addAll(objects);
	}

	/**
	 * Specifies whether or not this row displays inferred information (the
	 * default value is false)
	 */
	public void setInferred(boolean inferred) {
		this.inferred = inferred;
	}

	public void setStrikeThrough(boolean strikeThrough) {
		this.strikeThrough = strikeThrough;
	}

	public int getPreferredWidth() {
		return this.preferredWidth;
	}

	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	public int getRightMargin() {
		return this.rightMargin;
	}

	public void setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
	}

	private void setupFont() {
		this.plainFont = OWLRendererPreferences.getInstance().getFont();
		this.plainFontHeight = this.iconLabel.getFontMetrics(this.plainFont)
				.getHeight();
		this.boldFont = this.plainFont.deriveFont(Font.BOLD);
		this.textPane.setFont(this.plainFont);
	}

	protected int getFontSize() {
		return OWLRendererPreferences.getInstance().getFontSize();
	}

	public boolean isRenderExpression() {
		return this.renderExpression;
	}

	public boolean isRenderIcon() {
		return this.renderIcon;
	}

	public void setCommentedOut(boolean commentedOut) {
		this.commentedOut = commentedOut;
	}

	public boolean isWrap() {
		return this.wrap;
	}

	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	//
	// Implementation of renderer interfaces
	//
	// //////////////////////////////////////////////////////////////////////////////////////
	private boolean renderLinks;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setupLinkedObjectComponent(table, table.getCellRect(row, column, true));
		this.preferredWidth = table.getParent().getWidth();
		this.componentBeingRendered = table;
		// Set the size of the table cell
		// setPreferredWidth(table.getColumnModel().getColumn(column).getWidth());
		return prepareRenderer(value, isSelected, hasFocus);
		// // This is a bit messy - the row height doesn't get reset if it is
		// larger than the
		// // desired row height.
		// // Reset the row height if the text has been wrapped
		// int desiredRowHeight = getPrefSize(table, table.getGraphics(),
		// c.getText()).height;
		// if (desiredRowHeight < table.getRowHeight()) {
		// desiredRowHeight = table.getRowHeight();
		// }
		// else if (desiredRowHeight > table.getRowHeight(row)) {
		// // Add a bit of a margin, because wrapped lines
		// // tend to merge with adjacent lines too much
		// desiredRowHeight += 4;
		// }
		// if (table.getEditingRow() != row) {
		// if (table.getRowHeight(row) < desiredRowHeight) {
		// table.setRowHeight(row, desiredRowHeight);
		// }
		// }
		// reset();
		// return c;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		this.componentBeingRendered = tree;
		Rectangle cellBounds = new Rectangle();
		if (!this.gettingCellBounds) {
			this.gettingCellBounds = true;
			cellBounds = tree.getRowBounds(row);
			this.gettingCellBounds = false;
		}
		setupLinkedObjectComponent(tree, cellBounds);
		this.preferredWidth = -1;
		this.minTextHeight = 12;
		// textPane.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2 +
		// rightMargin));
		tree.setToolTipText(value != null ? value.toString() : "");
		Component c = prepareRenderer(value, selected, hasFocus);
		reset();
		return c;
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		this.componentBeingRendered = list;
		Rectangle cellBounds = new Rectangle();
		// We need to prevent infinite recursion here!
		if (!this.gettingCellBounds) {
			this.gettingCellBounds = true;
			cellBounds = list.getCellBounds(index, index);
			this.gettingCellBounds = false;
		}
		this.minTextHeight = 12;
		if (list.getParent() != null) {
			this.preferredWidth = list.getParent().getWidth();
		}
		// preferredWidth = -1;
		// textPane.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2 +
		// rightMargin));
		setupLinkedObjectComponent(list, cellBounds);
		Component c = prepareRenderer(value, isSelected, cellHasFocus);
		reset();
		return c;
	}

	private void setupLinkedObjectComponent(JComponent component,
			Rectangle cellRect) {
		this.renderLinks = false;
		this.linkedObjectComponent = null;
		if (cellRect == null) {
			return;
		}
		if (component instanceof LinkedObjectComponent
				&& OWLRendererPreferences.getInstance().isRenderHyperlinks()) {
			this.linkedObjectComponent = (LinkedObjectComponent) component;
			Point mouseLoc = component.getMousePosition(true);
			if (mouseLoc == null) {
				this.linkedObjectComponent.setLinkedObject(null);
				return;
			}
			this.renderLinks = cellRect.contains(mouseLoc);
		}
	}

	protected final class ActiveEntityVisitor implements OWLEntityVisitor {
		public void visit(OWLClass cls) {
			if (!getOWLModelManager().getActiveOntology().getAxioms(cls)
					.isEmpty()) {
				RenderableObjectCellRenderer.this.ontology = getOWLModelManager()
						.getActiveOntology();
			}
		}

		public void visit(OWLDataType dataType) {
		}

		public void visit(OWLIndividual individual) {
			if (!getOWLModelManager().getActiveOntology().getAxioms(individual)
					.isEmpty()) {
				RenderableObjectCellRenderer.this.ontology = getOWLModelManager()
						.getActiveOntology();
			}
		}

		public void visit(OWLDataProperty property) {
			if (!getOWLModelManager().getActiveOntology().getAxioms(property)
					.isEmpty()) {
				RenderableObjectCellRenderer.this.ontology = getOWLModelManager()
						.getActiveOntology();
			}
		}

		public void visit(OWLObjectProperty property) {
			if (!getOWLModelManager().getActiveOntology().getAxioms(property)
					.isEmpty()) {
				RenderableObjectCellRenderer.this.ontology = getOWLModelManager()
						.getActiveOntology();
			}
		}
	}

	private ActiveEntityVisitor activeEntityVisitor = new ActiveEntityVisitor();

	private Component prepareRenderer(Object value, boolean isSelected,
			boolean hasFocus) {
		this.renderingComponent.setOpaque(isSelected || this.opaque);
		if (value instanceof OWLEntity) {
			OWLEntity entity = (OWLEntity) value;
			OWLDeclarationAxiom declAx = getOWLModelManager()
					.getOWLDataFactory().getOWLDeclarationAxiom(entity);
			if (getOWLModelManager().getActiveOntology().containsAxiom(declAx)) {
				this.ontology = getOWLModelManager().getActiveOntology();
			}
			entity.accept(this.activeEntityVisitor);
		}
		prepareTextPane(getRendering(value), isSelected);
		if (isSelected) {
			this.renderingComponent.setBackground(SELECTION_BACKGROUND);
			this.textPane.setForeground(SELECTION_FOREGROUND);
		} else {
			this.renderingComponent.setBackground(this.componentBeingRendered
					.getBackground());
			this.textPane.setForeground(this.componentBeingRendered
					.getForeground());
		}
		final Icon icon = getIcon(value);
		this.iconLabel.setIcon(icon);
		if (icon != null) {
			this.iconLabel.setPreferredSize(new Dimension(icon.getIconWidth(),
					this.plainFontHeight));
		}
		this.renderingComponent.revalidate();
		return this.renderingComponent;
	}

	@SuppressWarnings("unchecked")
	protected String getRendering(Object object) {
		if (object == null) {
			return "";
		}
		if (object instanceof VariableListItem) {
			return getRendering(((VariableListItem) object).getItem());
		}
		if (object instanceof StaticListItem) {
			return getRendering(((StaticListItem) object).getItem());
		}
		if (object instanceof PropertyChainModel) {
			return ((PropertyChainModel) object).getCell().render(
					getOWLModelManager());
		}
		if (object instanceof PropertyChainCell) {
			return ((PropertyChainCell) object).render(getOWLModelManager());
		}
		if (object instanceof BindingModel) {
			BindingModel b = (BindingModel) object;
			return b.getIdentifier() + "="
					+ b.getPropertyChainModel().render(getOWLModelManager());
		}
		if (object instanceof OWLObject) {
			StringBuilder rendering = new StringBuilder(getOWLModelManager()
					.getRendering(((OWLObject) object)));
			for (OWLObject eqObj : this.equivalentObjects) {
				// Add in the equivalent class symbol
				rendering.append(" \u2261 ");
				rendering.append(getOWLModelManager().getRendering(eqObj));
			}
			return rendering.toString();
		}
		return object.toString();
	}

	protected Icon getIcon(Object object) {
		if (!this.renderIcon) {
			return null;
		}
		if (this.iconObject != null) {
			return this.owlEditorKit.getWorkspace().getOWLIconProvider()
					.getIcon(this.iconObject);
		}
		if (object instanceof OWLObject) {
			return this.owlEditorKit.getWorkspace().getOWLIconProvider()
					.getIcon((OWLObject) object);
		} else {
			return null;
		}
	}

	protected Composite disabledComposite = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 0.5f);

	protected OWLModelManager getOWLModelManager() {
		return this.owlEditorKit.getModelManager();
	}

	protected Color getColor(OWLEntity entity, Color defaultColor) {
		for (OWLEntityColorProvider prov : this.entityColorProviders) {
			Color c = prov.getColor(entity);
			if (c != null) {
				return c;
			}
		}
		return defaultColor;
	}

	protected boolean activeOntologyContainsAxioms(OWLEntity owlEntity) {
		return !getOWLModelManager().getActiveOntology().getReferencingAxioms(
				owlEntity).isEmpty();
	}

	private Style plainStyle;
	private Style boldStyle;
	private Style nonBoldStyle;
	private Style selectionForeground;
	private Style foreground;
	private Style linkStyle;
	private Style inconsistentClassStyle;
	private Style focusedEntityStyle;
	// private Style linespacingStyle;
	private Style annotationURIStyle;
	private Style ontologyURIStyle;
	private Style commentedOutStyle;
	private Style strikeOutStyle;
	private Style fontSizeStyle;

	private void prepareStyles() {
		StyledDocument doc = this.textPane.getStyledDocument();
		Map<String, Color> keyWordColorMap = this.owlEditorKit.getWorkspace()
				.getKeyWordColorMap();
		for (String keyWord : keyWordColorMap.keySet()) {
			Style s = doc.addStyle(keyWord, null);
			Color color = keyWordColorMap.get(keyWord);
			StyleConstants.setForeground(s, color);
			StyleConstants.setBold(s, true);
		}
		this.plainStyle = doc.addStyle("PLAIN_STYLE", null);
		// StyleConstants.setForeground(plainStyle, Color.BLACK);
		StyleConstants.setItalic(this.plainStyle, false);
		StyleConstants.setSpaceAbove(this.plainStyle, 0);
		// StyleConstants.setFontFamily(plainStyle,
		// textPane.getFont().getFamily());
		this.boldStyle = doc.addStyle("BOLD_STYLE", null);
		StyleConstants.setBold(this.boldStyle, true);
		this.nonBoldStyle = doc.addStyle("NON_BOLD_STYLE", null);
		StyleConstants.setBold(this.nonBoldStyle, false);
		this.selectionForeground = doc.addStyle("SEL_FG_STYPE", null);
		StyleConstants.setForeground(this.selectionForeground,
				SELECTION_FOREGROUND);
		this.foreground = doc.addStyle("FG_STYLE", null);
		StyleConstants.setForeground(this.foreground, FOREGROUND);
		this.linkStyle = doc.addStyle("LINK_STYLE", null);
		StyleConstants.setForeground(this.linkStyle, Color.BLUE);
		StyleConstants.setUnderline(this.linkStyle, true);
		this.inconsistentClassStyle = doc.addStyle("INCONSISTENT_CLASS_STYLE",
				null);
		StyleConstants.setForeground(this.inconsistentClassStyle, Color.RED);
		this.focusedEntityStyle = doc.addStyle("FOCUSED_ENTITY_STYLE", null);
		StyleConstants.setForeground(this.focusedEntityStyle, Color.BLACK);
		StyleConstants.setBackground(this.focusedEntityStyle, new Color(220,
				220, 250));
		// linespacingStyle = doc.addStyle("LINE_SPACING_STYLE", null);
		// StyleConstants.setLineSpacing(linespacingStyle, 0.0f);
		this.annotationURIStyle = doc.addStyle("ANNOTATION_URI_STYLE", null);
		StyleConstants.setForeground(this.annotationURIStyle, Color.BLUE);
		StyleConstants.setItalic(this.annotationURIStyle, true);
		this.ontologyURIStyle = doc.addStyle("ONTOLOGY_URI_STYLE", null);
		StyleConstants.setForeground(this.ontologyURIStyle, Color.GRAY);
		this.commentedOutStyle = doc.addStyle("COMMENTED_OUT_STYLE", null);
		StyleConstants.setForeground(this.commentedOutStyle, Color.GRAY);
		StyleConstants.setItalic(this.commentedOutStyle, true);
		this.strikeOutStyle = doc.addStyle("STRIKE_OUT", null);
		StyleConstants.setStrikeThrough(this.strikeOutStyle, true);
		StyleConstants.setBold(this.strikeOutStyle, false);
		this.fontSizeStyle = doc.addStyle("FONT_SIZE", null);
		StyleConstants.setFontSize(this.fontSizeStyle, 40);
	}

	private void prepareTextPane(Object value, boolean selected) {
		this.textPane.setBorder(null);
		String theVal = value.toString();
		if (!this.wrap) {
			theVal = theVal.replace('\n', ' ');
			theVal = theVal.replaceAll(" [ ]+", " ");
		}
		this.textPane.setText(theVal);
		if (this.commentedOut) {
			this.textPane.setText("// " + this.textPane.getText());
		}
		// textPane.setSize(textPane.getPreferredSize());
		StyledDocument doc = this.textPane.getStyledDocument();
		// doc.setParagraphAttributes(0, doc.getLength(), linespacingStyle,
		// false);
		resetStyles(doc);
		if (selected) {
			doc.setParagraphAttributes(0, doc.getLength(),
					this.selectionForeground, false);
		} else {
			doc.setParagraphAttributes(0, doc.getLength(), this.foreground,
					false);
		}
		if (this.commentedOut) {
			doc.setParagraphAttributes(0, doc.getLength(),
					this.commentedOutStyle, false);
			return;
		} else if (this.inferred) {
		}
		if (this.strikeThrough) {
			doc.setParagraphAttributes(0, doc.getLength(), this.strikeOutStyle,
					false);
		}
		if (this.ontology != null) {
			if (OWLRendererPreferences.getInstance()
					.isHighlightActiveOntologyStatements()
					&& getOWLModelManager().getActiveOntology().equals(
							this.ontology)) {
				doc.setParagraphAttributes(0, doc.getLength(), this.boldStyle,
						false);
			} else {
				doc.setParagraphAttributes(0, doc.getLength(),
						this.nonBoldStyle, false);
			}
		} else {
			this.textPane.setFont(this.plainFont);
		}
		// Set the writable status
		if (this.ontology != null) {
			if (getOWLModelManager().isMutable(this.ontology)) {
				this.textPane.setEnabled(!this.forceReadOnlyRendering);
			} else {
				// Not editable - set readonly
				this.textPane.setEnabled(false);
			}
		} else {
			// Ontology is null. If the object is an entity then the font
			// should be bold if there are statements about it
			if (value instanceof OWLEntity) {
				if (activeOntologyContainsAxioms((OWLEntity) value)) {
					this.textPane.setFont(this.boldFont);
				}
			}
		}
		highlightText(doc);
	}

	private void highlightText(StyledDocument doc) {
		// Highlight text
		StringTokenizer tokenizer = new StringTokenizer(
				this.textPane.getText(), " []{}(),\n\t'", true);
		this.linkRendered = false;
		this.annotURIRendered = false;
		int tokenStartIndex = 0;
		while (tokenizer.hasMoreTokens()) {
			// Get the token and determine if it is a keyword or
			// entity (or delimeter)
			String curToken = tokenizer.nextToken();
			if (curToken.equals("'")) {
				while (tokenizer.hasMoreTokens()) {
					String s = tokenizer.nextToken();
					curToken += s;
					if (s.equals("'")) {
						break;
					}
				}
			}
			renderToken(curToken, tokenStartIndex, doc);
			tokenStartIndex += curToken.length();
		}
		if (this.renderLinks && !this.linkRendered) {
			this.linkedObjectComponent.setLinkedObject(null);
		}
	}

	private boolean annotURIRendered = false;
	private boolean linkRendered = false;
	private boolean parenthesisRendered = false;

	protected void renderToken(final String curToken,
			final int tokenStartIndex, final StyledDocument doc) {
		boolean enclosedByBracket = false;
		if (this.parenthesisRendered) {
			this.parenthesisRendered = false;
			enclosedByBracket = true;
		}
		OWLRendererPreferences prefs = OWLRendererPreferences.getInstance();
		int tokenLength = curToken.length();
		Color c = this.owlEditorKit.getWorkspace().getKeyWordColorMap().get(
				curToken);
		if (c != null && prefs.isHighlightKeyWords() && this.highlightKeywords) {
			Style s = doc.getStyle(curToken);
			doc.setCharacterAttributes(tokenStartIndex, tokenLength, s, true);
		} else {
			// Not a keyword, so might be an entity (or delim)
			OWLEntity curEntity = getOWLModelManager().getOWLEntity(curToken);
			if (curEntity != null) {
				if (this.focusedEntity != null) {
					if (curEntity.equals(this.focusedEntity)) {
						doc.setCharacterAttributes(tokenStartIndex,
								tokenLength, this.focusedEntityStyle, true);
					}
				} else if (curEntity instanceof OWLClass) {
					// If it is a class then paint the word red if the class
					// is inconsistent
					try {
						if (this.highlightUnsatisfiableClasses &&
						// !getOWLModelManager().getReasoner().isConsistent(getOWLModelManager().getActiveOntology())
								// ||
								!getOWLModelManager().getReasoner()
										.isSatisfiable((OWLClass) curEntity)) {
							// Paint red because of inconsistency
							doc.setCharacterAttributes(tokenStartIndex,
									tokenLength, this.inconsistentClassStyle,
									true);
						}
					} catch (OWLReasonerException e) {
						e.printStackTrace();
					}
				} else if (this.highlightUnsatisfiableProperties
						&& curEntity instanceof OWLObjectProperty) {
					highlightPropertyIfUnsatisfiable(curEntity, doc,
							tokenStartIndex, tokenLength);
				}
				strikeoutEntityIfCrossedOut(curEntity, doc, tokenStartIndex,
						tokenLength);
				if (this.renderLinks) {
					renderHyperlink(curEntity, tokenStartIndex, tokenLength,
							doc);
				}
			} else {
				if (this.highlightUnsatisfiableClasses
						&& this.unsatisfiableNames.contains(curToken)) {
					// Paint red because of inconsistency
					doc.setCharacterAttributes(tokenStartIndex, tokenLength,
							this.inconsistentClassStyle, true);
				} else if (isAnnotationURI(curToken) && !this.annotURIRendered) { // this
					// could
					// be
					// an
					// annotation
					// URI
					doc.setCharacterAttributes(tokenStartIndex, tokenLength,
							this.annotationURIStyle, true);
					this.annotURIRendered = true;
				} else if (isOntologyURI(curToken)) {
					fadeOntologyURI(doc, tokenStartIndex, tokenLength,
							enclosedByBracket);
				} else if (curToken.equals("(")) {
					this.parenthesisRendered = true;
				}
			}
		}
	}

	private void renderHyperlink(OWLEntity curEntity, int tokenStartIndex,
			int tokenLength, StyledDocument doc) {
		try {
			Rectangle startRect = this.textPane.modelToView(tokenStartIndex);
			Rectangle endRect = this.textPane.modelToView(tokenStartIndex
					+ tokenLength);
			if (startRect != null && endRect != null) {
				int width = endRect.x - startRect.x;
				int heght = startRect.height;
				Rectangle tokenRect = new Rectangle(startRect.x, startRect.y,
						width, heght);
				tokenRect.grow(0, -2);
				if (this.linkedObjectComponent.getMouseCellLocation() != null) {
					Point mouseCellLocation = this.linkedObjectComponent
							.getMouseCellLocation();
					if (mouseCellLocation != null) {
						mouseCellLocation = SwingUtilities.convertPoint(
								this.renderingComponent, mouseCellLocation,
								this.textPane);
						if (tokenRect.contains(mouseCellLocation)) {
							doc.setCharacterAttributes(tokenStartIndex,
									tokenLength, this.linkStyle, false);
							this.linkedObjectComponent
									.setLinkedObject(curEntity);
							this.linkRendered = true;
						}
					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private boolean isOntologyURI(String token) {
		try {
			URI uri = new URI(token);
			if (uri.isAbsolute()) {
				OWLOntology ont = getOWLModelManager().getOWLOntologyManager()
						.getOntology(uri);
				if (getOWLModelManager().getActiveOntologies().contains(ont)) {
					return true;
				}
			}
		} catch (URISyntaxException e) {
			// just dropthough
		}
		return false;
	}

	private void fadeOntologyURI(StyledDocument doc, int tokenStartIndex,
			int tokenLength, boolean enclosedByBracket) {
		// if surrounded by brackets, also render them in grey
		int start = tokenStartIndex;
		int length = tokenLength;
		if (enclosedByBracket) {
			start--;
			length = length + 2;
		}
		doc.setCharacterAttributes(start, length, this.ontologyURIStyle, true);
	}

	private boolean isAnnotationURI(String token) {
		if (this.annotationURINames == null) {
			this.annotationURINames = new HashSet<String>();
			for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
				for (URI uri : (ont.getAnnotationURIs())) {
					this.annotationURINames.add(getOWLModelManager()
							.getURIRendering(uri));
				}
			}
		}
		return this.annotationURINames.contains(token);
	}

	private void strikeoutEntityIfCrossedOut(OWLEntity entity,
			StyledDocument doc, int tokenStartIndex, int tokenLength) {
		if (this.crossedOutEntities.contains(entity)) {
			doc.setCharacterAttributes(tokenStartIndex, tokenLength,
					this.strikeOutStyle, false);
		}
	}

	private void highlightPropertyIfUnsatisfiable(OWLEntity entity,
			StyledDocument doc, int tokenStartIndex, int tokenLength) {
		try {
			OWLObjectProperty prop = (OWLObjectProperty) entity;
			OWLDescription d = getOWLModelManager().getOWLDataFactory()
					.getOWLObjectMinCardinalityRestriction(prop, 1);
			if (!getOWLModelManager().getReasoner().isSatisfiable(d)) {
				doc.setCharacterAttributes(tokenStartIndex, tokenLength,
						this.inconsistentClassStyle, true);
			}
		} catch (OWLReasonerException e) {
		}
	}

	private void resetStyles(StyledDocument doc) {
		doc.setParagraphAttributes(0, doc.getLength(), this.plainStyle, true);
		StyleConstants.setFontSize(this.fontSizeStyle, getFontSize());
		Font f = OWLRendererPreferences.getInstance().getFont();
		StyleConstants.setFontFamily(this.fontSizeStyle, f.getFamily());
		doc.setParagraphAttributes(0, doc.getLength(), this.fontSizeStyle,
				false);
		setupFont();
	}

	protected final class OWLCellRendererLayoutManager implements
			LayoutManager2 {
		/**
		 * Adds the specified component to the layout, using the specified
		 * constraint object.
		 * 
		 * @param comp
		 *            the component to be added
		 * @param constraints
		 *            where/how the component is added to the layout.
		 */
		public void addLayoutComponent(Component comp, Object constraints) {
			// We only have two components the label that holds the icon
			// and the text area
		}

		/**
		 * Calculates the maximum size dimensions for the specified container,
		 * given the components it contains.
		 * 
		 * @see java.awt.Component#getMaximumSize
		 * @see java.awt.LayoutManager
		 */
		public Dimension maximumLayoutSize(Container target) {
			return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
		}

		/**
		 * Returns the alignment along the x axis. This specifies how the
		 * component would like to be aligned relative to other components. The
		 * value should be a number between 0 and 1 where 0 represents alignment
		 * along the origin, 1 is aligned the furthest away from the origin, 0.5
		 * is centered, etc.
		 */
		public float getLayoutAlignmentX(Container target) {
			return 0;
		}

		/**
		 * Returns the alignment along the y axis. This specifies how the
		 * component would like to be aligned relative to other components. The
		 * value should be a number between 0 and 1 where 0 represents alignment
		 * along the origin, 1 is aligned the furthest away from the origin, 0.5
		 * is centered, etc.
		 */
		public float getLayoutAlignmentY(Container target) {
			return 0;
		}

		/**
		 * Invalidates the layout, indicating that if the layout manager has
		 * cached information it should be discarded.
		 */
		public void invalidateLayout(Container target) {
		}

		/**
		 * If the layout manager uses a per-component string, adds the component
		 * <code>comp</code> to the layout, associating it with the string
		 * specified by <code>name</code>.
		 * 
		 * @param name
		 *            the string to be associated with the component
		 * @param comp
		 *            the component to be added
		 */
		public void addLayoutComponent(String name, Component comp) {
		}

		/**
		 * Removes the specified component from the layout.
		 * 
		 * @param comp
		 *            the component to be removed
		 */
		public void removeLayoutComponent(Component comp) {
		}

		/**
		 * Calculates the preferred size dimensions for the specified container,
		 * given the components it contains.
		 * 
		 * @param parent
		 *            the container to be laid out
		 * @see #minimumLayoutSize
		 */
		public Dimension preferredLayoutSize(Container parent) {
			if (RenderableObjectCellRenderer.this.componentBeingRendered instanceof JList) {
				JList list = (JList) RenderableObjectCellRenderer.this.componentBeingRendered;
				if (list.getFixedCellHeight() != -1) {
					return new Dimension(list.getWidth(), list.getHeight());
				}
			}
			int iconWidth;
			int iconHeight;
			int textWidth;
			int textHeight;
			int width;
			int height;
			iconWidth = RenderableObjectCellRenderer.this.iconLabel
					.getPreferredSize().width;
			iconHeight = RenderableObjectCellRenderer.this.iconLabel
					.getPreferredSize().height;
			// Insets insets = parent.getInsets();
			Insets rcInsets = RenderableObjectCellRenderer.this.renderingComponent
					.getInsets();
			if (RenderableObjectCellRenderer.this.preferredWidth != -1) {
				textWidth = RenderableObjectCellRenderer.this.preferredWidth
						- iconWidth - rcInsets.left - rcInsets.right;
				View v = RenderableObjectCellRenderer.this.textPane
						.getUI()
						.getRootView(RenderableObjectCellRenderer.this.textPane);
				v.setSize(textWidth, Integer.MAX_VALUE);
				textHeight = (int) v.getMinimumSpan(View.Y_AXIS);
				width = RenderableObjectCellRenderer.this.preferredWidth;
			} else {
				textWidth = RenderableObjectCellRenderer.this.textPane
						.getPreferredSize().width;
				textHeight = RenderableObjectCellRenderer.this.textPane
						.getPreferredSize().height;
				width = textWidth + iconWidth;
			}
			if (textHeight < iconHeight) {
				height = iconHeight;
			} else {
				height = textHeight;
			}
			int minHeight = RenderableObjectCellRenderer.this.minTextHeight;
			if (height < minHeight) {
				height = minHeight;
			}
			int totalWidth = width + rcInsets.left + rcInsets.right;
			int totalHeight = height + rcInsets.top + rcInsets.bottom;
			return new Dimension(totalWidth, totalHeight);
		}

		/**
		 * Lays out the specified container.
		 * 
		 * @param parent
		 *            the container to be laid out
		 */
		public void layoutContainer(Container parent) {
			int iconWidth;
			int iconHeight;
			int textWidth;
			int textHeight;
			Insets rcInsets = RenderableObjectCellRenderer.this.renderingComponent
					.getInsets();
			iconWidth = RenderableObjectCellRenderer.this.iconLabel
					.getPreferredSize().width;
			iconHeight = RenderableObjectCellRenderer.this.iconLabel
					.getPreferredSize().height;
			if (RenderableObjectCellRenderer.this.preferredWidth != -1) {
				textWidth = RenderableObjectCellRenderer.this.preferredWidth
						- iconWidth - rcInsets.left - rcInsets.right;
				View v = RenderableObjectCellRenderer.this.textPane
						.getUI()
						.getRootView(RenderableObjectCellRenderer.this.textPane);
				v.setSize(textWidth, Integer.MAX_VALUE);
				textHeight = (int) v.getMinimumSpan(View.Y_AXIS);
			} else {
				textWidth = RenderableObjectCellRenderer.this.textPane
						.getPreferredSize().width;
				textHeight = RenderableObjectCellRenderer.this.textPane
						.getPreferredSize().height;
				if (textHeight < RenderableObjectCellRenderer.this.minTextHeight) {
					textHeight = RenderableObjectCellRenderer.this.minTextHeight;
				}
			}
			int leftOffset = rcInsets.left;
			int topOffset = rcInsets.top;
			RenderableObjectCellRenderer.this.iconLabel.setBounds(leftOffset,
					topOffset, iconWidth, iconHeight);
			RenderableObjectCellRenderer.this.textPane.setBounds(leftOffset
					+ iconWidth, topOffset, textWidth, textHeight);
		}

		/**
		 * Calculates the minimum size dimensions for the specified container,
		 * given the components it contains.
		 * 
		 * @param parent
		 *            the component to be laid out
		 * @see #preferredLayoutSize
		 */
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(0, 0);
		}
	}
}
