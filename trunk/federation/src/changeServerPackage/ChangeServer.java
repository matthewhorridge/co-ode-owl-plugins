package changeServerPackage;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 14, 2008
 * Time: 1:59:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChangeServer {
    public static final String username = "username";
    public static final String timestamp = "timestamp";
    public static final String summary = "summary";
    public static final String change = "change";
    public static final String addremove = "bool";
    public static final String sequence = "sequence";
    public static final String ontology ="ontology";

    /** returns the latest change number for the given ontology URI */
    public String getLatestChangeNumber(String ontologyURI);

    /** returns a serialized verion of the change capsule requested for transfer to requesting client */
    public String getSpecificChange(String ontologyURI, Long sequenceNumber);

    /** Sets a changes timestamp and sequence number,
     * extracts the target ontology from a change object, and
     * stores the change in the list of changes for that "OntologyFileManager".<br><br>
     * Slight hack: take only the first change in the list of changes to be applied at once for
     * deciding which ontology to send this change to on the server) */
    public String recordChange(ChangeCapsule changeCapsule);

    /** creates a new baseline ontology file+folder on the server */
    public boolean createNewOntologyBaseline(String uri);
    
}
