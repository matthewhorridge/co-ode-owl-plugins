package org.protege.federation;

import client.ChangeMonitor;
import client.OperationsClient;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyChangeException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import changeServerPackage.ChangeConflictException;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 19, 2008
 * Time: 4:41:39 PM
 * Interface that defines what should occur when an ontology is about to be published to the server from protege 4
 */
public interface VersioningStrategy {

    /** returns if the local ontology is dirty, i.e. if it has been modified outside of the control
     * of the Federation plug-in's change tracking
     */
    public boolean isDirty() throws IOException, OWLOntologyCreationException, OWLOntologyChangeException;

    /** returns if the local ontology is up to date, i.e. if it has the same
     *  sequence number as the latest version on the server */
    public boolean isUpToDate() throws IOException, ChangeConflictException;

    /** based upon the "upToDate" and "dirty" nature of the local ontology, execute one of four
     * specific versioning strategies (in the non-locking case).
     */
    public String bringUpToDate(boolean dirty, boolean upToDate)  throws IOException, UnsupportedEncodingException, OWLOntologyCreationException, OWLOntologyChangeException, URISyntaxException;

    //protected boolean updateVersionAnnotation(Long newVersionNumber);

    /** Publishes the changes currenty managed by the Versioning Strategy, then deletes the monitored changes
     * and re-start monitoring from a clean slate.
     */
    public String publishChanges(String summary) throws IOException;
}
