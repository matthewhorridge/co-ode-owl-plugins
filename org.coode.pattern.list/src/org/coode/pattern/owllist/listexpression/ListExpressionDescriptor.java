package org.coode.pattern.owllist.listexpression;

import org.coode.pattern.impl.AbstractPatternDescriptor;
import org.coode.pattern.owllist.listexpression.ui.ListExpressionRenderer;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataFactory;

import java.net.URI;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class ListExpressionDescriptor extends AbstractPatternDescriptor<ListExpression> {

    public static final String DEFAULT_BASE_URI = "http://www.co-ode.org/ontologies/meta.owl#";

    // default URIs for the list constructors
    private URI defaultListURI = URI.create(DEFAULT_BASE_URI + "List");
    private URI defaultEmptyURI = URI.create(DEFAULT_BASE_URI + "EmptyList");
    private URI defaultNextURI = URI.create(DEFAULT_BASE_URI + "hasNext");
    private URI defaultFollowedByURI = URI.create(DEFAULT_BASE_URI + "isFollowedBy");
    private URI defaultContentsURI = URI.create(DEFAULT_BASE_URI + "hasContents");

    private OWLClass defaultList;
    private OWLClass defaultEmpty;
    private OWLObjectProperty defaultNext;
    private OWLObjectProperty defaultFollowedBy;
    private OWLObjectProperty defaultContents;

    public void init(OWLDataFactory df) {
        defaultList = df.getOWLClass(defaultListURI);
        defaultEmpty = df.getOWLClass(defaultEmptyURI);
        defaultNext = df.getOWLObjectProperty(defaultNextURI);
        defaultFollowedBy = df.getOWLObjectProperty(defaultFollowedByURI);
        defaultContents = df.getOWLObjectProperty(defaultContentsURI);
    }

    public ListExpression getPattern(OWLObject owlObject, OWLModelManager mngr) {
        ListExpressionBuilder listBuilder = new ListExpressionBuilder(this, mngr);
        ListExpression list = listBuilder.getList(owlObject);
        for (String error : listBuilder.getErrors()){
            System.out.println("error = " + error);
        }
        return list;
    }

    public OWLClass getDefaultListClass() {
//        OWLClass listClass = defaultList;
//        if (listClass == null){
//            listClass = mngr.getOWLClass("OWLList"); // try to find an obvious one
//            if (listClass == null){
//                listClass = df.getOWLClass(defaultListURI);
//            }
//        }
        return defaultList;
    }

    public OWLClass getDefaultEmptyListClass() {
//        OWLClass listClass = defaultEmpty;
//        if (listClass == null){
////            listClass = mngr.getOWLClass("EmptyList"); // try to find an obvious one
////            if (listClass == null){
//                listClass = df.getOWLClass(defaultEmptyURI);
////            }
//        }
        return defaultEmpty;
    }

    public OWLObjectProperty getDefaultNextProperty() {
//        OWLObjectProperty listProp = defaultNext;
//        if (listProp == null){
////            listProp = mngr.getOWLObjectProperty("hasNext"); // try to find an obvious one
////            if (listProp == null){
//                listProp = df.getOWLObjectProperty(defaultNextURI);
////            }
//        }
        return defaultNext;
    }

    public OWLObjectProperty getDefaultFollowedByProperty() {
//        OWLObjectProperty listProp = defaultFollowedBy;
//        if (listProp == null){
////            listProp = mngr.getOWLObjectProperty("isFollowedBy"); // try to find an obvious one
////            if (listProp == null){
//                listProp = df.getOWLObjectProperty(defaultFollowedByURI);
////            }
//        }
        return defaultFollowedBy;
    }

    public OWLObjectProperty getDefaultContentsProperty() {
//        OWLObjectProperty listProp = defaultContents;
//        if (listProp == null){
////            listProp = mngr.getOWLObjectProperty("hasContents"); // try to find an obvious one
////            if (listProp == null){
//                listProp = df.getOWLObjectProperty(defaultContentsURI);
////            }
//        }
        return defaultContents;
    }

    public void setDefaultListClass(OWLClass cls) {
        defaultList = cls;
    }

    public void setDefaultNextProperty(OWLObjectProperty prop) {
        defaultNext = prop;
    }

    public void setDefaultFollowedByProperty(OWLObjectProperty prop) {
        defaultFollowedBy = prop;
    }

    public void setDefaultContentsProperty(OWLObjectProperty prop) {
        defaultContents = prop;
    }
}
