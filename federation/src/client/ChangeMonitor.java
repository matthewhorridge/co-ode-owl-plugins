package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import test.ReverseChangeGenerator;
import fileManagerPackage.TagReader;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 12, 2008
 * Time: 3:26:01 PM
 * Intercepts changes and/or harvests them from a protege history manager. These changes are stored and available for quick
 * synchronizing with the server. Changes can also be saved out to file (and reloaded) from this object.
 */
public class ChangeMonitor implements OWLOntologyChangeListener {
    public List<OWLOntologyChange> recordedChanges = new ArrayList<OWLOntologyChange>();
    protected OWLOntology ontology;

    private boolean active = true; //sets if this listener is currently actively listening for changes ('true' by default)

    /** initiallizes a change monitor object with an existing list of changes (from the history manager, or elsewhere) */
    public ChangeMonitor(OWLOntology ontology, List<OWLOntologyChange> initialChanges) {
        this.ontology = ontology;

        if (ontology != null && initialChanges != null) {
            recordedChanges.addAll(initialChanges);
        }
    }

    /** creates an empty change monitor object */
    public ChangeMonitor(OWLOntology ontology) {
        this.ontology = ontology;
    }

    /** returns the version/sequence number of the local ontology monitored by this object */
    public Long getLatestVersionNumber() {
        return getOntologySequenceNumber(ontology);
    }

    /* returns the stored changes for publishing */
    public List<OWLOntologyChange> getChanges() {
        return recordedChanges;
    }



    /** read the change sequence number of an ontology */
    protected Long getOntologySequenceNumber(OWLOntology ontology) {
        Long number = null;
        Set<OWLAnnotation> allAnnotations = ontology.getAnnotations();
        for (OWLAnnotation annotation : allAnnotations) {
            if (annotation.getProperty().getIRI()
                    .compareTo(OWLRDFVocabulary.OWL_VERSION_INFO.getIRI()) == 0) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    String literal = ((OWLLiteral) annotation.getValue())
                            .getLiteral();
                    if (literal.startsWith(TagReader.CHANGEAXIOMPREFIX)) {
                        number = new Long(literal.substring(TagReader.CHANGEAXIOMPREFIX.length()));
                    }
                }
            }
        }
        return number;
    }

    /** listener method that records changes as they are made */
    @Override
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
        if (active) {
            for(OWLOntologyChange change : changes) {
                if (change.getOntology().equals(ontology)) { //only monitor changes on this object's ontology
                    recordedChanges.add(change);
                }
            }
        }
    }

    /** sets whether or not this listener temporary stops listening to changes being made to the ontology */
    public void setEnabled(boolean enabled) {
        active = enabled;
    }

    /** returns if the listener is currently listening to change being made to the ontology */
    public boolean enabled() {
        return active;
    }

    /** deletes the history of changes and starts recording anew */
    public void clearChanges() {
        recordedChanges.clear();
    }

    /** undos and deletes all changes that are currently stored in this changeMonitor for this ontology */
    public void undoAndDeleteChanges(OWLOntologyManager manager) throws OWLOntologyChangeException {
        //disable monitoring
        setEnabled(false);

        //undo changes
        ArrayList<OWLOntologyChange> undoChanges = new ArrayList<OWLOntologyChange>(recordedChanges.size());
        for(OWLOntologyChange change : recordedChanges) {
            ReverseChangeGenerator gen = new ReverseChangeGenerator();
            change.accept(gen);
            // Reverse the order
            undoChanges.add(0, gen.getReverseChange());
        }
        // Apply the undo changes
        manager.applyChanges(undoChanges);

        //delete history
        recordedChanges.clear();

        //reenable monitoring
        setEnabled(true);
    }
}
