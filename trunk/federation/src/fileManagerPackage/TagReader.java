package fileManagerPackage;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import java.io.File;
import java.net.URI;
import java.util.Set;

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
        URI physicalURI = ontologyFile.toURI();
        manager = OWLManager.createOWLOntologyManager();

        //ask the manager to load the ontology
        ontology = manager.loadOntologyFromPhysicalURI(physicalURI);
    }

    public long getLatestChangeInTag() {
        long latestChange = -1;
        //find the relevant annotaiton axiom that records sequence numbers of changes
        Set<OWLOntologyAnnotationAxiom> annotations = ontology.getOntologyAnnotationAxioms();
        for(OWLOntologyAnnotationAxiom annotation: annotations) {
            String text = annotation.getAnnotation().getAnnotationValueAsConstant().getLiteral();
            if (text.startsWith(CHANGEAXIOMPREFIX)) {
                text = text.substring(CHANGEAXIOMPREFIX.length());
                latestChange = new Long(text);
            }
        }

        return latestChange;
    }
}
