package org.coode.www.servlet;

import org.coode.html.OWLHTMLServer;
import org.coode.html.doclet.HTMLDoclet;
import org.coode.html.doclet.NestedHTMLDoclet;
import org.coode.html.page.EmptyOWLDocPage;
import org.coode.owl.mngr.OWLServer;
import org.coode.owl.mngr.ServerConstants;
import org.coode.www.ManageAction;
import org.coode.www.OWLDocServerConstants;
import org.coode.www.doclet.BlurbDoclet;
import org.coode.www.doclet.LoadFormDoclet;
import org.coode.www.doclet.OntologyMappingsTableDoclet;
import org.coode.www.exception.OntServerException;
import org.coode.www.exception.RedirectException;
import org.coode.www.mngr.SessionManager;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLOntology;

import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jul 3, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 *
 * Cannot subclass AbstractOntologyServerServlet as that checks if an ontology has been loaded
 */
public class ManageOntologies extends AbstractOntologyServerServlet {

    protected void handleXMLRequest(Map<String, String> params, OWLHTMLServer server, URL servletURL, PrintWriter out) throws OntServerException {
        // no implementation
    }

    protected HTMLDoclet handleHTMLRequest(Map<String, String> params, OWLHTMLServer server, URL pageURL) throws OntServerException {

        final String actionValue = params.get(OWLDocServerConstants.PARAM_ACTION);

        NestedHTMLDoclet ren = null;

        if (actionValue != null){
            ManageAction action = ManageAction.valueOf(actionValue);

            switch(action){
                case load:
                    boolean clear = (ServerConstants.TRUE.equals(params.get(OWLDocServerConstants.PARAM_CLEAR)));
                    ren = handleLoad(getURIsFromParams(params), clear, server);
                    break;
                case remove:
                    ren = handleRemove(getURIsFromParams(params), server);
                    break;
                case reload:
                    ren = handleReload(getURIsFromParams(params), server);
                    break;
                case browse:
                    ren = handleBrowse(getURIFromParam(params.get(OWLDocServerConstants.PARAM_URI)), server);
                    break;
                case hide:
                    handleSetVisibility(getURIFromParam(params.get(OWLDocServerConstants.PARAM_URI)), false, server);
                    break;
                case unhide:
                    handleSetVisibility(getURIFromParam(params.get(OWLDocServerConstants.PARAM_URI)), true, server);
                    break;
            }
        }

        if (ren == null){
            final Map<URI, URI> map = ManageOntologies.getMap(server);
            ren = createManagePageRenderer(server, map, null);
        }

        return ren;
    }

    protected Map<String, Set<String>> getRequiredParams(OWLServer server) {
        Map<String, Set<String>> required = new HashMap<String, Set<String>>();
//        required.put(PARAM_URI, Collections.singleton("<ontology uri>")); optional
//        required.put(PARAM_ACTION, getActionRenderings());
        return required;
    }

    private Set<String> getActionRenderings() {
        Set<String> actions = new HashSet<String>();
        for (ManageAction action : ManageAction.values()){
            actions.add(action.toString());
        }
        return actions;
    }

    private NestedHTMLDoclet handleLoad(Set<URI> uris, boolean clear, OWLHTMLServer server) throws OntServerException {
        Set<URI> success = new HashSet<URI>();
        Set<URI> fail = new HashSet<URI>();

        String message = "";

        if (clear){
            server.clearOntologies();
        }

        for (URI uri : uris){
            try{
                if (uri.isAbsolute()){
                    server.loadOntology(uri);
                    success.add(uri);
                }
                else{
                    fail.add(uri);
                }
            }
            catch(Exception e){
                fail.add(uri);
            }
        }


        if (!fail.isEmpty()){
            for (URI uri : fail){
                message += "failed to load: " + uri + "<br />";
            }
        }
        if (!success.isEmpty()){
            SessionManager.labelServerState(server);
            message += "<p>loaded " + success.size() + " ontologies</p>";
            message += "<p>saved session: [" + server.getCurrentLabel() + "]</p>";
        }

        Map<URI, URI> map = ManageOntologies.getMap(server);

        if (map.isEmpty() || map.containsValue(null)){ // empty or missing value in map
            return createManagePageRenderer(server, map, message);
        }
        else{
            throw new RedirectException(server.getBaseURL());
        }
    }


    private NestedHTMLDoclet handleRemove(Set<URI> uris, OWLHTMLServer server) throws OntServerException {
        Set<URI> success = new HashSet<URI>();
        Set<URI> fail = new HashSet<URI>();
        for (URI uri : uris){
            if (server.getOntology(uri) != null){
                server.removeOntology(uri);
                success.add(uri);
            }
            else{
                fail.add(uri);
            }
        }

        String message = "";
        if (!success.isEmpty()){
            SessionManager.labelServerState(server);
            message += "<p>removed " + success.size() + " ontologies</p>";
            message += "<p>saved session: [" + server.getCurrentLabel() + "]</p>";
        }

        return createManagePageRenderer(server, getMap(server), message);
    }


