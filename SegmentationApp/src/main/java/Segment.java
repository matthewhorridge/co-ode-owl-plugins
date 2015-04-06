import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.DeprecatedOWLEntityCollector;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/**
 * Created by IntelliJ IDEA.
 * User: Julian Seidenberg (j@deltaflow.com)
 * Date: Dec 3, 2007
 * Time: 11:31:36 AM
 * OWL-API based segmenter command-line application
 */
public class Segment {
    public String[] targets;
    OWLOntology oldOntology;
    OWLOntology newOntology;
    OWLOntologyManager oldManager;
    OWLOntologyManager newManager;

    Set<OWLClass> markedClasses = new HashSet<>(); //list of classes marked for inclusion in the extract
    Collection<OWLObjectPropertyExpression> markedObjectProperties = new HashSet<>(); //list of classes marked for inclusion in the extract
    Collection<OWLDataPropertyExpression> markedDataProperties = new HashSet<>(); //list of classes marked for inclusion in the extract
    Set<OWLIndividual> markedIndividuals = new HashSet<>(); //list of individuals marked for inclusion in the extract

    PropertyAxiomCollectionVisitor propertyVisitor;
    ClassAxiomCollectionVisitor classVisitor;
    IndividualAxiomCollectionVisitor individualVisitor;



    public Segment(String filename, String[] targets) throws OWLOntologyCreationException {
        this.targets = targets;

        //load old ontology
        oldManager = OWLManager.createOWLOntologyManager();
        oldOntology = oldManager.loadOntologyFromOntologyDocument(new File(
                filename));

        //create new empty ontology
        newManager = OWLManager.createOWLOntologyManager();
        SimpleIRIMapper mapper = new SimpleIRIMapper(oldOntology
                .getOntologyID().getOntologyIRI().get(),
                getNewFilenameURI(new File(filename)));
        newManager.addIRIMapper(mapper);
        newOntology = newManager.createOntology(oldOntology.getOntologyID()
                .getOntologyIRI().get());
    }

    /** creates a new name for the new ontology */
    protected IRI getNewFilenameURI(File oldURI) {
        //oldFilename = new File(oldFilename).getAbsolutePath();
        String oldFilename = oldURI.toURI().toString();
        int lastDot = oldFilename.lastIndexOf(".");
        String left = oldFilename.substring(0, lastDot);
        String right = oldFilename.substring(lastDot, oldFilename.length());

        String targetString = "";
        for(String target : targets) {
            targetString += target;
            targetString += "_";
        }
        targetString = targetString.substring(0, targetString.length()-1);  //cut off the last "_"
        String targetString2 = left+"-"+targetString;   //add the path and original filename to the target string

        if (targetString2.length() >= 200) {
            targetString2 = left+"-segment";
        }

        IRI saveURI = IRI.create(targetString2 + right);
        return saveURI;
    }

    /** saves the new ontology */
    protected void saveNewModel() throws OWLOntologyStorageException {
        newManager.saveOntology(newOntology);
    }

    /** recursively scans to old ontology for all classes to include in the segmentation */
    protected void scan() {
        OWLDataFactory factory = oldManager.getOWLDataFactory();

        //check that all target classes are in the ontology and create OWLClass objects from them
        OWLClass[] targetClasses = new OWLClass[targets.length];
        int i=0;

        for(String target : targets) {
            BidirectionalShortFormProviderAdapter bi = new BidirectionalShortFormProviderAdapter(oldManager, oldManager.getOntologies(), new SimpleShortFormProvider());
            for (OWLEntity ent : bi.getEntities(target)) {

                if (!oldOntology.containsClassInSignature(ent.asOWLClass()
                        .getIRI(), Imports.EXCLUDED)) {
                    System.err.println("Error: target class "+target+" not found in ontology.");
                } else {    //given class exists, so collect it in a new array
                    targetClasses[i] = factory.getOWLClass(IRI
                            .create(oldOntology.getOntologyID()
                                    .getOntologyIRI().get().toString()
                                    + "#" + target));
                    i++;
                }
            }
        }

        //collect all the classes that need to be included in the segment
        markedClasses.clear();
        markedObjectProperties.clear();
        markedDataProperties.clear();
        markedIndividuals.clear();

        for(OWLClass target : targetClasses) {
            ArrayList<OWLClass> targetList = new ArrayList<>(1);
            targetList.add(target);

            collectReferences(targetList); //get referenced classes of target
        }

        //System.out.println("markedClasses.size() = " + markedClasses.size());
        //System.out.println("markedClasses.size() = " + markedProperties.size());
    }

