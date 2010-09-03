package changeServerPackage;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.vocab.Namespaces;
import org.coode.xml.XMLWriterNamespaceManager;
import org.coode.xml.XMLWriter;
import org.coode.xml.XMLWriterImpl;
import org.coode.owlapi.owlxml.renderer.OWLXMLWriter;
import org.coode.owlapi.owlxml.renderer.OWLXMLObjectRenderer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.StringWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.coode.owl.owlxmlparser.OWLXMLParserHandler;
import org.coode.owl.owlxmlparser.OWLXMLParserException;
import changeServerPackage.parserUtil.OWLXMLSerializationParserHandler;


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
    protected String timestamp = null;  //the timestamp refers to the time the server received the change, this fits better with the model of change and avoids complications between different timezones
    protected String summary = "";

    protected List<String> changes;  //no initial value, a change must always have a ChangeObject
    protected List<Boolean> changesAdd; //records if a change adds an axiom or removes it
    protected List<String> changesOntologies;   //the ontology URIs each change applies to

    protected long sequence = -1;    //sequence number of this change (-1 = unassigned); this is assigned when the change is written to file on the server
    protected String ontologyURI = null;   //uri of the ontology this change object primarily applies to

    private SAXParser parser = null;    //parser for use in converting XML OWL serialization into an actual OWL object


    /** create an empty change capsule for querying and updating. This constructor should not be used for creating a changeCapsule for commiting changes to a server */
    public ChangeCapsule() {
        
    }


    /** create a new change object from a list of changes */
    public ChangeCapsule(List<OWLOntologyChange> owlChanges) {
        changes = new ArrayList<String>(owlChanges.size());
        changesAdd = new ArrayList<Boolean>(owlChanges.size());
        changesOntologies = new ArrayList<String>(owlChanges.size());

        for(OWLOntologyChange cha : owlChanges) {
            changes.add(serializeChange(cha));  //convert change to String

            changesOntologies.add(cha.getOntology().getURI().toASCIIString());  //use only US-ASCII characters in string

            if (cha instanceof AddAxiom) {  //record if a change is an Add or Remove
                changesAdd.add(true);        //true = add
            } else if (cha instanceof RemoveAxiom) {
                changesAdd.add(false);  //false = remove
            }
        }
    }

    /** create a new change object from a JSON seralization */
    public ChangeCapsule(String changeSerialization) {
        //parse the JSON
        JSONObject jsonObj = (JSONObject) JSONValue.parse(changeSerialization);

        //extract the basic parameters
        username = (String)jsonObj.get(ChangeServer.username);
        timestamp = (String)jsonObj.get(ChangeServer.timestamp);
        summary = (String)jsonObj.get(ChangeServer.summary);
        sequence = ((Long)jsonObj.get(ChangeServer.sequence));

        //convert array of JSON Change strings into an arraylist of strings
        JSONArray jsonArr = (JSONArray)jsonObj.get(ChangeServer.change);
        changes = new ArrayList<String>(jsonArr.size());
        //String[] changesArray = (String[])jsonArr.toArray(new String[jsonArr.size()]);
        for(Object cha : jsonArr) {
            changes.add((String)cha);
        }

        //convert array of ontologyURIs into arrayList of strings
        JSONArray jsonOnto = (JSONArray)jsonObj.get(ChangeServer.ontology);
        changesOntologies = new ArrayList<String>(jsonArr.size());
        for(Object ont : jsonOnto) {
            changesOntologies.add((String)ont);
        }

        //convert booleans of add/remove nature of change into the corresponding list object
        JSONArray jsonBools = (JSONArray)jsonObj.get(ChangeServer.addremove);
        changesAdd = new ArrayList<Boolean>(jsonBools.size());
        for(Object bool : jsonBools) {
            changesAdd.add((Boolean)bool);
        }
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
    public String getOntologyURI() {
        if (ontologyURI == null && changesOntologies != null) { //either use the URI set in the object, or fetch the URI of the ontology that the first change is applied to
            return changesOntologies.get(0);
        }
        else return ontologyURI;
    }

    /** sets the URI of the ontology to */
    public void setOntologyURI(String uri) {
        ontologyURI = uri;
    }

    /** returns if this change capsule is empty, i.e. if it contains zero changes */
    public boolean empty() {
        if ((changes.size() == 0 || changesOntologies.size() == 0 || changesAdd.size() == 0)) return true;
        else return false;
    }

    /** returns the changes as a part of a new ontology (they still need to be applied in order to take effect) */
    public List<OWLOntologyChange> getChangeOWL(OWLOntologyManager manager) throws URISyntaxException {
        ArrayList<OWLOntologyChange> owlChanges = new ArrayList<OWLOntologyChange>(changes.size());
        Iterator<Boolean> areAdds = changesAdd.iterator();  //the iterator will always have the exact same number of objects as the changes list, so this slight hack is perfectly safe
        Iterator<String> relevantOntologies = changesOntologies.iterator();

        for(String cha : changes) {
            Boolean isAdd = areAdds.next(); //is this an add or a remove axiom
            OWLOntology onto = manager.getOntology(new URI(relevantOntologies.next())); //ontology this change is to be applied to

            OWLOntologyChange owlChange = deserializeChange(onto, manager, cha, isAdd);
            owlChanges.add(owlChange);
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
        for(String chaString : changes) {  //add strings of changes to a JSON array
            //String chaString = serializeChange(cha);
            arr.add(chaString);
        }

        JSONArray arrBools = new JSONArray();
        for(Boolean boo : changesAdd) {
            arrBools.add(boo);
        }

        JSONArray arrOntologies = new JSONArray();
        for(String ontString : changesOntologies) {
            arrOntologies.add(ontString);
        }


        JSONObject obj = new JSONObject();  //put all the other parameters into a JSON object (hashmap)
        obj.put(ChangeServer.username, username);
        obj.put(ChangeServer.timestamp, timestamp);
        obj.put(ChangeServer.summary, summary);
        obj.put(ChangeServer.sequence, new Long(sequence));
        obj.put(ChangeServer.change, arr);
        obj.put(ChangeServer.addremove, arrBools);
        obj.put(ChangeServer.ontology, arrOntologies);

        return obj.toString();  //return a JSON serialization
    }

    /** returns the same JSON serialization as the toJSON method */
    public String toString() {
        return toJSON();
    }


    /** turns an OWLOntologyChange object into an XML string representation */
    private String serializeChange(OWLOntologyChange change) {
        //PrintWriter writer = new PrintWriter();
        StringWriter writer2 = new StringWriter();

        XMLWriterNamespaceManager namespaceManager = new XMLWriterNamespaceManager(Namespaces.OWL11XML.toString());
        //namespaceManager.createPrefixForNamespace("http://www.co-ode.org/remote#");
        //namespaceManager.setPrefix("remote", "http://www.co-ode.org/remote#");
        namespaceManager.setDefaultNamespace(Namespaces.OWL11XML.toString());

        XMLWriter xmlWriter = new XMLWriterImpl(writer2, namespaceManager);//, "RemoteElement");

        try {
            //xmlWriter.startDocument("remote:RemoteElement");
            OWLXMLWriter owlxmlWriter = new OWLXMLWriter(writer2, namespaceManager, change.getOntology());
            OWLXMLObjectRenderer renderer = new OWLXMLObjectRenderer(change.getOntology(), owlxmlWriter);

            //xmlWriter.writeStartElement("remote:RemoteElement");   //empty element to ensure that the namespace declairation is in the correct place
            //xmlWriter.writeEndElement();

            change.getAxiom().accept(renderer); //write out the object into RDF/XML
            xmlWriter.endDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer2.getBuffer().toString();
    }


    /** return and reassemble the change from components; bit by bit, so the changes and build upon each other */
    private OWLOntologyChange deserializeChange(OWLOntology ontology, OWLOntologyManager manager, String changeString, Boolean isAdd) {
        OWLOntologyChange owlChange = null;
        OWLAxiom axiom = stringToOWLAxiom(ontology, manager, changeString);

        if (isAdd) {
            owlChange = new AddAxiom(ontology, axiom);  //recreate the appropriate change axiom
        } else {
            owlChange = new RemoveAxiom(ontology, axiom);
        }

        return owlChange;
    }

    /** converts a string of an ontology change axiom into an object in the current ontology (provided the object exists, which it should, given the order of the changes being executed) */
    private OWLAxiom stringToOWLAxiom(final OWLOntology ontology, OWLOntologyManager manager, String string) {
        OWLAxiom changedAxiom = null;

        if (parser == null) {   //initialize parser
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                parser = factory.newSAXParser();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (SAXException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        //string = string.substring(0, string.indexOf("<remote:RemoteElement/>"))+string.substring(string.indexOf("<remote:RemoteElement/>")+"<remote:RemoteElement/>".length(),string.length());

        try {
            InputSource isrc = new InputSource(new StringReader(string));

            //OWLXMLSerializationParserHandler myHandler = new OWLXMLSerializationParserHandler(manager, ontology, null);
            OWLXMLSerializationParserHandler myHandler = new OWLXMLSerializationParserHandler(null);
            final OWLXMLParserHandler handler2 = new OWLXMLParserHandler(manager, ontology, myHandler);
            /*handler2.addFactory(new AbstractElementHandlerFactory(OWLXMLVocabulary.REMOTEELEMENT) {
            public OWLElementHandler createHandler(OWLXMLParserHandler handler) {
                //return new OWLXMLSerializationParserHandler(handler);
                return handler2.topHandler;
            }
        });*/

            parser.parse(isrc, handler2);

            changedAxiom = myHandler.getOWLObject();

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWLXMLParserException e) {
            e.printStackTrace();
        }

        return changedAxiom;
    }

}
