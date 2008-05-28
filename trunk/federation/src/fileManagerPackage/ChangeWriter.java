package fileManagerPackage;

import changeServerPackage.ChangeCapsule;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 14, 2008
 * Time: 3:18:51 PM
 * Used to write a change to a file for storage
 */
public class ChangeWriter {
    public static final String CHANGEPREFIX = "c";
    public static final String CHANGEEXTENSION = ".txt";
    public static final int LEADINGZEROSNUMBER = 7;
    public final File baseFilename;

    public ChangeWriter(File baseFilename) {
        this.baseFilename = baseFilename;
    }

    /** writes a changeCapsule into a single file on the server */
    public File writeChange(ChangeCapsule changeCapsule) throws IOException {
        String filename = Long.toString(changeCapsule.getSequence());
        filename = addLeadingZeros(filename, LEADINGZEROSNUMBER);
        filename = CHANGEPREFIX + filename + CHANGEEXTENSION;
        File file = new File(baseFilename.getPath(), filename);
        OutputStream outputstream = new FileOutputStream(file);

        PrintWriter pr = new PrintWriter(outputstream);
        pr.write(changeCapsule.toJSON());
        pr.flush();
        pr.close();

        /*Properties prop = new Properties();
        prop.setProperty(ChangeServerInterface.username, changeCapsule.getUsername());
        prop.setProperty(ChangeServerInterface.timestamp, changeCapsule.getTimestamp());
        prop.setProperty(ChangeServerInterface.summary, changeCapsule.getSummary());
        prop.setProperty(ChangeServerInterface.change, changeCapsule.getChange());
        prop.setProperty(ChangeServerInterface.sequence, Integer.toString(changeCapsule.getSequence()));

        prop.store(outputstream, "");*/

        return file;
    }


    /** adds zeros in front of the filename, so that it is displayed and read in the correct sort order */
    public static String addLeadingZeros(String str, int numberOfZeros) {
        while(str.length() < numberOfZeros) {
            str = "0"+str;
        }

        return str;    //append "c" before the number
    }
}
