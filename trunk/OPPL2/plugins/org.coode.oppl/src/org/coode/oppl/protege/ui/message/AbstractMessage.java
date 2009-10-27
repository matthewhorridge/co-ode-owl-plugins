/**
 *
 */
package org.coode.oppl.protege.ui.message;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class AbstractMessage implements Message {
	private final String messageText;

	/**
	 * Build this AstractMessage starting from the input string.
	 * 
	 * @param messageText
	 *            the text in this AbstractMessage. Cannot be {@code null}.
	 */
	protected AbstractMessage(String messageText) {
		if (messageText == null) {
			throw new IllegalArgumentException(
					"The message text cannot be null");
		}
		this.messageText = messageText;
	}

	/**
	 * @see org.coode.oppl.protege.ui.message.Message#getMessageText()
	 */
	public String getMessageText() {
		return this.messageText;
	}

	@Override
	public String toString() {
		return this.messageText;
	}
}
