package changeServerPackage;

import fileManagerPackage.OntologyFileManager;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 5, 2008
 * Time: 1:52:49 PM
 *  checks if there is a conflict between a "commit" submission and the existing data on the server
 *
 */
public interface ConflictDetector {
    /** Checks for a conflict between a given new changeCapsule and the existing data on the server
     * (accessed via the fileManager object). Throws an exception if a conflict occurs. */
    public void check(ChangeCapsule changeCapsule, OntologyFileManager fileManager) throws ChangeConflictException;
}
