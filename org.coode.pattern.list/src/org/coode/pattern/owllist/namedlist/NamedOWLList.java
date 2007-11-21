package org.coode.pattern.owllist.namedlist;

import org.apache.log4j.Logger;
import org.coode.pattern.impl.AbstractPattern;
import org.coode.pattern.owllist.listexpression.ListExpression;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.api.PatternListener;
import org.coode.pattern.api.Pattern;
import org.semanticweb.owl.model.*;
import org.protege.editor.owl.model.OWLModelManager;

import java.net.URI;
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
 * A named list is just a normal class that has one or more OWLListRestrictions on it
 */
public class NamedOWLList extends AbstractPattern {

    private Map<ListExpression, OWLNaryClassAxiom> currentExpressionsMap =
            new HashMap<ListExpression, OWLNaryClassAxiom>();

    // stores the conditions that already exist in the ontology and is unmodifiable
    private final Set<ListExpression> originalExpressions;

    // stores any new originalExpressions that are added
//    private final Set<ListExpression> currentExpressions = new HashSet<ListExpression>();

    private OWLClass baseCls;

    private String name;

    private boolean defined;

    private boolean valid;

    private OWLModelManager mngr;

    private PatternListener l = new PatternListener(){
        public void patternChanged(Pattern pattern) {
            OWLAxiom ax = currentExpressionsMap.get(pattern); // this will need to be removed

            currentExpressionsMap.put((ListExpression)pattern, null);
            //@@TODO update the state when a list expression changes

            notifyChanged(); // if any of the parts have changed, the whole has changed
        }
    };

    public NamedOWLList(OWLClass base, NamedListDescriptor descr, OWLModelManager mngr) {
        super(descr);

        this.mngr = mngr;

        this.baseCls = base;
        this.name = mngr.getOWLEntityRenderer().render(base);
        this.valid = true;

        final OWLDataFactory df = mngr.getOWLDataFactory();
        for (OWLOntology ont : mngr.getActiveOntologies()){
            for (OWLSubClassAxiom ax : ont.getSubClassAxiomsForLHS(base)){
                processAxiom(ax, getDescriptor().getListExpressionDescriptor(df));
            }

            for (OWLEquivalentClassesAxiom ax : ont.getEquivalentClassesAxioms(base)){
                processEquivalentAxiom(ax, base, getDescriptor().getListExpressionDescriptor(df));
            }
        }

        // make a read only copy that is for comparison
        originalExpressions = deepCopyExpressions();
        // @@TODO suss out further things about this list from the base class
    }

    public NamedOWLList(String name, NamedListDescriptor descr, OWLModelManager mngr) {
        super(descr);
        this.mngr = mngr;
        this.name = name;
        this.valid = true;

        // make a read only copy that is for comparison
        originalExpressions = deepCopyExpressions();
    }

    public NamedListDescriptor getDescriptor() {
        return (NamedListDescriptor) super.getDescriptor();
    }

    public boolean isValid() {
        return valid;
    }

    public OWLEntity getBase() {
        return baseCls;
    }

    public List<ListExpression> getParts() {
        return new ArrayList<ListExpression>(currentExpressionsMap.keySet());
    }

    public OWLObject toOWL(OWLOntology ont, OWLDataFactory df) {
        return getBaseClass(ont);
    }

