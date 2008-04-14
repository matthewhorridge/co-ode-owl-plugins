/**
 * 
 */
package uk.ac.manchester.cs.lintroll.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Luigi Iannone
 * 
 */
public class LabelledTextArea extends JPanel {
	private JLabel label;
	private JTextArea textArea;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3712649739532503722L;

	public LabelledTextArea(String labelCaption) {
		this.init(labelCaption, "");
	}

	public LabelledTextArea(String labelCaption, String text) {
		this.init(labelCaption, text);
	}

	private void init(String labelCaption, String text) {
		this.label = new JLabel(labelCaption);
		this.textArea = new JTextArea(text);
		this.add(this.label);
		this.add(new JScrollPane(this.textArea));
	}

	/**
	 * @return the label
	 */
	public JLabel getLabel() {
		return this.label;
	}

	/**
	 * @return the textArea
	 */
	public JTextArea getTextArea() {
		return this.textArea;
	}
}