    /** collect all classes referenced by the target classes */
    private void collectReferences(ArrayList<OWLClass> oldRefs) {
        for(OWLClass target : oldRefs) {
            ArrayList<OWLClass> refs = new ArrayList<>(0);
            if (!markedClasses.contains(target)) {  //check that marked classes not already referenced to avoid unlimted recursion
                refs = getReferrals(target);

                markedClasses.add(target); //mark the target as visited
            }

            collectReferences(refs);    //also collect all classes referenced by these newly found classes (recursive call!)

            for(OWLClass toMark : refs) {
                if (!markedClasses.contains(toMark)) {
                    markedClasses.add(toMark);  //mark all the collected classes for segmentation (if they are not already included)
                }
            }


        }
    }

    /** get all named classes that the given class references" */
    private ArrayList<OWLClass> getReferrals(OWLClass cls) {//throws OWLException {
        ArrayList<OWLClass> referentClasses = new ArrayList<>();
        ArrayList<OWLObjectPropertyExpression> referentObjectProperties = new ArrayList<>();
        ArrayList<OWLDataPropertyExpression> referentDataProperties = new ArrayList<>();
        ArrayList<OWLIndividual> referentindividuals = new ArrayList<>();

        if (cls != null) {
            //collect individuals that this class is a type of
            Set<OWLClassAssertionAxiom> inds = oldOntology.getClassAssertionAxioms(cls);
            for(OWLClassAssertionAxiom ind : inds) {
                referentindividuals.add(ind.getIndividual());
            }


            DeprecatedOWLEntityCollector collector = new DeprecatedOWLEntityCollector();

            cls.accept(collector);          //collect all the references this class has to other classes

            //collect from all superclasses as well
            for (OWLClassExpression owlDescription : EntitySearcher
                    .getSuperClasses(cls, oldOntology)) {
                owlDescription.accept(collector);
            }

            //also collect from all equivalent classes
            Collection<OWLClassExpression> equivSet = EntitySearcher
                    .getEquivalentClasses(cls, oldOntology);
            for (Iterator<OWLClassExpression> iterator = equivSet.iterator(); iterator
                    .hasNext();) {
                OWLClassExpression owlDescription = iterator.next();
                owlDescription.accept(collector);
            }

            Set<OWLEntity> set = collector.getObjects();
            for (Iterator<OWLEntity> iterator = set.iterator(); iterator.hasNext();) {
                OWLEntity owlEntity = iterator.next();

                if (owlEntity instanceof OWLClass) {
                        referentClasses.add((OWLClass)owlEntity);
                    }

                if (owlEntity instanceof OWLObjectPropertyExpression) {
                    referentObjectProperties
                            .add((OWLObjectPropertyExpression) owlEntity);
                    }
                if (owlEntity instanceof OWLDataPropertyExpression) {
                    referentDataProperties
                            .add((OWLDataPropertyExpression) owlEntity);
                }

               /* if (owlEntity instanceof OWLIndividual) { //doesn't work for some reason, replaced with "getClassAssertionAxioms" call
                    if (owlEntity.getURI() != null && !referentindividuals.contains((OWLIndividual)owlEntity)) {
                        referentindividuals.add((OWLIndividual)referentindividuals);
                    }
                }*/
            }
        }

        //add all used properties to global list
        for (OWLObjectPropertyExpression newProperty : referentObjectProperties) {
            if (!markedObjectProperties.contains(newProperty)) {
                markedObjectProperties.add(newProperty);
                HashSet<OWLObjectPropertyExpression> superProperties = new HashSet<>();
                 //fill the set
                getSuperproperties(superProperties, newProperty);  

                for(OWLObjectPropertyExpression property : superProperties) {   //add all the supers from the set to the main list
                    if (!markedObjectProperties.contains(property)) {
                        markedObjectProperties.add(property);
                    }
                }
            }
        }
        for (OWLDataPropertyExpression newProperty : referentDataProperties) {
            if (!markedDataProperties.contains(newProperty)) {
                markedDataProperties.add(newProperty);
                //fill the set
                HashSet<OWLDataPropertyExpression> superProperties = new HashSet<>();
                getSuperproperties(superProperties, newProperty);

                for(OWLDataPropertyExpression property : superProperties) {   //add all the supers from the set to the main list
                    if (!markedDataProperties.contains(property)) {
                        markedDataProperties.add(property);
                    }
                }

            }
        }

        //add all used individuals to global list
        for(OWLIndividual newIndividual : referentindividuals) {
            markedIndividuals.add(newIndividual);
        }

        return referentClasses;
    }

