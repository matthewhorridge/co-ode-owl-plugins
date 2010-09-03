package org.protege.federation;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.history.HistoryManagerImpl;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.prefs.Preferences;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.util.SimpleURIShortFormProvider;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.*;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.net.*;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import client.ChangeMonitor;
import client.OperationsClient;
import changeServerPackage.ChangeConflictException;
import changeServerPackage.ChangeCapsule;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 7, 2008
 * Time: 2:21:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FederationViewComponent extends AbstractOWLViewComponent {

    private OWLModelManager manager;
    private ChangeSaverLoader changeSaverLoader;    //loader dialog boxes for the saving and loading of serialized changes
    private static HashMap<String, ChangeMonitor> changeMonitors = new HashMap<String, ChangeMonitor>(1); //map between ontology URIs and their corrosponding change monitors
    private static HashMap<String, VersioningStrategy> versioningStrategies = new HashMap<String, VersioningStrategy>(1); //map between ontology URIs and their corrosponding change monitors
    private static HashMap<String, ChangeCountUpdater> changeCountUpdaters = new HashMap<String, ChangeCountUpdater>(1);    //detects changes and updates the change count appropriately
    private OperationsClient operationsClient = null;

    protected JTextField serverField;
    protected JTextField usernameField;

    //tab-specific variables

    /*protected JToggleButton active;    //displays whether or not changes are being recorded by the plug-in
    protected JLabel changeCount;   //counts the number of changes which have been recorded
    protected JLabel clientSequenceNumber;  //displays the currently downloaded sequence number on the client
    protected JLabel serverSequenceNumber; //displays the latest sequence number available on the server
    protected JLabel serverChangeSummary;
    protected JLabel serverChangeAuthor;
    protected JLabel serverChangeTimestamp;*/
    protected static HashMap<String, LabelSet> labelSets = new HashMap<String, LabelSet>(1);    //encapsulates all the above labels and controls

    private static final int XGRIDSPACING = 10;
    private static final int YGRIDSPACING = 10;


    /** remove any listeners added to this tab */
    protected void disposeOWLView() {
        Set<String> ontologyURIs = changeMonitors.keySet();
        for(String uri : ontologyURIs)
        manager.removeOntologyChangeListener(changeMonitors.get(uri));
    }

    /** Saves the current configuration of server name, username, etc. to the standard Protege preferences list */
    private void saveConfiguration() {
        Preferences pref = PreferencesManager.getInstance().getPreferencesForSet("FederationPreferences", "Saved");
        pref.putString("serverField", serverField.getText());
        pref.putString("usernameField", usernameField.getText());
    }

    /** Loads the saved server name and username, if they have been set */
    private void loadConfiguration() {
        Preferences pref = PreferencesManager.getInstance().getPreferencesForSet("FederationPreferences", "Saved");
        serverField.setText(pref.getString("serverField", ""));
        usernameField.setText(pref.getString("usernameField", ""));
    }

    protected void initialiseOWLView() throws Exception {
        this.manager = getOWLModelManager();
        changeSaverLoader = new ChangeSaverLoader(this);

        //checkOperationsClient();  //don't initially connect

        setLayout(new BorderLayout(10, 10));
        JPanel pane = rebuildUI();

        add(pane);

        loadConfiguration();    //load in the saved server address and username from a previous session

        updateUI();
    }

    /** tests if the operations client is up to date and, if necessary, re-creates it with a new server and username*/
    private void checkOperationsClient() {
        try {
            if (operationsClient == null) {
                operationsClient = new OperationsClient(serverField.getText(), usernameField.getText());
            } else {
                if ((operationsClient.getServerHostname().compareTo(serverField.getText()) != 0) ||
                   ((operationsClient.getUsername().compareTo(usernameField.getText()) != 0))) {
                    operationsClient = new OperationsClient(serverField.getText(), usernameField.getText());
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString(), "Problem connecting to server", JOptionPane.ERROR_MESSAGE);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString(), "Problem connecting to server", JOptionPane.ERROR_MESSAGE);
        }

        saveConfiguration();    //after any operation that connect to the net, always save the current servername and username
    }

    protected JPanel rebuildUI() {
        JPanel initialPanel = new JPanel(new BorderLayout());

        //build server connection panel
        JPanel serverConnector = new JPanel(new FlowLayout());
        serverConnector.add(new JLabel("Enter server hostname/IP-address: "));
        serverField = new JTextField(21); //field to store the server's name
        serverConnector.add(serverField);
        serverConnector.add(new JLabel("         Enter username: "));
        usernameField = new JTextField(21);

        try {
            usernameField.setText(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {  //ignore exceptions here, just leave the text field blank
            e.printStackTrace();
        }
        serverConnector.add(usernameField);
        initialPanel.add(serverConnector, BorderLayout.NORTH);

        //build the tabbed pane for each ontology
        SimpleURIShortFormProvider shortener = new SimpleURIShortFormProvider();
        JTabbedPane tabbedPane = new JTabbedPane();
        Set<OWLOntology> ontologies =  manager.getOntologies();
        for(OWLOntology onto : ontologies) {
            final String ontoURI = onto.getURI().toString();

            //use or create a new ChangeMonitor for each loaded ontology
            final ChangeMonitor changeMonitor;
            if (!changeMonitors.containsKey(ontoURI)) {
                HistoryManagerImpl historyManager = (HistoryManagerImpl)manager.getHistoryManager();

                changeMonitor = new ChangeMonitor(onto, historyManager.getChanges());
                changeMonitors.put(ontoURI, changeMonitor);
            } else {
                changeMonitor = changeMonitors.get(ontoURI);
            }
            manager.addOntologyChangeListener(changeMonitor);



            //add a versioning strategy for each loaded ontology (non-locking varierty)
            if (!versioningStrategies.containsKey(ontoURI)) {
                VersioningStrategy versioningStrategy = new VersioningStrategy_NonLocking(onto, operationsClient, changeMonitor, manager.getOWLOntologyManager());
                versioningStrategies.put(ontoURI, versioningStrategy);
            }


            JPanel tab = new JPanel(new BorderLayout());

            //vertically align everything in the tab
            JPanel tabContents = new JPanel();
            tabContents.setLayout(new BoxLayout(tabContents, BoxLayout.PAGE_AXIS));
            tabContents.setAlignmentY(Component.CENTER_ALIGNMENT);
            tabContents.setAlignmentY(Component.TOP_ALIGNMENT);

            //displays if change recording is active
            JPanel activeDisplay = new JPanel(new GridLayout(1, 2, XGRIDSPACING, YGRIDSPACING));    //10 pixels caps between grid elements
            activeDisplay.add(new JLabel("local change history recording: "));

            //create the set of information providing labels for this ontology
            LabelSet labelSet;
            if (!labelSets.containsKey(ontoURI)) {
                labelSet = new LabelSet();
                labelSets.put(ontoURI, labelSet);
            } else {
                labelSet = labelSets.get(ontoURI);
            }

            //make sure the change count label is always up to date
            ChangeCountUpdater changeCountUpdater;
            if (!changeCountUpdaters.containsKey(ontoURI)) {
                changeCountUpdater = new ChangeCountUpdater(labelSet, changeMonitor);
                changeCountUpdaters.put(ontoURI, changeCountUpdater);
            } else {
                changeCountUpdater = changeCountUpdaters.get(ontoURI);
            }
            manager.addOntologyChangeListener(changeCountUpdater);



            labelSet.active =  new JToggleButton("inactive");
            labelSet.active.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (!changeMonitor.enabled()) changeMonitor.setEnabled(true);
                    } else {
                        if (changeMonitor.enabled()) changeMonitor.setEnabled(false);
                    }
                }
            });
            activeDisplay.add(labelSet.active);
            tabContents.add(activeDisplay);

            //counts the number of changes recorded
            JPanel changeCountPanel = new JPanel(new GridLayout(1, 2, XGRIDSPACING, YGRIDSPACING)); //10 pixels caps between grid elements
            changeCountPanel.add(new JLabel("number of changes recorded: "));
            labelSet.changeCount = new JLabel("0");
            changeCountPanel.add(labelSet.changeCount);
            tabContents.add(changeCountPanel);


            //display sequence number of the tag currently downloaded to the client
            JPanel sequenceNumberPanel1 = new JPanel(new GridLayout(1, 2, XGRIDSPACING, YGRIDSPACING));    //10 pixels caps between grid elements
            sequenceNumberPanel1.add(new JLabel("current change sequence number on client: "));
            labelSet.clientSequenceNumber = new JLabel("--");
            sequenceNumberPanel1.add(labelSet.clientSequenceNumber);
            tabContents.add(sequenceNumberPanel1);

            //load , save and clear changes button
            JPanel localChangeControlsPanel = new JPanel(new GridLayout(1, 3, XGRIDSPACING, YGRIDSPACING));
            JButton loadButton = new JButton("load & apply");
            loadButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    loadChanges(ontoURI);
                }
            });
            localChangeControlsPanel.add(loadButton);
            JButton clearButton = new JButton("undo & clear");
            clearButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    clearChanges(ontoURI);
                }
            });
            localChangeControlsPanel.add(clearButton);
            JButton saveButton = new JButton("save");
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    saveChanges(ontoURI);
                }
            });
            localChangeControlsPanel.add(saveButton);


            //publish button
            JButton publishButton = new JButton("PUBLISH / COMMIT");
            publishButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    publishChanges(ontoURI);
                }
            });

            //add a dividing line
            tabContents.add(new JSeparator());

            //display sequence number of latest change available on the server
            JPanel sequenceNumberPanel2 = new JPanel(new GridLayout(1, 2, XGRIDSPACING, YGRIDSPACING));    //10 pixels caps between grid elements
            sequenceNumberPanel2.add(new JLabel("latest change sequence number: "));
            labelSet.serverSequenceNumber = new JLabel("--");
            sequenceNumberPanel2.add(labelSet.serverSequenceNumber);
            tabContents.add(sequenceNumberPanel2);

            //username
            JPanel latestChangeUser = new JPanel(new GridLayout(1, 2, XGRIDSPACING, YGRIDSPACING));    //10 pixels caps between grid elements
            latestChangeUser.add(new JLabel("latest change author: "));
            labelSet.serverChangeAuthor = new JLabel("--");
            latestChangeUser.add(labelSet.serverChangeAuthor);
            tabContents.add(latestChangeUser);

            //timestamp
            JPanel latestChangeTimestamp = new JPanel(new GridLayout(1, 2, XGRIDSPACING, YGRIDSPACING));    //10 pixels caps between grid elements
            latestChangeTimestamp.add(new JLabel("latest change timestamp: "));
            labelSet.serverChangeTimestamp = new JLabel("--");
            latestChangeTimestamp.add(labelSet.serverChangeTimestamp);
            tabContents.add(latestChangeTimestamp);

            //summary
            JPanel latestChangeSummary = new JPanel(new GridLayout(1, 2, XGRIDSPACING, YGRIDSPACING));    //10 pixels caps between grid elements
            latestChangeSummary.add(new JLabel("latest change summary: "));
            labelSet.serverChangeSummary = new JLabel("--");
            latestChangeSummary.add(labelSet.serverChangeSummary);
            tabContents.add(latestChangeSummary);

            //controls for refreshing and downloading changes
            JPanel serverControlsPanel = new JPanel(new GridLayout(2, 1, XGRIDSPACING*4, YGRIDSPACING));    //10 pixels caps between grid elements
            JButton refreshButton = new JButton("refresh");
            refreshButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent actionEvent) {
                    queryChanges(ontoURI);
                }
            });
            JButton downloadButton = new JButton("DOWNLOAD / UPDATE");
            downloadButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent actionEvent) {
                    downloadChanges(ontoURI);
                }
            });
            serverControlsPanel.add(refreshButton);
            serverControlsPanel.add(downloadButton);
            tabContents.add(serverControlsPanel);

            tab.add(tabContents, BorderLayout.CENTER);
            tabbedPane.addTab(shortener.getShortForm(onto.getURI()), null, tab, onto.getURI().toString()); //tooltip of complete URI, name of just the last bit of the URI
        }
        initialPanel.add(tabbedPane, BorderLayout.SOUTH);

        return initialPanel;
    }

    /** publishes the changes recorded so far*/
    protected void publishChanges(String ontoURL) {
        checkOperationsClient();

        try {
            if (versioningStrategies.containsKey(ontoURL)) {
                VersioningStrategy versioningStrategy = versioningStrategies.get(ontoURL);
                if (!versioningStrategy.isDirty() && versioningStrategy.isUpToDate()) {

                    String summary = JOptionPane.showInputDialog(this, "Please enter a summary description for the changes to be published");
                    String response = versioningStrategy.publishChanges(summary);

                    queryChanges(ontoURL);  //update the info display panel with the latests info (just published) 
                    //TODO: possibly remove this line, confirmation given by an update of the server's most recent change display
                    JOptionPane.showMessageDialog(this, response, "Server Response", JOptionPane.INFORMATION_MESSAGE);  //print "success"

                } else {
                    JOptionPane.showMessageDialog(this, "Please update to the latest version (and check for errors) before publishing new changes", "Publishing failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString(), "Could not create OWL ontology", JOptionPane.ERROR_MESSAGE);
        } catch (OWLOntologyChangeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString(), "Could not process ontology change", JOptionPane.ERROR_MESSAGE);
        } catch (ChangeConflictException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString(), "A change conflict occurred", JOptionPane.ERROR_MESSAGE);
        //} catch (URISyntaxException e) {
        //    e.printStackTrace();
        //    JOptionPane.showMessageDialog(this, e.toString(), "Invalid Ontology URI", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString(), "Publishing failed (I/O Error)", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** queries the server for the state of the latest change */
    protected void queryChanges(String ontoURL) {
        checkOperationsClient();

        if (changeMonitors.containsKey(ontoURL) && labelSets.containsKey(ontoURL)) {  //basic sanity check to make sure the ontology is actually being change tracked
            try {
                Long latestVersion = changeMonitors.get(ontoURL).getLatestVersionNumber();
                OWLOntology ontology = manager.getOWLOntologyManager().getOntology(new URI(ontoURL));
                LabelSet labelSet = labelSets.get(ontoURL);

                //fetch the latest change
                ChangeCapsule change = operationsClient.getSpecificChange(ontology, latestVersion);

                //update the various fields in the interface with the details of the latest change
                labelSet.serverSequenceNumber.setText(Long.toString(change.getSequence()));
                labelSet.serverChangeSummary.setText(change.getSummary());
                labelSet.serverChangeAuthor.setText(change.getUsername());
                labelSet.serverChangeTimestamp.setText(change.getTimestamp());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.toString(), "I/O Error", JOptionPane.ERROR_MESSAGE);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.toString(), "Ontology URI not found", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No ontology found", "Ontology URI not found", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** downloads all new changes from the server and integrates them into the current ontology.
     * This method executes four different types of operations depending on the state of the local model
     * and the state of the model on the server. */
    protected void downloadChanges(String ontoURL) {
        if (changeMonitors.containsKey(ontoURL)) {  //basic sanity check to make sure the ontology is actually being change tracked

            ChangeMonitor cm = changeMonitors.get(ontoURL); //fetch the correct change monitor for this ontology
            checkOperationsClient();    //check that we can execute operations and the servername is nicely set

            try {
                //OWLOntology ontology = manager.getOWLOntologyManager().getOntology(new URI(ontoURL));

                VersioningStrategy versioningStrategy  = versioningStrategies.get(ontoURL);
                String messageString;    //string to display in a pop-up after download

                boolean dirty = versioningStrategy.isDirty();   //the first time this calls, the latest tag is downloaded, the result is cached on subsequent calls to this method
                boolean upToDate = versioningStrategy.isUpToDate();

                if (dirty || !upToDate) {  //if we are already up to date and clean, then no need to do any updating
                    messageString = versioningStrategy.bringUpToDate(dirty, upToDate); //downloads all the latest changes from the server, taking possible conflicts in account
                } else {
                    //clean and up-to-date, so nothing to do
                    messageString = "Ontology is already up to date (no changes downloaded).";
                }

                if (messageString != null) {
                    //pop-up to ask the user to check for conflicts
                    JOptionPane.showMessageDialog(this, messageString, "Update Complete - please check for error", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.toString(), "I/O Error", JOptionPane.ERROR_MESSAGE);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.toString(), "Ontology URI not found", JOptionPane.ERROR_MESSAGE);
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.toString(), "Could not create OWL ontology", JOptionPane.ERROR_MESSAGE);
            } catch (OWLOntologyChangeException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.toString(), "Could not process ontology change", JOptionPane.ERROR_MESSAGE);
            } catch (ChangeConflictException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.toString(), "A change conflict occurred", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    /** undos and deletes all changes that have been recorded so far for this ontology
     * (if this is called as part of the "publish changes" function,
     * then changes will be re-applied once the new state of the ontology
     * is downloaded from the server after publishing) */
    protected void clearChanges(String ontoURL) {
        if (changeMonitors.containsKey(ontoURL)) {
            try {
                Integer preDeleteCount = 0;
                if (labelSets.containsKey(ontoURL)) preDeleteCount = new Integer(labelSets.get(ontoURL).changeCount.getText()); //how many changes exist before deleting

                changeMonitors.get(ontoURL).undoAndDeleteChanges(manager.getOWLOntologyManager());
                JOptionPane.showMessageDialog(this, preDeleteCount+" changes were rolled back", "Changes Deleted", JOptionPane.INFORMATION_MESSAGE);
            } catch (OWLOntologyChangeException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                JOptionPane.showMessageDialog(this, e.toString(), "Could not undo changes", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** saves all changes recorded so far into a changeCapsule in a file (so they can be loaded later) */
    protected void saveChanges(String ontoURL) {
        if (changeMonitors.containsKey(ontoURL)) {
            try {
                ChangeMonitor cm = changeMonitors.get(ontoURL);

                File toSave = changeSaverLoader.saveFile("changes"+cm.getLatestVersionNumber()+".txt"); //get file

                List<OWLOntologyChange> changes = cm.getChanges();
                ChangeCapsule capsule = new ChangeCapsule(changes);
                String changeText = capsule.toJSON();
                PrintWriter pw = new PrintWriter(toSave);
                pw.write(changeText);
                pw.close();
            } catch (FileNotFoundException e) {
                System.err.println(e.toString());
            }
        }
    }

    /** loads a saved set of changes from a changeCapsule file and applies them on the current ontology */
    protected void loadChanges(String ontoURL) {
        File toOpen = changeSaverLoader.openFile();
    }

    public void updateUI() {
        super.updateUI();
    }
}
