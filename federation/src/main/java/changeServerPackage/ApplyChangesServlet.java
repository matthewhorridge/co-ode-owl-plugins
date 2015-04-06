package changeServerPackage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 14, 2008
 * Time: 3:02:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApplyChangesServlet extends HttpServlet {
    public final static String COMMIT = "commit";
    public final static String QUERY = "query";
    public final static String UPDATE = "update";

    public final static String PARAMETER_COMMAND = "command";
    public final static String PARAMETER_CAPSULE= "capsule";


    static ChangeServerImpl changeServer = new ChangeServerImpl(new BasicConflictDetectorImpl());


    /** foward a get request to the POST request handler */
     public void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
         doPost(request, response);
     }

     /** Receive a change from the client */
     public void doPost(HttpServletRequest request,
                        HttpServletResponse response)
         throws IOException, ServletException
     {
         String command = request.getParameter("command");  //variable to hold the ontology URI
         //String ontologyURIToQuery = request.getParameter("uri");  //variable to hold the ontology URI (now held in capsule)
         ChangeCapsule changeCapsule = new ChangeCapsule(request.getParameter("capsule")); //hold the changeCapsule prarsed from JSON information
         String responseText = "";  //text to send to client

         if (command == null || (command.compareToIgnoreCase(QUERY)!=0 && command.compareToIgnoreCase(COMMIT)!=0 && command.compareToIgnoreCase(UPDATE)!=0)) {
             responseText += "Error: invalid command\n";
         }
         if (changeCapsule.getOntologyURI() == null) {
             responseText += "Error: no ontology URI given\n";
         }
         if (changeCapsule.getSequence() < 0) {
             responseText += "Error: no sequence number set\n";
         }

         if (responseText.length() == 0) {  //no errors, so continue
             //send the response of the number of the latest change on the server
             if (command.compareToIgnoreCase(QUERY) == 0) {
                 responseText = changeServer.getLatestChangeNumber(changeCapsule.getOntologyURI());
             }

             //returns the serialized change object corresponding to a specific change number
             if (command.compareToIgnoreCase(UPDATE) == 0) {
                 responseText = changeServer.getSpecificChange(changeCapsule.getOntologyURI(), changeCapsule.getSequence());
             }

             //returns whether or not the the sent change as accepted by the server's conflict detection algorithm
             if (command.compareToIgnoreCase(COMMIT) == 0) {
                 responseText = changeServer.recordChange(changeCapsule);  //singleton access
             }
         }

         /*ResourceBundle rb =
             ResourceBundle.getBundle("LocalStrings",request.getLocale());
         response.setContentType("text/html");
         PrintWriter out = response.getWriter();

         out.println("<html>");
         out.println("<head>");

         String title = error; //rb.getString(error);

         out.println("<title>" + title + "</title>");
         out.println("</head>");
         out.println("<body bgcolor=\"white\">");
         out.println("<h1>" + title + "</h1>");
         out.println("</body>");
         out.println("</html>");*/

         //send response
         response.setContentType("text/html");
         PrintWriter out = response.getWriter();
         out.print(responseText);
         out.flush();
         out.close();
     }

    public ApplyChangesServlet() {

    }


    public static void main(String[] args) {
        new ApplyChangesServlet();
    }

}
