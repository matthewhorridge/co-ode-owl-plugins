package org.coode.pattern.owllist.test;

import junit.framework.TestCase;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParserImpl;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.owllist.listexpression.ListExpression;
import org.coode.pattern.owllist.listexpression.ListExpressionBuilder;
import org.coode.pattern.owllist.listexpression.ui.ListExpressionRenderer;
import org.semanticweb.owl.model.*;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLModelManagerImpl;
import org.protege.editor.owl.model.description.manchester.ManchesterSyntaxParser;
import org.protege.editor.owl.model.description.OWLExpressionParserException;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 14, 2007<br><br>
 */
public class TestRoundTrip extends TestCase {


    private static final String BASE = "http://www.co-ode.org/ontologies/test/lists.owl";
    private ListExpressionParserImpl listParser;
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
    private ListExpressionBuilder listBuilder;

    private String[] positiveTestData = new String[]{
            "(A)",
            "(A, B, C)",
            "(A, B, C, ...)",
            "(A, ...)",
            "(..., A, B, C)",
            "(..., A)",
            "(..., A, B, C, ...)",
            "(..., A, ...)",
            "(A, ..., B)",
            "(..., A, ..., B, C, ...)",
            "(A, [B|C])",
            "(A, [B|C|A])",
            "(A, [B])",
            "(..., A, ..., ..., B, C, ...)",
            "(A, ..., ..., B, C, ...)"
    };


    public void testRoundTrips(){
        try {
            init();
            for (final String in : positiveTestData) {
                System.out.println("INPUT: " + in);
                ListExpression list1 = listParser.createOWLPattern(in);
                OWLDescription owl = list1.toOWL(ont, mngr.getOWLDataFactory());

                ListExpression list2 = listBuilder.getList(owl);
                assertEquals(list1, list2);

//                String out = ren.render(list2);
//                System.out.println("OUTPUT: " + out);
//                assertEquals(in, out);
            }
        }
        catch (OWLExpressionParserException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testRoundTripSpecificPattern(){
        try {
            init();
            final String in = "(A, ..., B)";
            ListExpression list1 = listParser.createOWLPattern(in);
            OWLDescription owl = list1.toOWL(ont, mngr.getOWLDataFactory());

            ListExpression list2 = listBuilder.getList(owl);
            assertEquals(list1, list2);

//            String out = list2.render();
//            assertEquals(in, out);
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

                listBuilder = new ListExpressionBuilder(descr, mngr);
            }
            catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }
    }
}
