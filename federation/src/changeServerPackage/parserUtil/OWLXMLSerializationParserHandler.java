package changeServerPackage.parserUtil;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntology;
import org.coode.owl.owlxmlparser.*;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 22, 2008
 * Time: 2:58:32 PM
 * Reads the OWL objects when parsing XML into OWL
 */
public class OWLXMLSerializationParserHandler implements OWLElementHandler {
    private OWLAxiom axiomChange = null; //the parsed axiomChange
  //  private OWLAxiom returnableAxiom;

    public void endElement() throws OWLXMLParserException {
        //System.out.println("end");
    }


    public void startElement(String name) throws OWLXMLParserException {
        //System.out.println("start");
    }

    public void attribute(String s, String s1) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public OWLXMLSerializationParserHandler(OWLXMLParserHandler owlxmlParserHandler) {
        //super(owlxmlParserHandler);
    }

    public OWLAxiom getOWLObject() throws OWLXMLParserException {
        return axiomChange;//.getAxiom();
    }

    public void setParentHandler(OWLElementHandler owlElementHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /*public OWLAxiomChange getChange() {
        return axiomChange;
    }*/

    public void handleChild(AbstractOWLAxiomElementHandler handler) throws OWLXMLParserException {
        //axiomChange = new AddAxiom(getOntology(), handler.getOWLObject());
        axiomChange = handler.getOWLObject();
   //     if (axiomChange != null) returnableAxiom = axiomChange;
    }

    public void handleChild(AbstractOWLDescriptionElementHandler abstractOWLDescriptionElementHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleChild(AbstractOWLObjectPropertyElementHandler abstractOWLObjectPropertyElementHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleChild(OWLDataPropertyElementHandler owlDataPropertyElementHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleChild(OWLIndividualElementHandler owlIndividualElementHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleChild(AbstractOWLDataRangeHandler abstractOWLDataRangeHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleChild(OWLConstantElementHandler owlConstantElementHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleChild(OWLAnnotationElementHandler owlAnnotationElementHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleChild(OWLSubObjectPropertyChainElementHandler owlSubObjectPropertyChainElementHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleChild(OWLDatatypeFacetRestrictionElementHandler owlDatatypeFacetRestrictionElementHandler) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleChars(char[] chars, int i, int i1) throws OWLXMLParserException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getText() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isTextContentPossible() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
