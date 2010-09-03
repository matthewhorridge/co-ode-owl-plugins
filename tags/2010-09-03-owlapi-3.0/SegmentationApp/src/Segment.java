import org.semanticweb.owl.model.*;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.util.*;

import java.io.*;
import java.net.URI;
import java.util.*;

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

    Set<OWLClass> markedClasses = new HashSet<OWLClass>(); //list of classes marked for inclusion in the extract
    Set<OWLProperty> markedProperties = new HashSet<OWLProperty>(); //list of classes marked for inclusion in the extract
    Set<OWLIndividual> markedIndividuals = new HashSet<OWLIndividual>(); //list of individuals marked for inclusion in the extract

    PropertyAxiomCollectionVisitor propertyVisitor;
    ClassAxiomCollectionVisitor classVisitor;
    IndividualAxiomCollectionVisitor individualVisitor;



    public Segment(String filename, String[] targets) throws OWLOntologyCreationException {
        this.targets = targets;

        //load old ontology
        oldManager = OWLManager.createOWLOntologyManager();
        URI physicalURI = new File(filename).toURI();
        oldOntology = oldManager.loadOntologyFromPhysicalURI(physicalURI);

        //create new empty ontology
        newManager = OWLManager.createOWLOntologyManager();
        SimpleURIMapper mapper = new SimpleURIMapper(oldOntology.getURI(), getNewFilenameURI(physicalURI));
        newManager.addURIMapper(mapper);
        newOntology = newManager.createOntology(oldOntology.getURI());
    }

    /** creates a new name for the new ontology */
    protected URI getNewFilenameURI(URI oldURI) {
        //oldFilename = new File(oldFilename).getAbsolutePath();
        String oldFilename = oldURI.toString();
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

        URI saveURI = URI.create(targetString2 + right);
        return saveURI;
    }

    /** saves the new ontology */
    protected void saveNewModel() throws OWLOntologyStorageException {
        newManager.saveOntology(newOntology);
    }

    /** recursively scans to old ontology for all classes to include in the segmentation */
    protected void scan() throws OWLException {
        OWLDataFactory factory = oldManager.getOWLDataFactory();

        //check that all target classes are in the ontology and create OWLClass objects from them
        OWLClass[] targetClasses = new OWLClass[targets.length];
        int i=0;

        for(String target : targets) {
            BidirectionalShortFormProviderAdapter bi = new BidirectionalShortFormProviderAdapter(oldManager, oldManager.getOntologies(), new SimpleShortFormProvider());
            for (OWLEntity ent : bi.getEntities(target)) {

                if (!oldOntology.containsClassReference(ent.asOWLClass().getURI())) {
                    System.err.println("Error: target class "+target+" not found in ontology.");
                } else {    //given class exists, so collect it in a new array
                    targetClasses[i] = factory.getOWLClass(URI.create(oldOntology.getURI().toString()+"#"+target));
                    i++;
                }
            }
        }

        //collect all the classes that need to be included in the segment
        markedClasses.clear();
        markedProperties.clear();
        markedIndividuals.clear();

        for(OWLClass target : targetClasses) {
            ArrayList<OWLClass> targetList = new ArrayList<OWLClass>(1);
            targetList.add(target);

            collectReferences(targetList); //get referenced classes of target
        }

        //System.out.println("markedClasses.size() = " + markedClasses.size());
        //System.out.println("markedClasses.size() = " + markedProperties.size());
    }

    /** collect all classes referenced by the target classes */
    private void collectReferences(ArrayList<OWLClass> oldRefs) {
        for(OWLClass target : oldRefs) {
            ArrayList<OWLClass> refs = new ArrayList<OWLClass>(0);
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
        ArrayList<OWLClass> referentClasses = new ArrayList<OWLClass>();
        ArrayList<OWLProperty> referentProperties = new ArrayList<OWLProperty>();
        ArrayList<OWLIndividual> referentindividuals = new ArrayList<OWLIndividual>();

        if (cls != null) {
            //collect individuals that this class is a type of
            Set<OWLClassAssertionAxiom> inds = oldOntology.getClassAssertionAxioms(cls);
            for(OWLClassAssertionAxiom ind : inds) {
                referentindividuals.add(ind.getIndividual());
            }


            OWLEntityCollector collector = new OWLEntityCollector();

            cls.accept(collector);          //collect all the references this class has to other classes

            //collect from all superclasses as well
            Set<OWLDescription> superSet = cls.getSuperClasses(oldOntology);
            for (Iterator<OWLDescription> iterator = superSet.iterator(); iterator.hasNext();) {
                OWLDescription owlDescription = iterator.next();
                owlDescription.accept(collector);
            }

            //also collect from all equivalent classes
            Set<OWLDescription> equivSet = cls.getEquivalentClasses(oldOntology);
            for (Iterator<OWLDescription> iterator = equivSet.iterator(); iterator.hasNext();) {
                OWLDescription owlDescription = iterator.next();
                owlDescription.accept(collector);
            }

            Set<OWLEntity> set = collector.getObjects();
            for (Iterator<OWLEntity> iterator = set.iterator(); iterator.hasNext();) {
                OWLEntity owlEntity = iterator.next();

                if (owlEntity instanceof OWLClass) {
                    if (owlEntity.getURI() != null && !referentClasses.contains((OWLClass)owlEntity)) {
                        referentClasses.add((OWLClass)owlEntity);
                    }
                }

                if (owlEntity instanceof OWLProperty) {
                    if (owlEntity.getURI() != null && !referentProperties.contains((OWLProperty)owlEntity)) {
                        referentProperties.add((OWLProperty)owlEntity);
                    }
                }

               /* if (owlEntity instanceof OWLIndividual) { //doesn't work for some reason, replaced with "getClassAssertionAxioms" call
                    if (owlEntity.getURI() != null && !referentindividuals.contains((OWLIndividual)owlEntity)) {
                        referentindividuals.add((OWLIndividual)referentindividuals);
                    }
                }*/
            }
        }

        //add all used properties to global list
        for(OWLProperty newProperty : referentProperties) {
            if (!markedProperties.contains(newProperty)) {
                markedProperties.add(newProperty);

                HashSet<OWLProperty> superProperties = new HashSet<OWLProperty>();
                getSuperproperties(superProperties, newProperty);   //fill the set

                for(OWLProperty property : superProperties) {   //add all the supers from the set to the main list
                    if (!markedProperties.contains(property)) {
                        markedProperties.add(property);
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
    private void getSuperproperties(HashSet<OWLProperty> set, OWLProperty property) {
        if (!set.contains(property)) {  //only get the supers if we haven't already do so (to avoid infinite loop in cyclic ontology)
            Set superProperites = property.getSuperProperties(oldOntology);
            for(Object superProperty : superProperites) {
                if (superProperty instanceof OWLProperty) {
                    set.add((OWLProperty)superProperty);
                    getSuperproperties(set, (OWLProperty)superProperty);
                }
            }
        }
    }




    /** collect all axioms that are relevant for the new model */
    protected void segment() {
        //data structure to collect and store all releveant axioms
        propertyVisitor = new PropertyAxiomCollectionVisitor(markedProperties, oldManager.getOWLDataFactory());

        for(OWLProperty property : markedProperties) {
            if (property.isOWLDataProperty()) { //visit all data properites that were collected in phase 1
                Set<OWLDataPropertyAxiom> pAxioms = oldOntology.getAxioms(property.asOWLDataProperty());
                for(OWLDataPropertyAxiom axiom : pAxioms) {
                    axiom.accept(propertyVisitor);  //collect all axioms from the marked properties that make sense to add to the new model
                }
            }

            if (property.isOWLObjectProperty()) { //visit all object properties
                Set<OWLObjectPropertyAxiom> pAxioms = oldOntology.getAxioms(property.asOWLObjectProperty());
                for(OWLObjectPropertyAxiom axiom : pAxioms) {
                    axiom.accept(propertyVisitor);  //collect all axioms from the marked properties that make sense to add to the new model
                }
            }
        }

        classVisitor = new ClassAxiomCollectionVisitor(markedClasses, oldManager.getOWLDataFactory());

        for(OWLClass cls : markedClasses) {
            Set<OWLClassAxiom> cAxioms = oldOntology.getAxioms(cls);
            for(OWLClassAxiom axiom : cAxioms) {
                axiom.accept(classVisitor);   //collect all axioms from the marked classes that make sense to add to the new model
            }
        }

        individualVisitor = new IndividualAxiomCollectionVisitor(markedIndividuals, markedProperties, markedClasses, oldManager.getOWLDataFactory());

        for(OWLIndividual ind : markedIndividuals) {
            Set<OWLIndividualAxiom> iAxiom = oldOntology.getAxioms(ind);
            for(OWLIndividualAxiom axiom : iAxiom) {
                axiom.accept(individualVisitor);
            }
        }
    }

    /** re-build new ontology from collected axioms */
    protected void rebuild() {
        ArrayList<OWLOntologyChange> listOfNewAxioms = new ArrayList<OWLOntologyChange>();

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
    private void addSetToList(List<OWLOntologyChange> list, Set<? extends OWLAxiom> set) {
        for(OWLAxiom axiom : set) {
            list.add(new AddAxiom(newOntology, axiom));
        }
    }

    /** count the number of classes in the new ontology */
    public int getNewModelClassNumber() {
        Set<OWLClass> allClasses = newOntology.getReferencedClasses();
        return allClasses.size();
    }

    /** count the number of classes in the old ontology */
    public int getOldModelClassNumber() {
        Set<OWLClass> allClasses = oldOntology.getReferencedClasses();
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
        ArrayList<String> newTargets = new ArrayList<String>();
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

