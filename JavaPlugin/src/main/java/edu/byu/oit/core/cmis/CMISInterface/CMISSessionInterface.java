package edu.byu.oit.core.cmis.CMISInterface;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

import java.util.ArrayList;
import java.util.Map;


public interface CMISSessionInterface {

    /**
     * Sets the username and password for the session. This must be set prior to calling "startSession()"
     * @param username Your username
     * @param password Your password
     */
    public void setCredentials(String username, String password);

    /**
     * Initializes the session relative to the appropriate login credentials and box.
     * @pre Assumes that .setCredentials(username, password) has been called with valid login credentials.
     * @post The session is initialized and all it's methods are usable.
     */
    public void startSession();


    //----------------
    //create         |
    //----------------

    /**
     * Upload a local document to the repository
     * @pre <code>String Description</code> argument can be null or "" if necessary.
     * @post A valid, non-null Cmis Document object
     * @param folderId The objectID of the folder into which you wish to upload your document.
     * @param fileName The name that the uploaded document will assume on the server.
     * @param contentStream The contentStream containing the documents actual content.
     * @param contentType The content type associated with the document, IE "cmis:document". If null, "cmis:document" is supplied as a default.
     * @param version The document version associated with the uploaded document.
     * @param description The description metadata you may wish to post attached to the document. null or "" are appropriate arguments, if that's what you want.
     * @return Returns a Document object representing the file you just uploaded to the server.
     */
    public Document uploadDocument(String folderId, String fileName, ContentStream contentStream, String contentType, String version, String description);

    /**
     * Creates an empty folder in the repository, given the path in the repo, and a name.
     * @pre A valid, non-null <code>String path</code> must follow format "/home/user/aFolder/" <br>
     * <code>String folderName</code> must be a valid, non-null String designating the folder name
     * @post A valid, non-null cmis Folder object.
     * @param path The String representation of the path in the repo, to where you wish to create your folder.
     * @param folderName The name you wish to give your new folder.
     * @return Returns a Folder object representation of the folder you just created in the repo.
     *
     */
    public Folder createFolder(String path, String folderName);

    /**
     * Upload a folder, including all containing files and sub-folders, to the repository.
     * @param folderId The objectID of the Folder inside which you wish to upload the local folder and subdirectory.
     * @param folderName The name of the root folder as it will appear in the repository
     * @param folderPath The path to the local root folder of the directory you wish to upload to the server
     * @return Returns a Folder object representation of the root folder you just uploaded.
     * @pre <code>String folderPath</code> must follow format "/home/user/aFolder/"<br>
     * <code>String folderName</code> must be a valid, non-null String object
     * @post A valid, non-null cmis Folder object which represents an existing folder on the repository
     */
    public Folder uploadFolder(String folderId, String folderName, String folderPath);

    //----------------
    // Read          |
    //----------------

    /**
     * Retrieve a folder from the repository
     * @param objectId The objectId of the folder you wish to retrieve
     * @return Returns the Folder object representation of the folder in the repository, which you can update/delete/otherwise manipulate
     * @pre <code>String objectID</code> must be a valid, non-null String retrieved from the repository in connection with an actual CMIS object
     * @post A valid, non-null cmis Folder object
     */
    public Folder getFolder(String objectId);

    /**
     * Retrieves a Document from the repository.
     * @param objectId The objectId of the document you wish to retrieve
     * @return Returns a Document object representation of the document in the repository, which you can update/delete/otherwise manipulate
     * @pre <code>String objectID</code> must be a valid, non-null String retrieved from the repository in connection with an actual CMIS object
     * @post A valid, non-null cmis Document object representing a document existing in the repository.
     */
    public Document getDocument(String objectId);

    /**
     * Retrieves a Document using the path to the file in the repository.
     * @param pathToDocument The path pointing to the document in the repository.
     * @return Returns a Document object representation of the document in the repository, which you can update/delete/otherwise manipulate.
     * @pre <code>String pathToDocument</code> must follow format "/User_Homes/user/aFolder/doc.txt"
     * @post A valid, non-null cmis Document object representing a document existing in the repository.
     */
    public Document getDocumentByPath(String pathToDocument);

