package edu.byu.oit.core.cmis.tests.CMISAbstract;


import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractCMISsession {

    private Session session;
    private Folder currentFolder;

    //----------------------------------
    //connection, getters and setters
    //----------------------------------

    /**
     * Initializes the session with a particular box/repository.
     * @param boxName The name of the Server to which you would like to connect.
     * @param username The username to be used as credentials when logging into the server.
     * @param password The Password to be used as credentials when logging into the server.
     */
    abstract public void initializeSession(String boxName, String username, String password);

    /**
     * Changes the current session to the session you pass in
     * @param session_in The incoming session which you would like to use as the current working session, on which all repo operations can be called.
     */
    public void setSession(Session session_in){

        session = session_in;
    }

    /**
     * Returns the current session. A session is a connection to a server, and contains all CRUD methods.
     * @return Returns the current session. A session is a connection to a server, and contains all CRUD methods.
     */
    public Session getSession(){

        return session;
    }

    /**
     * When navigating manually through the server directory, there is always a 'current' folder in which the user is located at any given time. This returns that folder.
     * @return When navigating manually through the server directory, there is always a 'current' folder in which the user is located at any given time. This returns that folder.
     */
    public Folder getCurrentFolder() {

        return currentFolder;
    }

    /**
     * Set the current working folder to the Folder passed in as an arg.
     * @param currentFolder Used in navigation. This method basically sets the "current working directory" to the incoming folder.
     */
    public void setCurrentFolder(Folder currentFolder) {

        this.currentFolder = currentFolder;
    }

    //------------------------------
    //create -- ie "upload"
    //------------------------------

    /**
     * Creates a ContentStream for a local file which can then be uploaded to the server through the 'uploadDocument()' method.
     * @param newName When you create a document, it needs a name. This param gives you that new name.
     * @param filePath The path to the local file which you would like to create and manipulate in memory.
     * @return Returns a ContentStream which can be used to upload the newly created document to the repo.
     */
    public ContentStream createDocument(String newName, String filePath){
        Path path = FileSystems.getDefault().getPath(filePath);
        byte[] buf = {};
        String mimetype = "";

        try {
            buf = Files.readAllBytes(path);
            mimetype = Files.probeContentType(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream input = new ByteArrayInputStream(buf);
        return session.getObjectFactory().createContentStream(newName, buf.length, mimetype, input);
    }

    /**
     * Uploads a document (contentStream) to a specified server folder
     * @param folderId The ID of the folder on the repository to which you'd like to upload a file.
     * @param filename The name of the file as it will show up in the repository after the upload.
     * @param contentStream The contentStream produced by the "createDocument" method, which contains the bytes of the document you are trying to upload.
     * @param contentType The repository-defined content type. IE an alfresco document's content type is "cmis:document"
     * @param version The version of the file you are uploading.
     * @return Returns the Document that has just been uploaded to the server.
     */
    public Document uploadDocument(String folderId, String filename, ContentStream contentStream, String contentType, String version){
        if (contentType == null) contentType = "cmis:document";

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, contentType);
        properties.put(PropertyIds.NAME, filename);

        Folder folder = getFolder(folderId);

        VersioningState v = VersioningState.MINOR;
        if (version != null && version.toLowerCase().equals("major")) v = VersioningState.MAJOR;

        Document doc = folder.createDocument(properties, contentStream, v);
        System.out.println("The following Document has been uploaded: " + doc.getName());
        return doc;
    }

    /**
     * Uploads a folder to a specified server folder. This includes all of that folder's internal content. (?)
     * @param folderPath The local path to the folder you wish to upload.
     * @return Returns that Folder just uploaded to the server.
     */
    public Folder uploadFolder(String folderPath){
        return null;
    }

    //------------------------------
    //Read -- ie "get"s
    //------------------------------

    /**
     * Retrieving an object from the server requires having that object's ID. This method allows you to retrieve the object's ID based on its path in the repository.
     * @param path The path of the object on the server.
     * @return returns the ID of the object specified by the path.
     */
    public String getObjectIdByPath(String path){
        CmisObject object = session.getObjectByPath(path);
        String id = object.getId();
        return id;
    }

    /**
     * Retrieves a given document when given the Document's ID (objectID)
     * @param id The ID of the object you wish to retrieve.
     * @return returns the Document associated with the given ID on the server.
     */
    public Document getDocument(String id){
        Document doc = (Document)session.getObject(id);
        return doc;
    }

    /**
     * Retrieves a given Document when given that Document's path in the repository
     * @param path The path of the document on the server which you would like to get
     * @return Returns the Document associated with the given path on the server.
     */
    public Document getDocumentByPath(String path){
        CmisObject object = session.getObjectByPath(path);
        Document doc = (Document)object;
        return doc;
    }

    /**
     * Retrives a given Folder when given that Folder's ID (objectID)
     * @param id The ID of the folder located on the server which you would like to get.
     * @return Returns the Folder on the server associated with the given ID.
     */
    public Folder getFolder(String id){
        CmisObject object = session.getObject(id);
        Folder folder = (Folder)object;
        return folder;
    }

    /**
     * Prints the given folder's content metadata
     * @param folderId The FolderID (objectID) of the Folder on the server who's children's metadata you are trying to view.
     */
    public void printFolderContentMetadata(String folderId){
        folderFunction(folderId, 20, 0, true, false, null);
    }

    /**
     * Prints a given Document's metadata
     * @param doc The document who's metadata you'd like to print.
     */
    public void printMetadata(Document doc){

    }

    /**
     * Downloads a document from the server. All you have to do is 'get' the document by it's objectID, and pass it into this function along with a string designating the destination directory where you wish to store the document locally.
     * @param doc The Document on the server that you wish to download.
     * @param localDestination The destination directory to where you wish to download the Document.
     */
    public void downloadDocument(Document doc, String localDestination){
        String filename = doc.getName();
        java.io.InputStream stream = doc.getContentStream().getStream();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(localDestination +filename)); // ie localDestination == "/home/wlane/Downloads/AlfrescoTest/"
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = stream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            System.out.println("Finished downloading");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Downloads the content of the folder associated with the given folderID. You must also provide a String representing the path to the folder in which you wish to store the downloaded contents.
     * @param folderId The ID associated with the folder on the server you'd like to download
     * @param destinationDir The local directory to where you'd like to store the downloaded Folder contents
     */
    public void downloadFolderContent(String folderId, String destinationDir){
        //first get the folder by id
        folderFunction( folderId, 20, 0, false, true, destinationDir);
    }

    //--------------------------------
    //update
    //--------------------------------

    /**
     *
     * @param doc
     * @param metadateInfo
     */
    public void updateDocMetadata(Document doc, String metadateInfo){}

    /**
     *
     * @param doc
     */
    public void updateDocumentContent(Document doc){}

    /**
     *
     * @param folder
     * @param newName
     */
    public void updateFolderName(Folder folder, String newName){}

    //--------------------------------
    //delete
    //--------------------------------

    /**
     *
     * @param doc
     */
    public void deleteDocument(Document doc){}

    /**
     *
     * @param folderID
     */
    public void deleteFolder(String folderID){}

    //---------------------------------
    //other
    //---------------------------------

    /**
     *
     * @param query
     */
    public void executeQuery(String query){}

    /**
     *
     */
    public void getTicket(){}

    /**
     *
     */
    public void getConnection(){}

    /**
     * This method is used by different methods to perform 'download' and 'print' actions on a given folder
     * @param folderId The ID of the folder you are wanting to manipulate.
     * @param maxItemsPerPage The maximum number of items per page.
     * @param skipCount
     * @param print Boolean value that tells us whether to perform the 'print' functionality or not.
     * @param download Boolean value that tells us whether to perform the 'download' functionality or not.
     * @param downloadToThisLocalDir The directory folder to which we are downloading files, given that the 'download' flag is true. This field is set to null if performing a 'print'.
     */
    public void folderFunction(String folderId, int maxItemsPerPage, int skipCount, boolean print, boolean download, String downloadToThisLocalDir){
        CmisObject object = session.getObject(session.createObjectId(folderId));
        Folder folder = (Folder)object;

        OperationContext opContext = session.createOperationContext();
        opContext.setMaxItemsPerPage(maxItemsPerPage);

        ItemIterable<CmisObject> children = folder.getChildren(opContext);
        ItemIterable<CmisObject> page = children.skipTo(skipCount).getPage();

        Iterator<CmisObject> childItr = page.iterator();
        while(childItr.hasNext()) {
            Document item = (Document)childItr.next();
            if (print) {
                System.out.println("**********************************************");
                printMetadata(item);
            }
            if (download) {
                downloadDocument(item, downloadToThisLocalDir);
            }
        }
    }

    /**
     *  Lists the current Folder's contents (Folders and Documents). Current folder is defined as the Folder declared in the session's 'currentFolder' variable.
     */
    public void listCurrentFolderContents(){
        ItemIterable<CmisObject> children = currentFolder.getChildren();
        for(CmisObject o : children){
            System.out.println("[" + o.getName() + "]");
        }
        System.out.println();
    }

    /**
     * Simple function call that changes the current working directory to the immediately accessible folder matching the String folderName variable.
     * @param folderName The name of the folder in the currentFolder to which you'd like to navigate. 'listCurrentFolderContents()' will give you a list of valid folder options (as well as invalid Document items into which you cannot navigate).
     */
    public void navigateIntoFolder(String folderName){
        ItemIterable<CmisObject> children =currentFolder.getChildren();
        for(CmisObject o : children){
            if(o.getName().equals(folderName)){
                currentFolder = (Folder)o;
            }
        }
    }

}
