package test;

import java.net.*;
import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.semanticweb.owl.util.SimpleURIMapper;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.apibinding.OWLManager;
import changeServerPackage.ChangeCapsule;
import changeServerPackage.ApplyChangesServlet;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 14, 2008
 * Time: 2:09:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestClient {

    OWLOntologyManager manager = null;
    URI ontologyURI = null;
    OWLOntology ontology = null;
    OWLDataFactory factory = null;
    ArrayList<OWLOntologyChange> currentChanges = null;

    public TestClient() {

    }



    public void sendChangeToServer(ChangeCapsule changeCapsule) throws IOException {
        if (!changeCapsule.empty()) {

            //configure connection
            URL url = new URL ("http://"+InetAddress.getLocalHost().getHostName()+":8080/ChangeServer");// URL of CGI-Bin script.
            URLConnection urlConn = url.openConnection(); // URL connection channel.
            urlConn.setDoInput(true);  // Let the run-time system (RTS) know that we want input.
            urlConn.setDoOutput (true);// Let the RTS know that we want to do output.
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// Specify the content type.

            // Send POST output
            OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());

            String content = ApplyChangesServlet.PARAMETER_COMMAND + "=" + ApplyChangesServlet.COMMIT;
            content += "&"+ApplyChangesServlet.PARAMETER_CAPSULE+ "=" + URLEncoder.encode(changeCapsule.toJSON(), "UTF-8");

            wr.write(content);
            wr.flush();

            // Get response data.
            BufferedReader input = new BufferedReader (new InputStreamReader (urlConn.getInputStream()));
            String str;
            while (null != ((str = input.readLine()))) {
                System.out.println(str);
            }

            //cleanup
            wr.close();
            input.close();
        }
    }




    /** deletes changes from client, so they can be downloaded from the server again and be property integrated (in the right order) */
    private void undoChanges(List<OWLOntologyChange> existingChanges) throws OWLOntologyChangeException {
        ArrayList<OWLOntologyChange> changesToUndo = new ArrayList<OWLOntologyChange>(existingChanges.size());

        int i = existingChanges.size()-1;
        while(i >= 0) {
            OWLOntologyChange changeUndo = existingChanges.get(i);
            ReverseChangeGenerator gen = new ReverseChangeGenerator();
            changeUndo.accept(gen);
            changesToUndo.add(gen.getReverseChange());
            i--;
        }

        manager.applyChanges(changesToUndo);
    }




    public void createNewTestOntology() throws OWLOntologyChangeException, OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();

        ontologyURI = URI.create("http://www.co-ode.org/ontologies/plugin/protege/federation/test/testont.owl");

        URI physicalURI = new File("tempOnto.owl").toURI();
        SimpleURIMapper mapper = new SimpleURIMapper(ontologyURI, physicalURI);
        manager.addURIMapper(mapper);

        ontology = manager.createOntology(ontologyURI);
        factory = manager.getOWLDataFactory();

        manager.addOntologyChangeListener(new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
                currentChanges = new ArrayList<OWLOntologyChange>(changes.size());  //convert all changes to XML
                for(OWLOntologyChange c: changes) {
                    currentChanges.add(c);
                }
            }
        });
    }

    public ArrayList<OWLOntologyChange> applyNewChange() throws OWLOntologyChangeException {
        OWLClass clsA = factory.getOWLClass(URI.create(ontologyURI + "#A"));
        OWLClass clsB = factory.getOWLClass(URI.create(ontologyURI + "#B"));
        OWLClass clsC = factory.getOWLClass(URI.create(ontologyURI + "#C"));
        OWLClass clsD = factory.getOWLClass(URI.create(ontologyURI + "#D"));
        OWLClass clsE = factory.getOWLClass(URI.create(ontologyURI + "#E"));
        OWLClass clsF = factory.getOWLClass(URI.create(ontologyURI + "#F"));
        OWLClass clsG = factory.getOWLClass(URI.create(ontologyURI + "#G"));
        OWLClass clsH = factory.getOWLClass(URI.create(ontologyURI + "#H"));
        OWLClass clsI = factory.getOWLClass(URI.create(ontologyURI + "#I"));
        OWLClass clsJ = factory.getOWLClass(URI.create(ontologyURI + "#J"));
        OWLClass clsK = factory.getOWLClass(URI.create(ontologyURI + "#K"));
        OWLClass clsL = factory.getOWLClass(URI.create(ontologyURI + "#L"));
        OWLClass clsM = factory.getOWLClass(URI.create(ontologyURI + "#M"));

        OWLAxiom axiom = factory.getOWLSubClassAxiom(clsA, clsB);
        OWLAxiom axiom2 = factory.getOWLSubClassAxiom(clsA, clsC);
        OWLAxiom axiom3 = factory.getOWLSubClassAxiom(clsA, clsD);
        OWLAxiom axiom4 = factory.getOWLSubClassAxiom(clsA, clsE);
        OWLAxiom axiom5 = factory.getOWLSubClassAxiom(clsA, clsF);
        OWLAxiom axiom6 = factory.getOWLSubClassAxiom(clsA, clsG);
        OWLAxiom axiom7 = factory.getOWLSubClassAxiom(clsA, clsH);
        OWLAxiom axiom8 = factory.getOWLSubClassAxiom(clsA, clsI);
        OWLAxiom axiom9 = factory.getOWLSubClassAxiom(clsA, clsJ);
        OWLAxiom axiom10 = factory.getOWLSubClassAxiom(clsA, clsK);
        OWLAxiom axiom11 = factory.getOWLSubClassAxiom(clsA, clsL);
        OWLAxiom axiom12  = factory.getOWLSubClassAxiom(clsA, clsM);

        ArrayList<AddAxiom> changes = new ArrayList<AddAxiom>();
        changes.add(new AddAxiom(ontology, axiom));
        changes.add(new AddAxiom(ontology, axiom2));
        changes.add(new AddAxiom(ontology, axiom3));
        changes.add(new AddAxiom(ontology, axiom4));
        changes.add(new AddAxiom(ontology, axiom5));
        changes.add(new AddAxiom(ontology, axiom6));
        changes.add(new AddAxiom(ontology, axiom7));
        changes.add(new AddAxiom(ontology, axiom8));
        changes.add(new AddAxiom(ontology, axiom9));
        changes.add(new AddAxiom(ontology, axiom10));
        changes.add(new AddAxiom(ontology, axiom11));
        changes.add(new AddAxiom(ontology, axiom12));

        manager.applyChanges(changes);

        return currentChanges; //changes are recorded by the visitor
    }



    /** query server the the latest change sequence number on the server */
    public long getLatestChangeSequenceNumber() {
        return 0;
    }


    /** download all changes that aren't already integrated into the current ontology */
    public void updateOntology() {

    }


    /** queries the current ontology as to it's change sequence number */
    public long getOntologySequenceNumber(OWLOntology ontology) {
        return -1;
    }


    public static void main(String[] args) {
        TestClient client = new TestClient();

        try {
            //create an ontology and make some teset changes
            client.createNewTestOntology();
            List<OWLOntologyChange> changeObjects = client.applyNewChange();

            //record changes in changeCapsule object
            ChangeCapsule changeSet = new ChangeCapsule(changeObjects); //create new object encapsulating all that changes made to the ontology
            changeSet.setUsername(InetAddress.getLocalHost().getHostName());
            changeSet.setSequence(1);
            changeSet.setSummary("This is a test summary for a change");

            //publish changes
            client.sendChangeToServer(changeSet);
            client.undoChanges(changeObjects);//delete changes from client (they will be downloaded again in the next step)


            //query for new changes


        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWLOntologyChangeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}