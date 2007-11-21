package org.coode.pattern.owllist.test;

import junit.framework.TestCase;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.owllist.listexpression.ListExpressionBuilder;
import org.coode.pattern.owllist.listexpression.ListExpression;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.SimpleURIMapper;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLModelManagerImpl;

import java.net.URI;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
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
 * Date: Nov 13, 2007<br><br>
 */
public class TestListBuilder extends TestCase {

    private static final String BASE = "http://www.co-ode.org/ontologies/amino-acid/2006/05/18/amino-acid.owl";
    private static final String META_BASE = "http://www.co-ode.org/ontologies/meta.owl";

    private OWLClass a;
    private OWLClass d;
    private OWLClass e;
    private OWLClass f;

    // list-specific entities
    private OWLClass list;
    private OWLObjectProperty hasNext;
    private OWLObjectProperty followedBy;
    private OWLObjectProperty hasContent;

    private ListExpressionDescriptor descr;
    private OWLModelManager mngr;
    private OWLOntology ont;

    private ListExpressionBuilder listBuilder;

    public void testBuildSimpleList(){
        init();
        ListExpression list = buildList("#ADE");

        assertEquals(3, list.size());
        assertEquals(a, list.get(0));
        assertEquals(d, list.get(1));
        assertEquals(e, list.get(2));
        assertFalse(list.isStartOpen());
        assertFalse(list.isEndOpen());
    }

    public void testBuildStartsWith(){
        init();
        ListExpression list = buildList("#StartsADE");

        assertEquals(4, list.size());
        assertEquals(a, list.get(0));
        assertEquals(d, list.get(1));
        assertEquals(e, list.get(2));
        assertEquals(null, list.get(3));
        assertFalse(list.isStartOpen());
        assertTrue(list.isEndOpen());
    }

    public void testBuildEndsWith(){
        init();
        ListExpression list = buildList("#EndsADE");

        assertEquals(4, list.size());
        assertEquals(null, list.get(0));
        assertEquals(a, list.get(1));
        assertEquals(d, list.get(2));
        assertEquals(e, list.get(3));
        assertTrue(list.isStartOpen());
        assertFalse(list.isEndOpen());
    }

    public void testBuildContains(){
        init();
        ListExpression list = buildList("#ContainsADE");

        assertEquals(5, list.size());
        assertEquals(null, list.get(0));
        assertEquals(a, list.get(1));
        assertEquals(d, list.get(2));
        assertEquals(e, list.get(3));
        assertEquals(null, list.get(4));
        assertTrue(list.isStartOpen());
        assertTrue(list.isEndOpen());
    }


    public void testBuildContainsUnion(){
        init();
        ListExpression list = buildList("#ContainsUnion");

        assertEquals(3, list.size());
        assertEquals(a, list.get(0));
        Set<OWLDescription> dOrFSet = new HashSet<OWLDescription>();
        dOrFSet.add(d);
        dOrFSet.add(f);
        OWLObjectUnionOf dOrF = mngr.getOWLDataFactory().getOWLObjectUnionOf(dOrFSet);
        assertEquals(dOrF, list.get(1));
        assertEquals(e, list.get(2));
        assertFalse(list.isStartOpen());
        assertFalse(list.isEndOpen());
    }


    public void testBuildContainsGap(){
        init();
        ListExpression list = buildList("#AFollowedByE");

        assertEquals(3, list.size());
        assertEquals(a, list.get(0));
        assertEquals(null, list.get(1));
        assertEquals(e, list.get(2));
        assertFalse(list.isStartOpen());
        assertFalse(list.isEndOpen());
    }

    private ListExpression buildList(String s) {
        OWLClass ade = mngr.getOWLDataFactory().getOWLClass(URI.create(BASE + s));
        Set<OWLDescription> equivs = ade.getEquivalentClasses(ont);
        assertEquals(1, equivs.size());
        OWLDescription listInOWL = equivs.iterator().next();
        ListExpression list = listBuilder.getList(listInOWL);
        assertEquals(Collections.EMPTY_LIST, listBuilder.getErrors());
        return list;
    }

    private void init(){
        if (listBuilder == null){
            try {
                mngr = new OWLModelManagerImpl();
                URI physicalURI = getClass().getResource("testlists.owl").toURI();
                mngr.getOWLOntologyManager().addURIMapper(new SimpleURIMapper(URI.create(BASE), physicalURI));
                ont = mngr.loadOntology(URI.create(BASE));
                mngr.setActiveOntology(ont);

                final OWLDataFactory df = mngr.getOWLDataFactory();
                a = df.getOWLClass(URI.create(BASE + "#A"));
                d = df.getOWLClass(URI.create(BASE + "#D"));
                e = df.getOWLClass(URI.create(BASE + "#E"));
                f = df.getOWLClass(URI.create(BASE + "#F"));

                list = df.getOWLClass(URI.create(META_BASE + "#List"));
                hasNext = df.getOWLObjectProperty(URI.create(META_BASE + "#hasNext"));
                followedBy = df.getOWLObjectProperty(URI.create(META_BASE + "#isFollowedBy"));
                hasContent = df.getOWLObjectProperty(URI.create(META_BASE + "#hasContents"));

                descr = new ListExpressionDescriptor();
                descr.setDefaultContentsProperty(hasContent);
                descr.setDefaultNextProperty(hasNext);
                descr.setDefaultFollowedByProperty(followedBy);
                descr.setDefaultListClass(list);

                listBuilder = new ListExpressionBuilder(descr, mngr);
            }
            catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }
    }
}