    /**
     * Retrieves the thumbnail representation of the given Document.
     * @param doc The Document object representation of the thumbnail you wish to retrieve.
     * @return Returns a Rendition object, which is the class encapsulation of all alternate representations of a normal CMIS object. A thumbnail is one such alternate representation of a document.
     * @pre <code>Document doc</code> must be a valid, non-null cmis Document object.
     * @post A valid, non-null, cmis Rendition object.
     */
    public Rendition getDocumentThumbnail(Document doc);

    /**
     * Forms a URL that retrieves the thumbnail of a document in the Alfresco data store, if available.
     * @param doc The document who's Alfresco thumbnail URL you wish to retrieve.
     * @return Returns the URL that accesses Alfresco's thumbnail retrieval webservice.
     * @pre <code>Document doc</code> must be a Document type that is supported for thumbnails: ie text files, images && for which a thumbnail creation event has already been triggered.
     * @post A valid URL pointing to the thumbnail.
     */
    public String getAlfrescoThumbnailUrl(Document doc);

    /**
     * Forms a URL that retrieves the full document from the Alfresco data store.
     * @param doc The document whose URL you'd wish to retrieve.
     * @return Returns a URL that accesses the file passed in as an argument.
     * @pre <code>Docuemtn Doc</code> must be a non-null Document that exists in the data store.
     * @post A valid URL pointing to the document.
     */
    public String getAlfrescoDocumentUrl(Document doc);

    /**
     * Retrieves all of the given Document's metadata, returned in the form of a String.
     * @param doc The Document who's metadata you wish to retrieve.
     * @return Returns a string with all the metadata for the given Document
     * @pre A valid, non-null cmis Document object representing a document existing in the repository.
     * @post A valid, non-null String formatted with all Document metadata
     */
    public String getDocumentMetadataAll(Document doc);

    /**
     * Extracts and formats the given property values into a String object
     * @param properties An array of properties previously extracted from a document.
     * @return The properties and their associated values formatted as a String
     * @pre <code>properties.size() > 0</code>, and <code>properties.get(i) != null</code>
     * @post A valid, non-null String object formatted with the properties
     */
    public String getDocumentMetadataByProperty(ArrayList<Property> properties);

    /**
     * Retrieves the complete version history of a given document, formatted as a String object
     * @param doc The Document who's version history you wish to retrieve
     * @return Returns the String object containing the complete document history of the given Document
     * @pre <code>Document doc</code> Is a valid, non-null cmis Document object representing a real document in the repository.
     * @post A valid, non-null String object formatted with the Document version history.
     */
    public String getDocumentVersionHistory(Document doc);

    /**
     * Retrieves and ItemsIterable container object containing all the CmisObjects in a given folder.
     * @param folder The folder who's contents you wish to retrieve.
     * @return Returns an ItemsIterable container of CmisObjects found in the given Folder.
     * @pre <code>Folder folder != null</code>
     * @post Returned <code>ItemIterable<CmisObject> != null && size() corresponds to the number of items in the folder</></code>
     */
    public ItemIterable<CmisObject> getFolderContents(Folder folder);


    //----------------
    // Update        |
    //----------------

    /**
     * Takes in a Map of Document properties you wish to change, and alters the the given Document to reflect those changes.
     * @param doc The Document object who's metadata properties you wish to alter.
     * @param properties The Map of properties and values to which the given Document will update.
     * @return Returns an IObjectID which contains the short and long ObjectID of the newly updated Document.
     * @pre <code>Document doc != null</code> && <code>properties.size() > 0 && properties != null</code>
     * @post A valid, non-null <code>IObjectID</code> object.
     */
    public IObjectID updateDocumentMetadataByProperty(Document doc, Map<String, Object> properties);

    /**
     * A given document adopts a new given content stream.
     * @param contentStream The new content wich the Document will contain.
     * @param docToUpdate The Document who's content you wish to change.
     * @return Returns and IObjectID which contains the short and long ObjectID of the newly updated document.
     * @pre <code>contentStream != null && docToUpdate != null</code>
     * @post A valid, non-null <code>IObjectID</>.
     */
    public IObjectID updateDocumentContent(ContentStream contentStream, Document docToUpdate);

    /**
     * Updates the name of an existing folder on the server.
     * @param name The new name to be assigned to the existing Folder.
     * @param folderId The objectId of the existing folder whose name you wish to change.
     * @return Returns an IObjectID which contains the short and long ObjectID of the newly updated document.
     * @pre <code>String name != null && String folderId != null</code>
     * @post A valid, non-null <code>IObjectId</code>.
     */
    public IObjectID updateFolderName(String name, String folderId);

