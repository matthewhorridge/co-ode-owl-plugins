package uk.ac.manchester.mae.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxDescriptionParser;
import org.coode.oae.utils.RenderingOWLEntityCheckerNoModelManager;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.mae.evaluation.PropertyChainCell;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;
import uk.ac.manchester.mae.parser.MAEpropertyChainCell;
import uk.ac.manchester.mae.parser.MAEpropertyChainExpression;

public class MAEAdapter {
	public static ManchesterOWLSyntaxDescriptionParser getParser(
			Set<OWLOntology> ontologies, OWLDataFactory factory) {
		return new ManchesterOWLSyntaxDescriptionParser(factory,
				new RenderingOWLEntityCheckerNoModelManager(ontologies));
	}

	public static OWLEntityChecker getChecker(Set<OWLOntology> ontologies) {
		return new RenderingOWLEntityCheckerNoModelManager(ontologies);
	}

	public static PropertyChainModel toPropertyChainModel(
			MAEpropertyChainExpression node, Set<OWLOntology> ontologies,
			OWLOntologyManager manager) {
		List<PropertyChainCell> list = new ArrayList<PropertyChainCell>();
		RenderingOWLEntityCheckerNoModelManager r = new RenderingOWLEntityCheckerNoModelManager(
				ontologies);
		ManchesterOWLSyntaxDescriptionParser parser = new ManchesterOWLSyntaxDescriptionParser(
				manager.getOWLDataFactory(), r);
		for (int i = 0; i < node.getCells().size(); i++) {
			MAEpropertyChainCell cell = node.getCells().get(i);
			OWLDescription facet = null;
			if (cell.getFacet() != null) {
				try {
					facet = parser.parse(cell.getFacet());
				} catch (ParserException e) {
					throw new RuntimeException("Invalid facet: "
							+ cell.getFacet(), e);
				}
			}
			OWLObjectProperty op = r.getOWLObjectProperty(cell
					.getPropertyName());
			if (op != null) {
				list.add(new PropertyChainCell(op, facet));
			} else {
				OWLDataProperty dp = r.getOWLDataProperty(cell
						.getPropertyName());
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