    private NestedHTMLDoclet handleReload(Set<URI> uris, OWLHTMLServer server) throws OntServerException {
        Set<URI> fail = new HashSet<URI>();
        Set<URI> success = new HashSet<URI>();
        for (URI uri : uris){
            try {
                server.getOWLOntologyManager().reloadOntology(uri);
                success.add(uri);
            }
            catch (Exception e) {
                fail.add(uri);
            }
        }


        String message = "";
        if (!success.isEmpty()){
            SessionManager.labelServerState(server);
            message += "<p>reloaded " + success.size() + " ontologies</p>";
            message += "<p>saved session: [" + server.getCurrentLabel() + "]</p>";
        }

        if (!fail.isEmpty()){
            for (URI uri : fail){
                message += "failed to load: " + uri + "<br />";
            }
        }
        return createManagePageRenderer(server, getMap(server), message);
    }


    private NestedHTMLDoclet handleBrowse(URI uri, OWLHTMLServer server) throws OntServerException {
        if (uri != null){
            final OWLOntology ontology = server.getOntology(uri);
            if (ontology != null){
                server.setActiveOntology(ontology);
                throw new RedirectException(server.getBaseURL());
            }
        }
        throw new OntServerException("Cannot browse to unknown ontology: " + uri);
    }


    private boolean handleSetVisibility(URI uri, boolean visible, OWLHTMLServer server) {
        if (uri != null){
            final OWLOntology ontology = server.getOntology(uri);
            if (ontology != null){
                server.setOntologyVisible(ontology, visible);
                return true;
            }
        }
        return false;
    }


    private NestedHTMLDoclet createManagePageRenderer(OWLHTMLServer server, Map<URI, URI> map, String message) throws OntServerException {

        EmptyOWLDocPage ren = new EmptyOWLDocPage(server);
        ren.setAutoFocusedComponent(OWLDocServerConstants.LOAD_ONTOLOGIES_INPUT_ID);

        if (map.isEmpty()){
            ren.addDoclet(new BlurbDoclet());
            ren.addDoclet(new LoadFormDoclet());
        }
        else{
            if (map.containsValue(null)){
                if (message == null){
                    message = "";
                }
                message += ("<p class='warn'>There appear to be missing imports in your ontology.</p>" +
                            "<p>You can specify a location for any that have not been loaded in the following table.<br />" +
                            "Or, you can <a style='font-weight: bolder; color: blue;' target='_top' href='" + server.getBaseURL() +
                            "'>continue to browse</a> your ontology without loading the imports.</p>");
            }

            if (message != null){
                ren.addMessage(message);
            }
            
            ren.addDoclet(new LoadFormDoclet());

            OntologyMappingsTableDoclet table = new OntologyMappingsTableDoclet(server);
            table.setMap(map);
            ren.addDoclet(table);
        }

        return ren;
    }


    public static Map<URI, URI> getMap(OWLServer server){
        Map<URI, URI> locationMap = new HashMap<URI, URI>();
        final Set<OWLOntology> ontologies = server.getOntologies();
        for (OWLOntology ont : ontologies){
            locationMap.put(ont.getURI(), server.getOWLOntologyManager().getPhysicalURIForOntology(ont));
            for (OWLImportsDeclaration importsDecl : ont.getImportsDeclarations()){
                if (server.getOWLOntologyManager().getOntology(importsDecl.getImportedOntologyURI()) == null){
                    locationMap.put(importsDecl.getImportedOntologyURI(), null);
                }
            }
        }
        return locationMap;
    }


    private Set<URI> getURIsFromParams(Map<String, String> params) {
        final URI uri = getURIFromParam(params.get(OWLDocServerConstants.PARAM_URI));
        if (uri != null){
            return Collections.singleton(uri);
        }
        else{
            Set<URI> uris = new HashSet<URI>();
            for (String param : params.keySet()){
                try {
                    URI ontURI = new URI(param);
                    if (ontURI.isAbsolute()){
                        URI physicalURI = new URI(params.get(param));
                        if (physicalURI.isAbsolute()){
                            uris.add(physicalURI);
                        }
                    }
                }
                catch (URISyntaxException e) {
                    // this wasn't a mapping property - do nothing
                }
            }
            return uris;
        }
    }

    private URI getURIFromParam(String param){
        if (param != null && param.length() > 0){
            String ontURIStr = param.trim();
            if (ontURIStr != null){
                return URI.create(ontURIStr);
            }
        }
        return null;
    }
}
