package changeServerPackage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.google.common.base.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 14, 2008
 * Time: 3:14:52 PM
 * Object to store all the metadata and actual data about a change. It can export/import itself
 * into a serialized JSON and/or XML format.
 */
public class ChangeCapsule {

    protected String username = null;
    //the timestamp refers to the time the server received the change, this fits better with the model of change and avoids complications between different timezones
    protected String timestamp = null; 
    protected String summary = "";
    //no initial value, a change must always have a ChangeObject
    protected List<String> changes;  
    //the ontology URIs each change applies to
    protected List<OWLOntologyID> changesOntologies;  
    //sequence number of this change (-1 = unassigned); this is assigned when the change is written to file on the server
    protected long sequence = -1;    
    //uri of the ontology this change object primarily applies to
    protected String ontologyURI = null;   
    //parser for use in converting XML OWL serialization into an actual OWL object
    private SAXParser parser = null;


    /**
     * create an empty change capsule for querying and updating. This
     * constructor should not be used for creating a changeCapsule for commiting
     * changes to a server
     */
    public ChangeCapsule() {}

    /** create a new change object from a list of changes */
    public ChangeCapsule(List<OWLOntologyChange> owlChanges) {
        changes = new ArrayList<String>(owlChanges.size());
        changesOntologies = new ArrayList<OWLOntologyID>(owlChanges.size());
        for (OWLOntologyChange cha : owlChanges) {
            // convert change to String
            String serializeChange = serializeChange(cha);
            if (!serializeChange.isEmpty()) {
            changes.add(serializeChange);

                changesOntologies.add(cha.getOntology().getOntologyID());
            }
        }
    }

    /** create a new change object from a JSON seralization */
    public ChangeCapsule(String changeSerialization) {
        // parse the JSON
        JSONObject jsonObj = (JSONObject) JSONValue.parse(changeSerialization);
        // extract the basic parameters
        username = (String) jsonObj.get(ChangeServer.username);
        timestamp = (String) jsonObj.get(ChangeServer.timestamp);
        summary = (String) jsonObj.get(ChangeServer.summary);
        sequence = (Long) jsonObj.get(ChangeServer.sequence);
        // convert array of JSON Change strings into an arraylist of strings
        JSONArray jsonArr = (JSONArray) jsonObj.get(ChangeServer.change);
        changes = new ArrayList<String>(jsonArr.size());
        for (Object cha : jsonArr) {
            changes.add((String) cha);
        }
        // convert array of ontologyURIs into arrayList of strings
        JSONArray jsonOnto = (JSONArray) jsonObj.get(ChangeServer.ontology);
        changesOntologies = new ArrayList<OWLOntologyID>(jsonArr.size());
        for (Object ont : jsonOnto) {
            changesOntologies.add(new OWLOntologyID(Optional.fromNullable(IRI
                    .create((String) ont)), Optional.<IRI> absent()));
        }

        //convert booleans of add/remove nature of change into the corresponding list object
        JSONArray jsonBools = (JSONArray) jsonObj.get(ChangeServer.addremove);
    }

    public String getUsername() {
        return username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSummary() {
        return summary;
    }

    /** returns the URI of the first change in this change capsule */
    public OWLOntologyID getOntologyURI() {
        if (ontologyURI == null && changesOntologies != null) {
            //either use the URI set in the object, or fetch the URI of the ontology that the first change is applied to
            return changesOntologies.get(0);
        }
        return ontologyURI;
    }

    /** sets the URI of the ontology to */
    public void setOntologyURI(OWLOntologyID uri) {
        ontologyURI = uri;
    }

    /** returns if this change capsule is empty, i.e. if it contains zero changes */
    public boolean empty() {
        return changes.isEmpty() || changesOntologies.isEmpty();
    }

    /**
     * returns the changes as a part of a new ontology (they still need to be
     * applied in order to take effect)
     */
    public List<OWLOntologyChange> getChangeOWL(OWLOntologyManager manager) {
        ArrayList<OWLOntologyChange> owlChanges = new ArrayList<OWLOntologyChange>(
                changes.size());
        // the iterator will always have the exact same number of objects as the
        // changes list, so this slight hack is perfectly safe
        Iterator<OWLOntologyID> relevantOntologies = changesOntologies
                .iterator();
        for (String cha : changes) {
            OWLOntology onto = manager.getOntology(relevantOntologies.next());
            // ontology this change is to be applied to
            OWLOntologyChange owlChange = deserializeChange(onto, cha);
            if (owlChange != null) {
                owlChanges.add(owlChange);
            }
        }
        return owlChanges;
    }

    /** returns the changes as a string serialization */
    public List<String> getChangeStrings() {
        return changes;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /** serializes this change as JSON */
    public String toJSON() {
        JSONArray arr = new JSONArray();
        for (String chaString : changes) {
            // add strings of changes to a JSON array
            arr.add(chaString);
        }
        JSONArray arrBools = new JSONArray();
        JSONArray arrOntologies = new JSONArray();
        for (OWLOntologyID ontString : changesOntologies) {
            arrOntologies.add(ontString.getOntologyIRI().get().toString());
        }
        JSONObject obj = new JSONObject();
        // put all the other parameters into a JSON object (hashmap)
        obj.put(ChangeServer.username, username);
        obj.put(ChangeServer.timestamp, timestamp);
        obj.put(ChangeServer.summary, summary);
        obj.put(ChangeServer.sequence, new Long(sequence));
        obj.put(ChangeServer.change, arr);
        obj.put(ChangeServer.addremove, arrBools);
        obj.put(ChangeServer.ontology, arrOntologies);
        return obj.toString();  // return a JSON serialization
    }

    /** returns the same JSON serialization as the toJSON method */
    @Override
    public String toString() {
        return toJSON();
    }

    /**
     * turns an OWLOntologyChange object into an XML string representation
     * 
     * @throws IOException
     */
    private String serializeChange(OWLOntologyChange change) {
        try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(out);
        stream.writeObject(change.getChangeData());
        stream.writeObject(change.getOntology().getOntologyID());
        stream.flush();
        return Base64.encodeBase64String(out.toByteArray());
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * return and reassemble the change from components; bit by bit, so the
     * changes and build upon each other
     */
    private OWLOntologyChange deserializeChange(OWLOntology ontology,
            String changeString) {
        try {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
Base64.decodeBase64(changeString)));
        OWLOntologyChangeData data = (OWLOntologyChangeData) in.readObject();
            OWLOntologyID id = (OWLOntologyID) in.readObject();
            if (ontology.getOntologyID().equals(id)) {
        return data.createOntologyChange(ontology);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