    /**
     * Updates the name of an existing document on the server.
     * @param name The new name to be assigned to the existing Document.
     * @param documentId The ObjectID of the Document whose name you wish to change.
     * @return Returns an IObjectID containing the short and long ObjectId's of the newly created Document.
     * @pre <code>String name != null, String documentId != null</code>
     * @post A valid, non-null <code>IObjectId</code>
     */
    public IObjectID updateDocumentName(String name, String documentId);

    //----------------
    // Delete        |
    //----------------

    /**
     * Deletes the given document.
     * @param documentId The ObjectId of the Document you wish to delete.
     * @return Returns true if the delete was a success, false otherwise.
     * @pre <code>String docuemntId != null</code>
     * @post Boolean assessment of True
     */
    public boolean deleteDocument(String documentId);

    /**
     * Deletes a Folder on the server, given the Folder's ObjectID.
     * @param folderId The ObjectID of the Folder you wish to delete.
     * @return Returns true if the delete succeeded, false otherwise.
     * @pre <code>String folderId != null</code>
     * @post Boolean assessment of True
     */
    public boolean deleteFolder(String folderId);

    //---------------
    // Other        |
    //---------------

    /**
     * Gets a ticket.
     * @return Returns a ticket.
     * @post <code>String getTicket() != null</code>
     */
    public String getTicket();

    /**
     * Executes a query on the repository
     * @param Query The query in the form of a string that y6ou wish to execute on the server.
     * @return Returns true if the query was successful, false otherwise.
     * @pre <code>String Query</code> follows Lucene query standards, <code>String query != null</code>
     * @post <code>ItemIterable<QueryResult> executeQuery(String Query) != null</code>
     */
    public ItemIterable<QueryResult> executeQuery(String Query);

    /**
     * Downloads a Document to a local directory from the repository
     * @param doc The Document in the repository that you wish to download.
     * @param localDestination The local destination to where you wish to download the Document.
     * @return Returns true if the download succeeded, false otherwise.
     * @pre <code>Document doc != null && String localDestination != null</code>
     * @post Boolean assessment of True if the Document exists in the repository, and false otherwise.
     */
    public boolean downloadDocument(Document doc, String localDestination);

    /**
     * Downloads all the Documents in a given folder (but NOT Folders) to the given local destination.
     * @param folderId The ObjectId of the Folder who's contents you wish to download locally.
     * @param localDestination The path to the Folder to where you wish download the contents of the given Folder.
     * @return Returns true if the download succeeds, false otherwise.
     * @pre <code>String folderId != null && String localDestination != null &&</code><br>
     *     localDestination follows this format: "/home/user/aFolder/" .
     * @post Boolean assessment of True if the folder exists in the repository and the content was successfully downloaded, and false otherwise.
     */
    public boolean downloadFolderContent(String folderId, String localDestination);


    //-----------------
    // Helper Methods |
    //-----------------

    /**
     * Creates a shortId version of the id retrieved from a .getId() method on a document or a folder.
     * @param longId The id retrieved from a .getId() method on a document or a folder.
     * @return Creates a shortId version of the id retrieved from a .getId() method on a document or a folder.
     */
    public String createShortIdFromLong(String longId);
    /**
     * Gets the boxname currently in use so the developer can easily form URLs and take advantage of the Alfresco REST api
     */
    public String getBoxNameUrl();
    /**
     * Gets the ObjectId of a Folder/Document given the path to the Folder/Document.
     * @param path The path to the Folder/Document who's ObjectId you wish to retrieve.
     * @return Returns the ObjectId of the Folder/Document located at the given path.
     * @pre <code>String path != null</code>
     * @post A valid, non-null <code>IObjectId</code>.
     */
    public IObjectID getObjectIdByPath(String path);

    /**
     * Creates the ContentStream of a new Document. ContentStreams can be used to upload content to a repository using the uploadDocument() method.
     * @param newName The name of the new Document.
     * @param filePath The local path to the file content.
     * @return Returns a ContentStream containing the new Document's name and content.
     * @pre <code>String newName != null && String filePath != null && filePath points to a valid document</code>
     * @post A valid, non-null <code>ContentStream</code> containing the content at <code>filePath</code>
     */
    public ContentStream createDocument(String newName, String filePath);

    /**
     *
     * @param doc
     * @return
     */
    public Boolean doesAlfrescoThumbnailExist(Document doc);

}
