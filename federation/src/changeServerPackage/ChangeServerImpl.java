package changeServerPackage;

import fileManagerPackage.OntologyFileManager;

import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 11, 2008
 * Time: 3:45:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeServerImpl implements ChangeServer {

    private ConflictDetector conflictDetector;

    /** creates a change server with a specific conflict detector implementation that it should use */
    public ChangeServerImpl(ConflictDetector conflictDetector) {
        this.conflictDetector = conflictDetector;
        System.out.println("Change Server initialized");
    }

    /** returns the latest change number for the given ontology URI */
    public String getLatestChangeNumber(String ontologyURI) {
        String changeNumber;
        OntologyFileManager fm;
        try {
            fm = OntologyFileManager.getInstance(ontologyURI);
            changeNumber = Long.toString(fm.getLatestChangeSequenceNumber());
        } catch (IOException e) {
            e.printStackTrace();
            changeNumber = e.toString();
        }
        return changeNumber;
    }

    /** returns a serialized verion of the change capsule requested for transfer to requesting client */
    public String getSpecificChange(String ontologyURI, Long sequenceNumber) {
        String cpString = null;
        try {
            OntologyFileManager fm = OntologyFileManager.getInstance(ontologyURI);
            ChangeCapsule cp = fm.getSpecificChange(sequenceNumber);
            cpString = cp.toJSON();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            cpString = "Error: change file not found "+e;
        }
        return cpString;
    }

    /** Sets a changes timestamp and sequence number,
     * extracts the target ontology from a change object, and
     * stores the change in the list of changes for that "OntologyFileManager".<br><br>
     * Slight hack: take only the first change in the list of changes to be applied at once for
     * deciding which ontology to send this change to on the server) */
    public String recordChange(ChangeCapsule changeCapsule) {
        String errorReturn = "success";

        //get first change's URI
        String firstOntologyURI = changeCapsule.getOntologyURI();

        //removed temporary hack to create ontology folder
        boolean created = createNewOntologyBaseline(firstOntologyURI);
        if (created) System.err.println("created new ontology folder");
        //else System.err.println("did not created folder (already exists?)");

        //store changeCapsule away onto the filesystem
        try {
            OntologyFileManager fileManager = OntologyFileManager.getInstance(firstOntologyURI);

            //check for conflict (client sequence number, latest server sequence number)
            conflictDetector.check(changeCapsule, fileManager);

            //set the timestamp
            changeCapsule.setTimestamp(getCurrentTimeString());

            //set sequence number
            long seq = fileManager.getLatestChangeSequenceNumber()+1;
            changeCapsule.setSequence(seq);

            fileManager.addChange(changeCapsule);
        } catch (IOException e) {
            errorReturn = "Error: change storage error: "+e;
            System.err.println(errorReturn);
        } catch(ChangeConflictException e) {
            errorReturn = e.getMessage();   //returns cause of conflict to calling client
        }

        return errorReturn;
    }


    private String getCurrentTimeString() {
        Date date = new Date();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        return df.format(date);
    }

    public boolean createNewOntologyBaseline(String uri) {
        try {
            return OntologyFileManager.createBaseline(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
