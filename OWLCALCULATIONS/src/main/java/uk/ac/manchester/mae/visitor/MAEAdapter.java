package uk.ac.manchester.mae.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.coode.oae.utils.RenderingOWLEntityCheckerNoModelManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import uk.ac.manchester.mae.evaluation.PropertyChainCell;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;
import uk.ac.manchester.mae.parser.MAEpropertyChainCell;
import uk.ac.manchester.mae.parser.MAEpropertyChainExpression;

public class MAEAdapter {

    public static ManchesterOWLSyntaxParser
            getParser(Set<OWLOntology> ontologies, OWLDataFactory factory) {
        ManchesterOWLSyntaxParser p = OWLManager.createManchesterParser();
        p.setOWLEntityChecker(
                new RenderingOWLEntityCheckerNoModelManager(ontologies));
        return p;
    }

    public static OWLEntityChecker getChecker(Set<OWLOntology> ontologies) {
        return new RenderingOWLEntityCheckerNoModelManager(ontologies);
    }

    public static PropertyChainModel toPropertyChainModel(
            MAEpropertyChainExpression node, Set<OWLOntology> ontologies,
            OWLOntologyManager manager) {
        List<PropertyChainCell> list = new ArrayList<>();
        ManchesterOWLSyntaxParser parser = getParser(ontologies,
                manager.getOWLDataFactory());
        for (int i = 0; i < node.getCells().size(); i++) {
            MAEpropertyChainCell cell = node.getCells().get(i);
            OWLClassExpression facet = null;
            if (cell.getFacet() != null) {
                try {
                    parser.setStringToParse(cell.getFacet());
                    facet = parser.parseClassExpression();
                } catch (OWLRuntimeException e) {
                    throw new RuntimeException(
                            "Invalid facet: " + cell.getFacet(), e);
                }
            }
            OWLObjectProperty op = getChecker(ontologies)
                    .getOWLObjectProperty(cell.getPropertyName());
            if (op != null) {
                list.add(new PropertyChainCell(op, facet));
            } else {
                OWLDataProperty dp = getChecker(ontologies)
                        .getOWLDataProperty(cell.getPropertyName());
                if (dp == null) {
                    throw new RuntimeException(
                            "Invalid property name (not an object or datatype property): "
                                    + cell.getPropertyName());
                }
                if (facet != null) {
                    throw new RuntimeException(
                            "Invalid facet: no facet can be specified for a datatype property"
                                    + cell.getFacet());
                }
                list.add(new PropertyChainCell(dp, null));
            }
        }
        return new PropertyChainModel(list);
    }
}
