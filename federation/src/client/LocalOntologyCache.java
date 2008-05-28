package client;

import org.semanticweb.owl.model.OWLOntology;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 19, 2008
 * Time: 1:03:45 PM
 * Stores ontologies locally for comparsion with the currently loaded model.
 * It is used to detect if the local ontology has been modified externally outside of
 * the client's change tracking system. Such so-called dirty local ontologies must be diffed
 * and cannot use the monitored change-tracking to determine what the user has changed.
 * (note that no local cache should be used, since, in the case that the server's copy has been modified
 * by another use (which is likely), that copy needs to be downloaded anyway, so why redundantly store
 * a local copy?)
 */
public class LocalOntologyCache {

    //deemed to not be necessary
}
