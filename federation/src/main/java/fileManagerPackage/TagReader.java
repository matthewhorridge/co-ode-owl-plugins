package fileManagerPackage;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 18, 2008
 * Time: 2:26:43 PM
 * Reads the latest change that has been embeded into a tag
 */
public class TagReader {
    public static final String CHANGEAXIOMPREFIX = "includesUpToChange: ";
    OWLOntologyManager manager;
    OWLOntology ontology;

    public TagReader(File ontologyFile) throws OWLOntologyCreationException {
        IRI physicalURI = IRI.create(ontologyFile.toURI());
        manager = OWLManager.createOWLOntologyManager();

        //ask the manager to load the ontology
        ontology = manager.loadOntologyFromOntologyDocument(physicalURI);
    }

    public long getLatestChangeInTag() {
        long latestChange = -1;
        //find the relevant annotaiton axiom that records sequence numbers of changes
        Set<OWLAnnotation> annotations = ontology.getAnnotations();
        for (OWLAnnotation annotation : annotations) {
            String text = annotation.getValue().toString();
            if (text.startsWith(CHANGEAXIOMPREFIX)) {
                text = text.substring(CHANGEAXIOMPREFIX.length());
                latestChange = new Long(text);
            }
        }

        return latestChange;
    }
}
