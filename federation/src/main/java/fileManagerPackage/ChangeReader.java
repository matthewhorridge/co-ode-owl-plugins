package fileManagerPackage;

import changeServerPackage.ChangeCapsule;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 11, 2008
 * Time: 3:45:33 PM
 * Gives access to the changes stored on disk for a certain ontology
 */
public class ChangeReader {

    File baseFilename;

    public ChangeReader(File baseFilename) {
        this.baseFilename = baseFilename;
    }


    /** Returns the change object with the requested sequence number */
    public ChangeCapsule getChangeBySequenceNumber(long sequenceNumber) throws IOException {
        FileInputStream input = new FileInputStream(new File(baseFilename, ChangeWriter.addLeadingZeros(Long.toString(sequenceNumber), ChangeWriter.LEADINGZEROSNUMBER)));
        return getChange(input);
    }

    /** retrieves a change object from a file reference */
    public ChangeCapsule getChange(File file) {
        ChangeCapsule changeCapsule = null;
        try {
            changeCapsule = getChange(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeCapsule;
    }

    /** retrieves a change object from a sequence number matching the change filename */
    public ChangeCapsule getChange(Long sequenceNumber) throws IOException {
        String filename = Long.toString(sequenceNumber);
        filename = ChangeWriter.addLeadingZeros(filename, ChangeWriter.LEADINGZEROSNUMBER);
        filename = ChangeWriter.CHANGEPREFIX + filename + ChangeWriter.CHANGEEXTENSION;

        File file = new File(baseFilename.getPath(), filename);
        return getChange(file);
    }


    private ChangeCapsule getChange(FileInputStream input) throws IOException {
        //read all the lines in the file
        BufferedReader bf = new BufferedReader(new InputStreamReader(input));
        String line = null;
        StringBuffer buff = new StringBuffer();
        while((line = bf.readLine()) != null) {
            buff.append(line);
        }

        //convert lines into ChangeCapsule object
        ChangeCapsule changeCapsule = new ChangeCapsule(buff.toString());

        return changeCapsule;
    }
}
