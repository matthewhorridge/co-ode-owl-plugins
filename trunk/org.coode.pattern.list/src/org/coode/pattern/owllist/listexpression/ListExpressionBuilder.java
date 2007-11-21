package org.coode.pattern.owllist.listexpression;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 3, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 *
 * Visitor that creates an OWLList from an appropriate OWLDescription
 * It IS NOT the place of the OWLListBuilder to determine if the description is appropriate
 *
 * @@TODO what should happen if the "structural parse" fails?? I suppose just throw an exception
 */
public class ListExpressionBuilder extends OWLObjectVisitorAdapter {

    private OWLDescription next;
    private OWLDescription followedBy;
    private boolean valid;
    private boolean closed;

    private ListExpression pattern;
    private OWLModelManager mngr;
    private ListExpressionDescriptor descr;

    private List<String> errors = new ArrayList<String>();
    private OWLObjectAllRestriction only;

    public ListExpressionBuilder(ListExpressionDescriptor descr, OWLModelManager mngr) {
        this.descr = descr;
        this.mngr = mngr;
    }

    public ListExpression getList(OWLObject root) {
        reset(root);
        root.accept(this);
        if (valid){
            if (!closed && !pattern.isEmpty()){
                pattern.add(null);
            }
            return pattern;
        }
        else{
            return null;
        }
    }

    public void reset(OWLObject root){
        pattern = new ListExpression(descr);
        valid = true;
        next = followedBy = null;
        closed = false;
        errors.clear();
    }

    public void visit(OWLObjectIntersectionOf owlAnd) {
        Set<OWLDescription> ops = owlAnd.getOperands();

        for (OWLDescription op : ops) {
            op.accept(this);
        }

        if (only != null){
            pattern.setOnly(only);
        }
        else{
            if (next == null){
                //System.out.println("ADDING NULL: " + pattern);
                if (followedBy != null) {
                    pattern.add(null);
                    followedBy.accept(this);
                }
            }
            else{
                next.accept(this);
            }
        }
    }

    public void visit(OWLObjectSomeRestriction some) {
        if (descr.getDefaultContentsProperty().equals(some.getProperty())) {
            pattern.add(some.getFiller());
        }
        else if (descr.getDefaultNextProperty().equals(some.getProperty())) {
            next = some.getFiller();
        }
        else if (descr.getDefaultFollowedByProperty().equals(some.getProperty())) {
//            if (pattern.isEmpty()) {
//                System.out.println("adding followed by on empty list = " + pattern);
//                pattern.add(getBaseElementType());
//                pattern.add(null);
//                some.getFiller().accept(this);
//            }
//            else {
            followedBy = some.getFiller();
//            }
        }
        else {
            valid = false;
            errors.add("wrong property used in restriction: " + some);
        }
    }


    public void visit(OWLObjectAllRestriction only) {
        if (descr.getDefaultContentsProperty().equals(only.getProperty())) {
            if (this.only == null){
                this.only = only;
            }
            else{
                if (!this.only.equals(only)){
                    valid = false;
                    errors.add("Cannot have an only list with different fillers in the head and tail");
                }
            }
        }
        else if (descr.getDefaultFollowedByProperty().equals(only.getProperty())) {
            if (this.only == null){
                this.only = only;
            }
            else{
                if (!this.only.equals(only)){
                    valid = false;
                    errors.add("Cannot have an only list with different fillers in the head and tail");
                }
            }
        }
    }

    public void visit(OWLClass owlClass) {
        Set<OWLClass> ancestors = mngr.getOWLClassHierarchyProvider().getAncestors(owlClass);
        ancestors.add(owlClass);
        if (ancestors.contains(descr.getDefaultEmptyListClass())){
            closed = true;
//            //pattern.add(owlClass);
        }
        else if (ancestors.contains(descr.getDefaultListClass())) {
//            type = owlClass;
        }
        else{
            valid = false;
            errors.add("Non descr class found: " + owlClass);
        }
    }

    public void visit(OWLObjectComplementOf owlNot) {
        if (pattern.isEmpty()){
            pattern.setNegated(true);
            owlNot.getOperand().accept(this);
        }
        else{
//            owlNot.getOperand().accept(this);
            valid = false;
            errors.add("Found a NOT somewhere other than the start of the descr: " + pattern);
        }
    }

    public void visit(OWLObjectUnionOf owlOr) {
        if (pattern.isEmpty()){
            Set<OWLDescription> ops = owlOr.getOperands();
            OWLDescription filler = null;
            for (OWLDescription op : ops){
                if (op instanceof OWLObjectSomeRestriction){
                    if (((OWLObjectSomeRestriction)op).getProperty().equals(descr.getDefaultFollowedByProperty())){
                        filler = ((OWLObjectSomeRestriction)op).getFiller();
                    }
                }
            }
            for (OWLDescription op : ops){
                if (op.equals(filler)){
                    System.out.println("adding null in Union = " + pattern);
                    pattern.add(null);
                    op.accept(this);
                    return;
                }
            }
        }
        else{
            valid = false;
            errors.add("Found an OR somewhere other than the start of the descr: " + pattern);
        }
    }

    // @@TODO should check the base type (normally OWLList) for universal restriction on the hasContents prop
    private OWLDescription getBaseElementType() {
        return mngr.getOWLDataFactory().getOWLThing();
    }

    public List<String> getErrors() {
        return errors;
    }
}