    public List<OWLOntologyChange> getChanges(OWLOntologyManager mngr, OWLOntology ont, Set<OWLOntology> onts) {

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        // @@TODO can't do this as we don't know if the changes are ever applied
        // @@TODO need to check against the ontology (see ValuePartition impl)

        final Set<ListExpression> listExpressions = currentExpressionsMap.keySet();
        if (!originalExpressions.equals(listExpressions)){

            OWLDataFactory df = mngr.getOWLDataFactory();

            // add all the new superclasses
            OWLAxiom ax;
            for (ListExpression expression : listExpressions) {
// @@TODO we can work this out by whether there is already an axiom for the given expression
//                final OWLNaryClassAxiom classAxiom = currentExpressionsMap.get(expression);
//                if (classAxiom == null){
//
//                }
                if (!originalExpressions.contains(expression)){
                    OWLDescription owlRestr = expression.toOWL(ont, df);
                    if (defined){
                        Set<OWLDescription> equivs = new HashSet<OWLDescription>();
                        equivs.add(getBaseClass(ont));
                        equivs.add(owlRestr);
                        ax = df.getOWLEquivalentClassesAxiom(equivs);
                        changes.add(new AddAxiom(ont, ax));
                    }
                    else{
                        ax = df.getOWLSubClassAxiom(getBaseClass(ont), owlRestr);
                        changes.add(new AddAxiom(ont, ax));
                    }
                }
            }

            // remove all old superclasses
            for (ListExpression expression : originalExpressions){
                if (!listExpressions.contains(expression)){
                    OWLDescription owlRestr = expression.toOWL(ont, df);
                    // @@TODO how do we know which of below will be in reality?
                    if (defined){
                        Set<OWLDescription> equivs = new HashSet<OWLDescription>();
                        equivs.add(getBaseClass(ont));
                        equivs.add(owlRestr);
                        ax = df.getOWLEquivalentClassesAxiom(equivs);
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                    else{
                        ax = df.getOWLSubClassAxiom(getBaseClass(ont), owlRestr);
                        changes.add(new AddAxiom(ont, ax));
                    }
                }
            }
        }

        // @@TODO update the properties if needed

        return changes;
    }

    public String render() {
        return name;
    }

    public List<OWLOntologyChange> delete(OWLOntologyManager mngr, Set<OWLOntology> onts) {
        System.err.println("Named List Deletion not yet implemented");
        return Collections.EMPTY_LIST;
    }

////////////////////////////////////////////////////////////////

    public void addCondition(ListExpression restr) {
        currentExpressionsMap.put(restr, null);
        restr.addChangeListener(l);
        notifyChanged();
    }

    public void removeCondition(ListExpression restr) {
        currentExpressionsMap.remove(restr);
        restr.removeChangeListener(l);
        notifyChanged();
    }

    public Set<ListExpression> getConditions(){
        return Collections.unmodifiableSet(currentExpressionsMap.keySet());
    }

    public void setDefined(boolean selected) {
        defined = selected;
        notifyChanged();
    }

    public boolean isDefined() {
        return defined;
    }


///////////////////////////////////////////////////////////////

//    private void generate(OWLDescription descr, OWLDataFactory df) {
//        ListExpressionDescriptor restrDescr = getDescriptor().getListExpressionDescriptor(df);
//        ListExpression listRestr = restrDescr.getPattern(descr, mngr);
//        if (listRestr != null) {
//            currentExpressions.add(listRestr);
//            listRestr.addChangeListener(l);
//        }
//    }

    private OWLClass getBaseClass(OWLOntology ont) {
        OWLClass baseClass = null;
        if (baseCls != null){
            baseClass = baseCls;
        }
        else {
            OWLEntity entity = mngr.getOWLEntity(name);
            if (entity != null && entity instanceof OWLClass) {
                baseClass = (OWLClass) entity;
            }
            else {
                try {
                    URI uri = new URI(ont.getURI().toString() + "#" + name);
                    baseClass = mngr.getOWLDataFactory().getOWLClass(uri);
                }
                catch (Exception e) {
                    valid = false;
                    Logger.getLogger(NamedOWLList.class).error(e);
                }
            }
        }
        return baseClass;
    }


    private void processEquivalentAxiom(OWLEquivalentClassesAxiom ax,
                                        OWLClass base,
                                        ListExpressionDescriptor restrDescr) {
        for (OWLDescription equiv : ax.getDescriptions()){
            if (!equiv.equals(base)){
                ListExpression listRestr = restrDescr.getPattern(equiv, mngr);
                if (listRestr != null) {
                    currentExpressionsMap.put(listRestr, ax);
                    listRestr.addChangeListener(l);
                }
            }
        }
    }

    private void processAxiom(OWLSubClassAxiom ax, ListExpressionDescriptor restrDescr) {
        OWLDescription superCls = ax.getSuperClass();
        ListExpression listRestr = restrDescr.getPattern(superCls, mngr);
        if (listRestr != null) {
            currentExpressionsMap.put(listRestr, ax);
            listRestr.addChangeListener(l);
        }
    }

    private Set<ListExpression> deepCopyExpressions() {
        Set<ListExpression> copies = new HashSet<ListExpression>();
        for (ListExpression e : currentExpressionsMap.keySet()){
            final ListExpression expression = e.cloneExpression();
            expression.addChangeListener(l);
            copies.add(expression);
        }
        return Collections.unmodifiableSet(copies);
    }
}
