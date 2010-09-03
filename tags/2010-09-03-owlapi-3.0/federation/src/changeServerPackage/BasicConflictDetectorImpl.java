package changeServerPackage;

import fileManagerPackage.OntologyFileManager;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 5, 2008
 * Time: 2:01:11 PM
 * Very basic implementation of the conflict detector. It requires that a client have the latest
 * version of each ontology on the server before it will accept changes coming from that client.
 */
public class BasicConflictDetectorImpl implements ConflictDetector {

    /** rejects a changeCapsule unless it has the same sequence number as the latest change on the server */
    public void check(ChangeCapsule changeCapsule, OntologyFileManager fileManager) throws ChangeConflictException {
        long clientNumber = changeCapsule.getSequence();
        long serverNumber = fileManager.getLatestChangeSequenceNumber();

        //numbers should be equal for there to be no conflict
        if (clientNumber < serverNumber) {
            throw new ChangeConflictException("Error: newer version of ontology on server. Client must update to latest version before commiting new changes");
        }
        if (clientNumber > serverNumber) {
            throw new ChangeConflictException("Error: client has newer version than server. This should not be possible: something strange has happened. ");
        }
    }
}
