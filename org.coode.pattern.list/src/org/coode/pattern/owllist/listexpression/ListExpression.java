package org.coode.pattern.owllist.listexpression;

import org.coode.pattern.impl.AbstractPattern;
import org.coode.pattern.owllist.listexpression.ui.ListExpressionOWLDescriptionRenderer;
import org.semanticweb.owl.model.*;

import java.util.*;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * A list expression is an OWLDescription that conforms to one of the patterns described in the OWLLists paper
 * Currently trivially recognised by usage of the hasContents property
 * <p/>
 */
public class ListExpression extends AbstractPattern implements List<OWLDescription> {

    // the list constructs
    private OWLClass list;
    private OWLClass emptyList;
    private OWLObjectProperty next;
    private OWLObjectProperty followedBy;
    private OWLObjectProperty contents;

    private final List<OWLDescription> elements = new ArrayList<OWLDescription>();

    private OWLAxiom axiom;

    private OWLDescription only;

    private boolean negated;

    public ListExpression(ListExpressionDescriptor descr){//, OWLAxiom axiom) {
        super(descr);

        this.axiom = axiom;

        list = descr.getDefaultListClass();
        emptyList = descr.getDefaultEmptyListClass();
        next = descr.getDefaultNextProperty();
        followedBy = descr.getDefaultFollowedByProperty();
        contents = descr.getDefaultContentsProperty();
    }

    public ListExpression cloneExpression() {
        ListExpression clone = new ListExpression(getDescriptor());
        clone.addAll(this.elements);
        clone.next = this.next;
        clone.followedBy = this.followedBy;
        clone.contents = this.contents;
        clone.only = this.only;
        clone.negated = this.negated;
        return clone;
    }

//////////////////////////// Pattern implementation

    public List<OWLOntologyChange> getChanges(OWLOntologyManager mngr, OWLOntology activeOnt, Set<OWLOntology> activeOnts) {
        return Collections.EMPTY_LIST;  // no changes as this pattern is not an axiom
    }

    public List<OWLOntologyChange> delete(OWLOntologyManager mngr, Set<OWLOntology> onts) {
        return Collections.EMPTY_LIST; // no changes as this pattern is not an axiom
    }

    public ListExpressionDescriptor getDescriptor() {
        return (ListExpressionDescriptor) super.getDescriptor();
    }

    public boolean isValid() {
        return true;
    }

    public OWLEntity getBase() {
        return null;
    }

    public List<OWLDescription> getParts() {
        List<OWLDescription> parts = new ArrayList<OWLDescription>();
        for (OWLDescription element : elements){
            if (element != null && !parts.contains(element)){
                parts.add(element);
            }
        }
        return parts;
    }

    public OWLDescription toOWL(OWLOntology ont, OWLDataFactory df) {
        ListExpressionOWLDescriptionRenderer owlRenderer = new ListExpressionOWLDescriptionRenderer(getDescriptor());
        return owlRenderer.render(this, df);
    }

/////////////////////////////////////////////////////// OWLList specific methods

    private List<OWLDescription> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public OWLObjectProperty getNextProperty() {
        return next;
    }

    public OWLObjectProperty getFollowedByProperty() {
        return followedBy;
    }

    public OWLObjectProperty getContentsProperty() {
        return contents;
    }

    public OWLDescription getOnly() {
        return only;
    }

    public boolean isEndOpen() {
        return elements.size() > 0 && elements.get(elements.size()-1) == null;
    }

    public boolean isStartOpen() {
        return elements.size() > 0 && elements.get(0) == null;
    }

    public boolean isExactly() {
        return !isEndOpen() && !isStartOpen();
    }

    public boolean isContains() {
        return isEndOpen() && isStartOpen();
    }

    public boolean isNegated() {
        return negated;
    }

    public boolean isOnly(){
        return only != null;
    }

    public void setNext(OWLObjectProperty next) {
        this.next = next;
        notifyChanged();
    }

    public void setFollowedBy(OWLObjectProperty followedBy) {
        this.followedBy = followedBy;
        notifyChanged();
    }

    public void setContents(OWLObjectProperty contents) {
        this.contents = contents;
        notifyChanged();
    }

    public void setNegated(boolean negated){
        this.negated = negated;
        notifyChanged();
    }

    public void setOnly(OWLDescription only) {
        if (!elements.isEmpty()){
            System.err.println("Making non-empty list only: " + only);
        }
        this.only = only;
        notifyChanged();
    }

    ///////////////// List implementation

    public boolean equals(Object object) {
        return (object instanceof ListExpression &&
                negated == ((ListExpression)object).isNegated() &&
                (only == ((ListExpression)object).getOnly() || only.equals(((ListExpression)object).getOnly())) &&
                elements.equals(((ListExpression)object).getElements()));
    }

    public OWLDescription get(int i) {
        return elements.get(i);
    }

    public OWLDescription set(int i, OWLDescription owlDescription) {
        OWLDescription element = elements.set(i, owlDescription);
        notifyChanged();
        return element;
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public boolean contains(Object o) {
        return elements.contains(o);
    }

    public Iterator<OWLDescription> iterator() {
        return elements.iterator();
    }

    public Object[] toArray() {
        return elements.toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return elements.toArray(ts);
    }

    public boolean add(OWLDescription element){
        if (element == null && !elements.isEmpty() && elements.get(elements.size()-1) == null){
            System.err.println("WARNING - trying to add multiple nulls in a row: " + elements);
            return false;
        }
        else{
            boolean result = elements.add(element);
            notifyChanged();
            return result;
        }
    }

    public boolean remove(Object o) {
        boolean result = elements.remove(o);
        notifyChanged();
        return result;
    }

    public boolean containsAll(Collection<?> objects) {
        return elements.containsAll(objects);
    }

    // @@TODO manage multiple inline ... elements
    public boolean addAll(Collection<? extends OWLDescription> owlDescriptions) {
        boolean result = elements.addAll(owlDescriptions);
        notifyChanged();
        return result;
    }

    // @@TODO manage multiple inline ... elements
    public boolean addAll(int i, Collection<? extends OWLDescription> owlDescriptions) {
        boolean result = elements.addAll(i, owlDescriptions);
        notifyChanged();
        return result;
    }

    // @@TODO manage multiple inline ... elements
    public boolean removeAll(Collection<?> objects) {
        boolean result = elements.removeAll(objects);
        notifyChanged();
        return result;
    }

    // @@TODO manage multiple inline ... elements
    public boolean retainAll(Collection<?> objects) {
        boolean result = elements.retainAll(objects);
        notifyChanged();
        return result;
    }

    public void add(int position, OWLDescription element){
        if (element == null && !elements.isEmpty() && elements.get(position) == null){
            System.err.println("WARNING - trying to add multiple nulls in a row: " + elements);
        }
        else{
            elements.add(position, element);
            notifyChanged();
        }
    }

    // @@TODO manage multiple inline ... elements
    public OWLDescription remove(int i) {
        OWLDescription element = elements.remove(i);
        notifyChanged();
        return element;
    }

    public int indexOf(Object o) {
        return elements.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return elements.lastIndexOf(o);
    }

    public ListIterator<OWLDescription> listIterator() {
        return elements.listIterator();
    }

    public ListIterator<OWLDescription> listIterator(int i) {
        return elements.listIterator(i);
    }

    // @@TODO should we also provide a method that returns a ListExpression?
    public List<OWLDescription> subList(int i, int i1) {
        return elements.subList(i, i1);
    }

    public void clear(){
        elements.clear();
        notifyChanged();
    }
}
