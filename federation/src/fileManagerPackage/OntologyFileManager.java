package fileManagerPackage;

import changeServerPackage.ChangeCapsule;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.OWLOntologyChangeException;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Jan 15, 2008
 * Time: 11:13:44 AM
 * Used to gain access to the base, tags and changes associated with one ontology stored in the system
 */
public class OntologyFileManager {
    public static final String CHANGESFOLDER = "changes";
    public static final String TAGSFOLDER = "tags";
    public static final String ONTOLOGYFILEEXTENSION = ".owl";

    private static HashMap<String, OntologyFileManager> fileManagerListSingleton = new HashMap<String, OntologyFileManager>();  //this object is created when this class is first referenced

    ArrayList<File> changes = new ArrayList<File>();
    ArrayList<File> tags = new ArrayList<File>();
    File baseline = null;
    ChangeWriter changeWriter;
    ChangeReader changeReader;
    File changesFolder = null;
    File tagsFolder = null;


    /** Singleton access to a file manager object for each possible ontologyURI (hashtable mapped).
     * This is used to ensure that the same object is used in all access to the storage system. If
     * the same object is used synchronized methods can be used to reliably implement transaction-based access */
    public static OntologyFileManager getInstance(String uri) throws IOException {
        if (!fileManagerListSingleton.containsKey(uri)) {   //populate the hashmap, if it doesn't already contain instances
            fileManagerListSingleton.put(uri, new OntologyFileManager(shortenURI(uri)));
        }

        return fileManagerListSingleton.get(uri);
    }