    /** recursively get all super-properties of a given property */
    private void getSuperproperties(Set<OWLObjectPropertyExpression> set,
            OWLObjectPropertyExpression property) {
         //only get the supers if we haven't already do so (to avoid infinite loop in cyclic ontology)
        if (!set.contains(property)) {
            set.add(property);
            Collection<OWLObjectPropertyExpression> superProperites = EntitySearcher
                    .getSuperProperties(property, oldOntology);
            for (OWLObjectPropertyExpression superProperty : superProperites) {
                getSuperproperties(set, superProperty);
                }
            }
        }
    /** recursively get all super-properties of a given property */
    private void getSuperproperties(Set<OWLDataPropertyExpression> set,
            OWLDataPropertyExpression property) {
        if (!set.contains(property)) {
            // only get the supers if we haven't
            // already do so (to avoid infinite loop
            // in cyclic ontology)
            set.add(property);
            Collection<OWLDataPropertyExpression> superProperites = EntitySearcher
                    .getSuperProperties(property, oldOntology);
            for (OWLDataPropertyExpression superProperty : superProperites) {
                getSuperproperties(set, superProperty);
            }
        }
    }




    /** collect all axioms that are relevant for the new model */
    protected void segment() {
        //data structure to collect and store all releveant axioms
        propertyVisitor = new PropertyAxiomCollectionVisitor(markedObjectProperties,markedDataProperties, oldManager.getOWLDataFactory());

        for(OWLObjectPropertyExpression property : markedObjectProperties) {
            for (OWLObjectPropertyAxiom axiom : oldOntology.getAxioms(property, Imports.EXCLUDED)) {
                axiom.accept(propertyVisitor);  //collect all axioms from the marked properties that make sense to add to the new model
            }}
        for(OWLDataPropertyExpression property:markedDataProperties) {
            for (OWLDataPropertyAxiom axiom : oldOntology.getAxioms(property
                    .asOWLDataProperty(), Imports.EXCLUDED)) {
                    axiom.accept(propertyVisitor);  //collect all axioms from the marked properties that make sense to add to the new model
                }
            }


        classVisitor = new ClassAxiomCollectionVisitor(markedClasses, oldManager.getOWLDataFactory());

        for(OWLClass cls : markedClasses) {
            Set<OWLClassAxiom> cAxioms = oldOntology.getAxioms(cls, Imports.EXCLUDED);
            for(OWLClassAxiom axiom : cAxioms) {
                axiom.accept(classVisitor);   //collect all axioms from the marked classes that make sense to add to the new model
            }
        }

        individualVisitor = new IndividualAxiomCollectionVisitor(
                markedIndividuals, markedClasses,
                oldManager.getOWLDataFactory());

        for(OWLIndividual ind : markedIndividuals) {
            Set<OWLIndividualAxiom> iAxiom = oldOntology.getAxioms(ind, Imports.EXCLUDED);
            for(OWLIndividualAxiom axiom : iAxiom) {
                axiom.accept(individualVisitor);
            }
        }
    }

