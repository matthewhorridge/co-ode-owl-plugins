package client;

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.apibinding.OWLManager;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 11, 2008
 * Time: 11:56:59 AM
 * A set of methods to evaluate if multi-user conflict situation exists, resolve the conflict and publish local changes to a server
 */
public class ConflictManagerClient {
    protected OperationsClient operationsClient;
    protected ChangeMonitor changeMonitor;
    protected OWLOntologyManager manager;


    private OWLOntology cachedTag = null;
    private File cachedFile = null;

    public ConflictManagerClient(OperationsClient operationsClient, ChangeMonitor changeMonitor) {
        this.operationsClient = operationsClient;
        this.manager = OWLManager.createOWLOntologyManager();
        this.changeMonitor = changeMonitor;
    }

    /** diffs the cached tag copy of an ontology with the monitored
     * local set of changes held in the history manager.
     * @return returns true if the monitored set is dirty (out of date) and the server's state should be used
     */
    public boolean dirtyLocalCopy(OWLOntology currentlyLoaded) throws OWLOntologyCreationException, IOException {
        boolean dirtyLocal = false;

        File latestTagFile = operationsClient.downloadLatestTag(currentlyLoaded);   //download the latest tag, or return the existing latest tag, if the latest is already downloaded
        OWLOntology latestTag = loadOntologyFromTag(latestTagFile);

        Set<OWLAxiom> currentAxioms = currentlyLoaded.getAxioms();
        Set<OWLAxiom> tagsAxioms = latestTag.getAxioms();

        if (currentAxioms.size() != tagsAxioms.size()) {
            dirtyLocal = true;
        } else {
            if (!currentAxioms.equals(tagsAxioms)) dirtyLocal = true;
            else dirtyLocal = false;    //same axioms in each ontology, so we have a clean local copy
        }

        return dirtyLocal;
    }


    /** returns whether the current view of the ontology on the client is up to date, i.e. if it includes the latest changes from the server
     * @return returns true if the local copy is up to date, false otherwise
     */
    public boolean upToDate(OWLOntology ontology) throws IOException {
        if (operationsClient.getLatestVersionNumber(ontology) > changeMonitor.getLatestVersionNumber()) {
            return false;   //server has higher version, so local copy is out of date
        } else {
            return true;    //we are up to date
        }
    }


    /** Load an ontology from a file, or return an already loaded file (only keeps one ontology in memory at a time) */
    private OWLOntology loadOntologyFromTag(File tagFile) throws OWLOntologyCreationException {
        if (cachedTag == null) {
            cachedTag = manager.loadOntologyFromPhysicalURI(tagFile.toURI());   //save a cached copy of the ontology
            cachedFile = tagFile;
        } else {
            if (cachedFile.toURI().toString().compareToIgnoreCase(tagFile.toURI().toString()) == 0) {
                //trying to load the cached ontlogy again, so just return the cached version
            } else {
                manager.removeOntology(cachedTag.getURI()); //unload the existing cached ontology to free up memory
                cachedTag = manager.loadOntologyFromPhysicalURI(tagFile.toURI());   //save a cached copy of the new ontology
                cachedFile = tagFile;   //update the cached filename
            }
        }

        return cachedTag;
    }

}