    /** transforms a URI into something that can be used for a folder name */
    public static String shortenURI(String uri) {
        String shortenedName = uri.replaceFirst("http://","");   //cut out the http prefix
        //shortenedName.replace('/','_');
        try {
            shortenedName = URLEncoder.encode(shortenedName, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return shortenedName;    //make uri into file system friendly filename string
    }


    /** instantiates a new File Manager for an ontology of a given name (filename).
     * If a directoy of this name does not already exist, it is (currently) NOT created.,
     * (The constructor is protected, so that this class can't be instantiated directly) */
    protected OntologyFileManager(String ontologyName) throws IOException {
        File ontologyFolder = new File(ontologyName);
        if (!ontologyFolder.isDirectory()) throw new IOException("No ontology folder found");

        for(File file : ontologyFolder.listFiles()) {
            if (file.toString().endsWith(CHANGESFOLDER) && file.isDirectory()) changesFolder = file;
            if (file.toString().endsWith(TAGSFOLDER) && file.isDirectory()) tagsFolder = file;
            if (!file.isDirectory() && file.getName().endsWith(ONTOLOGYFILEEXTENSION )) baseline = file;
        }

        if (changesFolder == null) {
            File newFolder = new File(ontologyFolder, CHANGESFOLDER);
            newFolder.mkdir();
        }
        if (tagsFolder == null) {
            File newFolder = new File(ontologyFolder, TAGSFOLDER);
            newFolder.mkdir();
        }
        if (baseline == null) {
            System.err.println("Error: no baseline ontology");
        }

        changeWriter = new ChangeWriter(changesFolder);
        changeReader = new ChangeReader(changesFolder);

        for(File file : changesFolder.listFiles()) {
            if (file.getName().startsWith(ChangeWriter.CHANGEPREFIX) && file.getName().endsWith(ChangeWriter.CHANGEEXTENSION)) {
                changes.add(file);
            }
        }
        for(File file : tagsFolder.listFiles()) {
            if (file.getName().startsWith(TagWriter.TAGPREFIX) && file.getName().endsWith(TagWriter.TAGEXTENSION)) {
                tags.add(file);
            }
        }

        Comparator<? super File> comp = new Comparator() {
            public int compare(Object o, Object o1) {
                return new Integer(chunkName(o)).compareTo(new Integer(chunkName(o1)));
            }

            public String chunkName(Object name) {
                String name2 = ((File)name).getName();
                if (name2.startsWith(TagWriter.TAGPREFIX)) {
                    name2 = name2.substring(TagWriter.TAGPREFIX.length());  //cut the prefix "tag" off the tag filename
                    name2 = name2.substring(0, name2.indexOf(".")); //cut off the file extension (.txt)
                }
                if (name2.startsWith(ChangeWriter.CHANGEPREFIX)) {
                    name2 = name2.substring(ChangeWriter.CHANGEPREFIX.length());  //cut the prefix "tag" off the change filename
                    name2 = name2.substring(0, name2.indexOf("."));
                }
                return name2;
            }
        };
        Collections.sort(changes, comp);
        Collections.sort(tags, comp);
    }


    /** returns the latest tag, or the baseline, if no tags have been created */
    public File getLatestTag() {
        File latest = baseline;
        if (tags.size() > 0) {
            latest = tags.get(tags.size()-1);   //get last item in the sorted list of tags
        }

        return latest;
    }

    /** returns the sequence number of the newest change in the manager */
    public long getLatestChangeSequenceNumber() {
        long latestChangeNumber = 0;
        if (changes.size() > 0) {
            ChangeCapsule cp = changeReader.getChange(changes.get(changes.size()-1));   //access the last existing change capsule
            latestChangeNumber = cp.getSequence();
        }
        return latestChangeNumber;
    }

    /** returns the ChangeCapsule belonging to a specific change sequence number */
    public ChangeCapsule getSpecificChange(Long sequenceNumber) throws IOException {
        ChangeCapsule cp = null;
        if (sequenceNumber >= 0) {
            cp = changeReader.getChange(sequenceNumber);


            //error condition: if there is a mismatch between the requested sequence number and the file number, iterate through all the change until the correct sequence number is foudn
            if (sequenceNumber != cp.getSequence()) {
                System.err.println("Error: sequence number mismatch. Requested: "+sequenceNumber+". Found: "+cp+". Now iterating to resolve issue");
                for(File c : changes) {
                    cp = changeReader.getChange(c);
                    if (cp.getSequence() == sequenceNumber) {
                        return cp;
                    }
                }
            }
        }

        if (cp == null) throw new IOException("Error: change file count not be found");

        return cp;
    }

    /** writes a new change to the list of changes */
    public void addChange(ChangeCapsule changeCapsule) throws IOException {
        File newChangeFile = changeWriter.writeChange(changeCapsule);    //write change to file
        changes.add(newChangeFile); //store change file reference in memory for future access (don't store the change object itself to save memory, otherwise, with lots of change, this might be far too big in terms of memory requirements, if we loaded and stored all changes)
    }

    /** creates a new tag based upon all changes up to and including the given sequence number */
    public String createTag(int sequenceNumber) {
        String error = "";

        ArrayList<ChangeCapsule> changesToApply = null;
        try {
            TagReader tagReader = new TagReader(getLatestTag());
            long latestChangeinLatestTag = tagReader.getLatestChangeInTag();
            long latestChangeOverall = getLatestChangeSequenceNumber();

            changesToApply = new ArrayList<ChangeCapsule>();
            for(long i=latestChangeinLatestTag; i < latestChangeOverall; i++) {
                changesToApply.add(changeReader.getChangeBySequenceNumber(i));
            }

            TagWriter tagWriter = new TagWriter(getLatestTag()); //give lateset existing new filename (the constructor automatically increments the sequence so that a new file is created)
            tagWriter.applyChanges(changesToApply);

            File newFile = tagWriter.saveNewTag(); //increment the counter and save the tag to disk
            tags.add(newFile);  //add the new tag to the list of tags in memory

        } catch (URISyntaxException e) {
            e.printStackTrace();
            error += e.toString();
        } catch (OWLOntologyChangeException e) {
            e.printStackTrace();
            error += e.toString();
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
            error += e.toString();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            error += e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            error += e.toString();
        }

        return error;
    }

    /** create a new folder for a new ontology from a URI (does nothing, if the folder already exists) */
    public static boolean createBaseline(String uri) throws IOException {
        File baselineFile = new File(shortenURI(uri));
        boolean created = baselineFile.mkdirs();    //create the directory, if not already existing
        getInstance(uri).baseline = baselineFile;   //set the appropriate object's baseline file, creating the object, if necessary
        return created;
    }
}