    /** re-build new ontology from collected axioms */
    protected void rebuild() {
        ArrayList<OWLOntologyChange> listOfNewAxioms = new ArrayList<>();

        //add all the axioms from the old ontology into a huge long list of AddAxiom changes
        addSetToList(listOfNewAxioms, propertyVisitor.getDeclarations());
        addSetToList(listOfNewAxioms, classVisitor.getDeclarations());
        addSetToList(listOfNewAxioms, propertyVisitor.getAxioms());
        addSetToList(listOfNewAxioms, classVisitor.getAxioms());
        addSetToList(listOfNewAxioms, individualVisitor.getAxioms());
        addSetToList(listOfNewAxioms, propertyVisitor.getAnnotations());
        addSetToList(listOfNewAxioms, classVisitor.getAnnotations());
        addSetToList(listOfNewAxioms, individualVisitor.getAnnotations());

        //apply adding all the new axioms to the new ontology
        try {
            newManager.applyChanges(listOfNewAxioms);
        } catch (OWLOntologyChangeException e) {
            e.printStackTrace();
        }
    }

    /** add a set to a list */
    private void addSetToList(Collection<OWLOntologyChange> list, Collection<? extends OWLAxiom> set) {
        for(OWLAxiom axiom : set) {
            list.add(new AddAxiom(newOntology, axiom));
        }
    }

    /** count the number of classes in the new ontology */
    public int getNewModelClassNumber() {
        Set<OWLClass> allClasses = newOntology.getClassesInSignature();
        return allClasses.size();
    }

    /** count the number of classes in the old ontology */
    public int getOldModelClassNumber() {
        Set<OWLClass> allClasses = oldOntology.getClassesInSignature();
        return allClasses.size();
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: \n java -jar segment.jar <filename.owl> targetclass1 [targetclass2 [...]]");
            System.exit(1);
        }

        /*String filename1 = args[0];
        int depth = new Integer(args[1]);
        if (depth < 0) depth = 1000000000;

        String namespace = args[2];
        */
        int j = 1;
        String[] targets = new String[args.length-1];
        while(j < args.length) {
            targets[j-1] = args[j];
            j++;
        }

        //try to load a file instead of assuming a class name
        ArrayList<String> newTargets = new ArrayList<>();
        if (targets.length == 1) {
            try {
                FileInputStream targetsFile = new FileInputStream(targets[0]);
                BufferedReader bufferedFile = new BufferedReader(new InputStreamReader(targetsFile));
                String line;

                while((line = bufferedFile.readLine()) != null) {
                    String trimmedLine = line.trim();
                    newTargets.add(trimmedLine);
                }
            } catch (IOException e) {
                //ignore exceptions
            }
        }
        if (newTargets.size() > 0) {
            targets = newTargets.toArray(new String[newTargets.size()]);
        }


            try {
                System.out.println("Loading model");
                Segment segmenter = new Segment(args[0], targets);  //filename and target class to extract

                int oldcount = segmenter.getOldModelClassNumber();

                System.out.println("Scanning model (model contains "+oldcount+" classes)");
                segmenter.scan();

                System.out.println("Segmenting model");
                segmenter.segment();

                System.out.println("Building new model");
                segmenter.rebuild();

                int newcount = segmenter.getNewModelClassNumber();

                System.out.println("Saving new model to disk (new model contains "+newcount+" classes)");
                segmenter.saveNewModel(); //write out the new (smaller) ontology using the filename specified in

            } catch (OWLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

    }
}

