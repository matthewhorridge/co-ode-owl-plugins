package org.protege.federation;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;
import client.ChangeMonitor;
import client.OperationsClient;

import java.io.IOException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.net.URISyntaxException;
import java.net.URI;

import changeServerPackage.ChangeConflictException;
import changeServerPackage.ChangeCapsule;
import fileManagerPackage.TagReader;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 19, 2008
 * Time: 4:41:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class VersioningStrategy_NonLocking implements VersioningStrategy {
    protected ChangeMonitor changeMonitor;
    protected OperationsClient operationsClient;
    private OWLOntology localOntology;
    private OWLOntologyManager manager;

    private Boolean isClean = null;     //once loaded we can be sure that an ontology stays clean, which allows us to avoid unnecessary dirty checks

    /** give the versioning strategy all the necessary objects to do the strategizing and also
     * set it to a specific ontology */
    public VersioningStrategy_NonLocking(OWLOntology ontology, OperationsClient operationsClient, ChangeMonitor changeMonitor, OWLOntologyManager manager) {
        this.operationsClient = operationsClient;
        this.changeMonitor = changeMonitor;
        this.localOntology = ontology;
        this.manager = manager;
    }

    /** return if the local ontology model is dirty (has been modified externally) */
    public boolean isDirty() throws IOException, OWLOntologyCreationException, OWLOntologyChangeException {
        boolean dirty = false;
        if (isClean == null || !isClean) {
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();   //new manager for the temporary ontology

            //download tag corresponding to current version from the server
            // (don't store on the client, since then we would need to save off a new copy of the ontology
            // every time we download an update, unnecessarily degrading performance)
            File downloadedTag = operationsClient.downloadSpecificTag(localOntology, changeMonitor.getLatestVersionNumber());
            OWLOntology downloadedOntology = tempManager.loadOntologyFromPhysicalURI(downloadedTag.toURI());

            //apply changes current in memory (from history manager+change monitor) to downloaded tag
            tempManager.applyChanges(changeMonitor.getChanges());

            //compare local memory ontology with just downloaded+changes tag from server
            Set<OWLAxiom> localAxioms = localOntology.getAxioms();
            Set<OWLAxiom> downloadedAxioms = downloadedOntology.getAxioms();

            //return if the local model is dirty
            dirty = !localAxioms.equals(downloadedAxioms);   //if they are equal, it is clean

            if (!dirty) isClean = true; //for short-cutting future calls of this method
        }
        return dirty;
    }


    /** compare server version number with local client side version number */
    public boolean isUpToDate() throws IOException, ChangeConflictException {
        Long serverNumber = operationsClient.getLatestVersionNumber(localOntology);  //version from servser
        Long clientNumber = changeMonitor.getLatestVersionNumber(); //client's version

        if (clientNumber > serverNumber) throw new ChangeConflictException("Server has lower sequence number than client. This should be possible.");

        if (clientNumber == serverNumber) return true;
        else return false;  //client < server = not up to date
    }


    /** Update the current ontology's version annotation tag to the (correct) most recent version passed in as a parameter
     * @return Returns whether the operation was successful or not
     */
    protected boolean updateVersionAnnotation(Long newVersionNumber) {
        boolean success = false;

        if (newVersionNumber != null) {
            changeMonitor.setEnabled(false);    //don't record the annotation change as an action / change, since it might be done in a whole variety of ways besides annotation

            OWLDataFactory df = manager.getOWLDataFactory();

            //remove existing change version annotation
            Set<OWLOntologyAnnotationAxiom> allAnnotations = localOntology.getOntologyAnnotationAxioms();
            for(OWLOntologyAnnotationAxiom annotation: allAnnotations) {
                if (annotation.getAnnotation().getAnnotationURI().compareTo(OWLRDFVocabulary.OWL_VERSION_INFO.getURI()) == 0) {
                    if (annotation.getAnnotation().getAnnotationValue() instanceof OWLConstant) {
                        String literal = ((OWLConstant)annotation.getAnnotation().getAnnotationValue()).getLiteral();
                        if (literal.startsWith(TagReader.CHANGEAXIOMPREFIX)) {
                            try {
                                manager.applyChange(new RemoveAxiom(localOntology, annotation));
                            } catch (OWLOntologyChangeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            //add an annotation to indicate the version of this tag
            OWLConstant cons = df.getOWLUntypedConstant(TagReader.CHANGEAXIOMPREFIX+newVersionNumber);
            OWLAnnotation anno = df.getOWLConstantAnnotation(OWLRDFVocabulary.OWL_VERSION_INFO.getURI(), cons);
            OWLOntologyAnnotationAxiom annoax = df.getOWLOntologyAnnotationAxiom(localOntology, anno);

            try {
                manager.applyChange(new AddAxiom(localOntology, annoax));
                success = true;
            } catch (OWLOntologyChangeException e) {
                e.printStackTrace();
            }

            changeMonitor.setEnabled(true);    //re-enable change monitoring
        } else {
            success = true; //if there is no new version number to update to, there is nothing to do, so we have a successful operation
        }
        return success;
    }

    /** Returns the axiom which are different between two ontologies. That is:
     * those axioms in the tag that are not in the local ontology */
    private List<OWLAxiom> generateDiff(OWLOntology local, OWLOntology tag) {
        Set<OWLAxiom> localAxioms = local.getAxioms();
        Set<OWLAxiom> tagAxioms = tag.getAxioms();

        List<OWLAxiom> diff = new ArrayList<OWLAxiom>();
        for(OWLAxiom axiom : tagAxioms) {
            if (!localAxioms.contains(axiom)) {
                diff.add(axiom);
            }
        }

        return diff;
    }

    /** Publishes the change set current stored in the ChangeMonitor (should only be called
     * if the version is clean and up-to-date) */
    public String publishChanges(String summary) throws IOException {
        //OWLOntology ontology = manager.getOWLOntologyManager().getOntology(new URI(ontoURL));
        String returnString = operationsClient.commitChangestoServer(localOntology, changeMonitor.getLatestVersionNumber(), summary, changeMonitor.getChanges());

        changeMonitor.clearChanges();  //start recording changes from a clean slate
        updateVersionAnnotation(changeMonitor.getLatestVersionNumber()+1);  //update version count

        return returnString;
    }


    /** Brings this object's ontology up to date, by whatever means is necessary.
     * the exact sequence of operations depends on whether or not the ontology is dirty or outdated.
     * in the end the user will have to check for any possible conflicts that they have introduced since
     * the last update.
     */
    public String bringUpToDate(boolean dirty, boolean upToDate) throws IOException, UnsupportedEncodingException, OWLOntologyCreationException, OWLOntologyChangeException, URISyntaxException {
        String messageString = null;  //if the user must check for conflicts, return a status alert in this string

        if (dirty) {   //dirty and outdated/up-to-date
            //download the latest tag from the server
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();    //new ontology manager for the temporary downloaded ontology
            File tag = operationsClient.downloadLatestTag(localOntology);
            OWLOntology tagOntology = tempManager.loadOntologyFromPhysicalURI(tag.toURI());

            //generate diff: latest server's+mem
            List<OWLAxiom> toAdd = generateDiff(localOntology, tagOntology);   //what do I need to get from local to tag?
            List<OWLAxiom> toDelete = generateDiff(tagOntology, localOntology);   //what do I need to get from tag to local?

            //keep memory copy loaded
            //apply locally w/conf.res.
            List<OWLOntologyChange> changesToApply = new ArrayList<OWLOntologyChange>();
            for(OWLAxiom addMe : toAdd) {   //add first, then delete
                changesToApply.add(new AddAxiom(localOntology, addMe));
            }
            for(OWLAxiom deleteMe : toDelete) {
                changesToApply.add(new RemoveAxiom(localOntology, deleteMe));
            }

            changeMonitor.setEnabled(true); //make sure we are recording changes here, because these will need to be published to server
            manager.applyChanges(changesToApply);   //apply and save the changes in the ChangeMonitor

            //no need to check for errors, if we already up to date, otherwise ask user to check for errors
            if (upToDate) messageString = null;
            else messageString = "Recovered from dirty local ontology.\n Integreated changes from latest version of the ontology on the server.\n Please check for any errors (dangling references, etc.) in the ontology.";

            //clean up
            tempManager.removeOntology(tagOntology.getURI());
            tag.deleteOnExit();
            isClean = true; //we are now garanteed to be stay clean (until Protege is quit and reloaded

            //no need to update version number manually, since this will have been done as part of the change synchronizing/diff from that downloaded tag
        }

        if (!dirty && !upToDate) {  //clean and outdated
            //save monitored changes
            List<OWLOntologyChange> changes = changeMonitor.getChanges();
            List<OWLOntologyChange> copyOfChanges = new ArrayList<OWLOntologyChange>();
            for(OWLOntologyChange change : changes) {
                copyOfChanges.add(change);
            }

            //undo all changes
            changeMonitor.undoAndDeleteChanges(manager);

            //download missing server changes need to update to latest version
            Long currentVersionCounter = changeMonitor.getLatestVersionNumber();
            Long serverVersion = operationsClient.getLatestVersionNumber(localOntology);

            List<OWLOntologyChange> serverChanges = new ArrayList<OWLOntologyChange>();  //all changes from the server wrapped into one long list
            StringBuffer updateDetails = new StringBuffer();
            while(currentVersionCounter < serverVersion) {
                currentVersionCounter++;   //start downloading latest version
                ChangeCapsule capsule = operationsClient.getSpecificChange(localOntology, currentVersionCounter);

                //build string to notify user of all changes which have taken place
                updateDetails.append("\n user: ");
                updateDetails.append(capsule.getUsername());
                updateDetails.append("; time: ");
                updateDetails.append(capsule.getTimestamp());
                updateDetails.append("; summary: ");
                updateDetails.append(capsule.getSummary());

                //collect all the changes into one long transaction (easy to undo in a single click of the back/undo button)
                List<OWLOntologyChange> singleServerChange = capsule.getChangeOWL(manager);
                serverChanges.addAll(singleServerChange);
            }

            //apply server changes to memory model
            changeMonitor.setEnabled(false);   //disable monitoring of changes while integrating the changes from the server
            manager.applyChanges(serverChanges);

            //apply saved changes to memory model (i.e. redo what the undo removed earlier)
            changeMonitor.setEnabled(true); //re-enable change recording, because these re-applied changes haven't been published to the server yet
            manager.applyChanges(copyOfChanges);

            //(with conflict resolution, while monitoring)
            //[now we are up-to-date]
            boolean successInVersionUpdate = updateVersionAnnotation(serverVersion);    //update annotation to match state of the ontology version

            //need to check for errors introduced during the re-application of the copyOfChanges
            messageString = "The ontology has been updated with the following changes from the server:\n";
            if (!successInVersionUpdate) messageString += "(warning: ontology version annotation could not be updated)\n";
            messageString += updateDetails;
            messageString += "\n\nPlease check for any errors (dangling references, etc.) in the ontology";
        }

        return messageString;
    }

}
