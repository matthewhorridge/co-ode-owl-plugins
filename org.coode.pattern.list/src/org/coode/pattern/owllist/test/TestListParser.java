package org.coode.pattern.owllist.test;

import org.protege.editor.owl.model.OWLModelManagerImpl;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.model.description.manchester.ManchesterSyntaxParser;
import org.semanticweb.owl.model.*;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.owllist.listexpression.ListExpression;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParserImpl;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParser;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import junit.framework.TestCase;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 9, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class TestListParser extends TestCase {

    private static final String BASE = "http://www.co-ode.org/ontologies/test/lists.owl";
    private ListExpressionParser listParser;
    private OWLClass a;
    private OWLClass b;
    private OWLClass c;

    // list-specific entities
    private OWLClass myList;
    private OWLObjectProperty hasNext;
    private OWLObjectProperty followedBy;
    private OWLObjectProperty hasContent;

    private ListExpressionDescriptor descr;
    private OWLModelManager mngr;
    private OWLOntology ont;

    private static int STRING = 0;
    private static int VALID = 1;
    private static int EMPTY = 2;
    private static int COUNT = 3;

    private Object[][] testData = new Object[][]{
            //           string,                valid, empty, count
            new Object[]{"()",                  true, true, 0},
            new Object[]{"(A, B, C)",           true, true, 0}
    };

    ////////////// test parser


    public void testParseListData(){
        try{
            init();
            for (Object[] data : testData){
            ListExpression list = listParser.createOWLPattern((String)data[STRING]);
            assertEquals(((Boolean)data[VALID]).booleanValue(), list.isValid());
            assertEquals(((Boolean)data[EMPTY]).booleanValue(), list.isEmpty());
            assertEquals(data[COUNT], list.size());
            }
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testParseEmptyList(){
        try{
            init();
            ListExpression list = listParser.createOWLPattern("()");
            assertTrue(list.isValid());
            assertTrue(list.isEmpty());
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }

    
    public void testParseSimpleList(){
        try {
            init();
            List<OWLDescription> list = listParser.createList("(A, B, C)");
            assertEquals(3, list.size());
            assertEquals(a, list.get(0));
            assertEquals(b, list.get(1));
            assertEquals(c, list.get(2));
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }


    public void testParseEndsWith(){
        try{
            init();
            List<OWLDescription> list = listParser.createList("(..., A, B, C)");
            assertEquals(4, list.size());
            assertEquals(null, list.get(0));
            assertEquals(a, list.get(1));
            assertEquals(b, list.get(2));
            assertEquals(c, list.get(3));
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testParseStartsWith(){
        try{
            init();
            List<OWLDescription> list = listParser.createList("(A, B, C, ...)");
            assertEquals(4, list.size());
            assertEquals(a, list.get(0));
            assertEquals(b, list.get(1));
            assertEquals(c, list.get(2));
            assertEquals(null, list.get(3));
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testParseContains(){
        try{
            init();
            List<OWLDescription> list = listParser.createList("(..., A, B, C, ...)");
            assertEquals(5, list.size());            
            assertEquals(null, list.get(0));
            assertEquals(a, list.get(1));
            assertEquals(b, list.get(2));
            assertEquals(c, list.get(3));
            assertEquals(null, list.get(4));
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testParseContainsUnspecifiedMiddle(){
        try{
            init();
            List<OWLDescription> list = listParser.createList("(A, ..., B, C)");
            assertEquals(4, list.size());
            assertEquals(a, list.get(0));
            assertEquals(null, list.get(1));
            assertEquals(b, list.get(2));
            assertEquals(c, list.get(3));
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }


    public void testParseContractsMultipleElipsis(){
        try{
            init();
            List<OWLDescription> list = listParser.createList("(A, ..., ..., B, C)");
            assertEquals(4, list.size());
            assertEquals(a, list.get(0));
            assertEquals(null, list.get(1));
            assertEquals(b, list.get(2));
            assertEquals(c, list.get(3));
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }


    public void testParseListWithUnion(){
        try{
            init();
            List<OWLDescription> list = listParser.createList("(A, [B|C])");
            assertEquals(2, list.size());
            assertEquals(a, list.get(0));
            Set<OWLDescription> bOrC = new HashSet<OWLDescription>();
            bOrC.add(b);
            bOrC.add(c);
            assertEquals(mngr.getOWLDataFactory().getOWLObjectUnionOf(bOrC), list.get(1));
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testParseOnlyList(){
        try{
            init();
            List<OWLDescription> list = listParser.createList("(A*)");
            assertEquals(1, list.size());
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    private void init(){
        if (listParser == null){
            try {
                mngr = new OWLModelManagerImpl();
                ont = mngr.createNewOntology(URI.create(BASE), null);
                mngr.setActiveOntology(ont);

                final OWLDataFactory df = mngr.getOWLDataFactory();
                a = df.getOWLClass(URI.create(BASE + "#A"));
                b = df.getOWLClass(URI.create(BASE + "#B"));
                c = df.getOWLClass(URI.create(BASE + "#C"));

                myList = df.getOWLClass(URI.create(BASE + "#MyList"));
                hasNext = df.getOWLObjectProperty(URI.create(BASE + "#hasNext"));
                followedBy = df.getOWLObjectProperty(URI.create(BASE + "#followedBy"));
                hasContent = df.getOWLObjectProperty(URI.create(BASE + "#hasContent"));

                List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
                changes.add(new AddAxiom(ont, df.getOWLSubClassAxiom(a, df.getOWLThing())));
                changes.add(new AddAxiom(ont, df.getOWLSubClassAxiom(b, df.getOWLThing())));
                changes.add(new AddAxiom(ont, df.getOWLSubClassAxiom(c, df.getOWLThing())));
                mngr.applyChanges(changes);

                descr = new ListExpressionDescriptor();
                descr.setDefaultContentsProperty(hasContent);
                descr.setDefaultNextProperty(hasNext);
                descr.setDefaultFollowedByProperty(followedBy);
                descr.setDefaultListClass(myList);

                ManchesterSyntaxParser parser = new ManchesterSyntaxParser();
                parser.setOWLModelManager(mngr);
                listParser = new ListExpressionParserImpl(descr, mngr);
            }
            catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }
    }
}
