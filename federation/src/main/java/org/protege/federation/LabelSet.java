package org.protege.federation;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 26, 2008
 * Time: 3:26:57 PM
 * Stores a set of labels used within each ontology
 */
public class LabelSet {
    public JLabel changeCount = null;   //counts the number of changes which have been recorded
    public JLabel clientSequenceNumber = null;  //displays the currently downloaded sequence number on the client
    public JLabel serverSequenceNumber = null; //displays the latest sequence number available on the server
    public JLabel serverChangeSummary = null;
    public JLabel serverChangeAuthor = null;
    public JLabel serverChangeTimestamp = null;

    public JToggleButton active = null;

    public LabelSet() {

    }
}
