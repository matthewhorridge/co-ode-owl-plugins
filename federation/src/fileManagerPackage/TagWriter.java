package fileManagerPackage;

import changeServerPackage.ChangeCapsule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.File;
import java.io.IOException;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 11, 2008
 * Time: 3:45:43 PM
 * Applies a series of changes to a baseline ontology to create a new "tag", i.e. version of the ontology with those changes applied
 */
public class TagWriter {
    public static final String TAGPREFIX = "tag";
    public static final String TAGEXTENSION = ".owl";
    OWLOntologyManager manager;
    OWLOntology latestOntology;
    File latestOntologyFile;    //latest tag or the baseline, if not tags have yet been created

    public TagWriter(File ontologyFile) throws OWLOntologyCreationException {
        latestOntologyFile = ontologyFile;
        URI physicalURI = ontologyFile.toURI();
        manager = OWLManager.createOWLOntologyManager();
        latestOntology = manager.loadOntologyFromPhysicalURI(physicalURI);
    }


    public void applyChanges(String changeFile) throws URISyntaxException, OWLOntologyChangeException {

    }


    public void applyChanges(ArrayList<ChangeCapsule> changes) throws URISyntaxException, OWLOntologyChangeException {
        for(ChangeCapsule cp : changes) {
            List<OWLOntologyChange> ontoChanges = cp.getChangeOWL(manager);
            manager.applyChanges(ontoChanges);
        }
    }


    /** creates a new file one count up from the latest file this object was instantiated with and saves it to disk.
     * Returns a reference to the new file */
    public File saveNewTag() throws OWLOntologyStorageException {
        OWLDataFactory df = manager.getOWLDataFactory();

        //increment the version count of the file name
        Integer sequenceNumber = -1;
        if (latestOntologyFile.getName().startsWith(TAGPREFIX)) {
            //only get the sequence number if there are already tags, otherwise just start sequence numbering at zero
            sequenceNumber = new Integer(latestOntologyFile.getName().substring(TAGPREFIX.length(), latestOntologyFile.getName().length()-TAGEXTENSION.length()));  //cut off the extension
        }

        sequenceNumber++;   //increase the sequence number count for the tag
        String sequenceString = ChangeWriter.addLeadingZeros(sequenceNumber.toString(), 5);
        String newFileName = TAGPREFIX + sequenceString + TAGEXTENSION;

        //remove existing change version annotation
        Set<OWLOntologyAnnotationAxiom> allAnnotations = latestOntology.getOntologyAnnotationAxioms();
        for(OWLOntologyAnnotationAxiom annotation: allAnnotations) {
            if (annotation.getAnnotation().getAnnotationURI().compareTo(OWLRDFVocabulary.OWL_VERSION_INFO.getURI()) == 0) {
                if (annotation.getAnnotation().getAnnotationValue() instanceof OWLConstant) {
                    String literal = ((OWLConstant)annotation.getAnnotation().getAnnotationValue()).getLiteral();
                    if (literal.startsWith(TagReader.CHANGEAXIOMPREFIX)) {
                        try {
                            manager.applyChange(new RemoveAxiom(latestOntology, annotation));
                        } catch (OWLOntologyChangeException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        //add an annotation to indicate the version of this tag
        OWLConstant cons = df.getOWLUntypedConstant(TagReader.CHANGEAXIOMPREFIX+sequenceNumber);
        OWLAnnotation anno = df.getOWLConstantAnnotation(OWLRDFVocabulary.OWL_VERSION_INFO.getURI(), cons);
        OWLOntologyAnnotationAxiom annoax = df.getOWLOntologyAnnotationAxiom(latestOntology, anno);

        try {
            manager.applyChange(new AddAxiom(latestOntology, annoax));
        } catch (OWLOntologyChangeException e) {
            e.printStackTrace();
        }

        //save the tag to disk
        File newFile = new File(latestOntologyFile.getParentFile().getAbsoluteFile().getPath(), newFileName);
        URI physicalURI2 = newFile.toURI();  //folder and new filename
        manager.saveOntology(latestOntology, new OWLXMLOntologyFormat(), physicalURI2);
        manager.removeOntology(latestOntology.getURI());

        return newFile;
    }
    

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TagWriter <ontology-base> <change-file1> <change-file2> ...");
            System.exit(1);
        }

        try {
            TagWriter writer = new TagWriter(new File(args[0]));
            ChangeReader cr = new ChangeReader(new File(args[1]));  //the base file passed in here is not taken into account, since the getChange method is directly called
            ArrayList<ChangeCapsule> changeCaps = new ArrayList<ChangeCapsule>();
            for(int i=1; i < args.length; i++) {
                ChangeCapsule cap = cr.getChange(new File(args[i]));
                changeCaps.add(cap);
            }

            writer.applyChanges(changeCaps);
            writer.saveNewTag();

        //} catch (IOException e) {
        //    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (OWLOntologyChangeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
