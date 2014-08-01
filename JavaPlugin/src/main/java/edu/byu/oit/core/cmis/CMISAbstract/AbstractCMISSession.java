package edu.byu.oit.core.cmis.CMISAbstract;

import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import edu.byu.oit.core.cmis.CMISInterface.IObjectID;
import edu.byu.oit.core.cmis.CmisUtilClasses.ObjectID;
import edu.byu.oit.core.cmis.CmisUtilClasses.ObjectPathModel;
import edu.byu.oit.core.cmis.CmisUtilClasses.TikaParser;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Office;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;


public abstract class AbstractCMISSession implements CMISSessionInterface {

    //----------------
    // Connection    |
    //----------------
    Session session;

    @Override
    abstract public void startSession();

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    //----------------
    //create         |
    //----------------
    @Override
    public Document uploadDocument(String folderId, String fileName, String filePath, String contentType, String version, String description ) throws IOException {
        if (contentType == null) contentType = "cmis:document";

        //uploading a folder consists of
        // 1) creating a document (ie content stream),
        // 2) scanning for metadata and setting the properties,
        // 3) using folderName.createDocument() to create the document in the repo

        // 1) CREATE DOCUMENT InputStream and ContentStream
        Path path = FileSystems.getDefault().getPath(filePath);
        byte[] buf = {};
        String mimetype = "";

        buf = Files.readAllBytes(path);
        mimetype = Files.probeContentType(path);

        ByteArrayInputStream input = new ByteArrayInputStream(buf);
        ContentStream contentStream = session.getObjectFactory().createContentStream(fileName, buf.length, mimetype, input);

        // 2) TRY Scanning metadata and setting properties
        Document doc = null;
        try{
            Metadata metadata = new Metadata();
            DefaultHandler handler = new DefaultHandler();
            Parser parser = new TikaParser();
            ParseContext context = new ParseContext();

            metadata.set(Metadata.CONTENT_TYPE, mimetype);

            parser.parse(input,handler, metadata, context);
            String author = metadata.get(Office.AUTHOR);
            String title = metadata.get(TikaCoreProperties.TITLE);

            //Append aspect to the content type so we can set description
            StringBuilder sb = new StringBuilder(contentType);
            sb.append(",P:cm:titled");
            contentType = sb.toString();

            Map<String, Object> properties = new HashMap<String, Object>();
            if (author != null || title != null) {
                System.out.println("Author:" + author);
                System.out.println("Title:" + title);
                properties.put("cmis:author", author);
                properties.put("cmis:title", title);
            }
            properties.put(PropertyIds.OBJECT_TYPE_ID, contentType);
            properties.put(PropertyIds.NAME, fileName);
            properties.put("cm:description", description);

            //3) CREATE the doument in the repo
            Folder folder = getFolder(folderId);
            VersioningState v = VersioningState.MINOR;
            if (version != null && version.toLowerCase().equals("major")) v = VersioningState.MAJOR;
            doc =folder.createDocument(properties, contentStream, v);
        }
        catch(TikaException te){
            System.out.println("Caught Tika Exception, Skipping...");
        }
        catch(SAXException se) {
            System.out.println("Caught SAXException, Skipping...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    public Folder createFolder(String path, String name){
        //create empty folder on server where name = 'folderName'
        IObjectID folderId = getObjectIdByPath(path);
        Folder folder = getFolder(folderId.getLongID());
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, name);
        Folder newFolder = folder.createFolder(properties);
        return newFolder;
    }

    @Override
    public Folder uploadFolder(String folderId, String folderName,String folderPath) throws IOException {

        //create empty folder on server where name = 'folderName'
        Folder folder = getFolder(folderId);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);
        Folder newFolder = folder.createFolder(properties);

        //enter 'foldername' and create documents in it based on the paths given in "pathsToFiles"
        ArrayList<ObjectPathModel> pathsToFiles = getFolderContentsForFolderUpload(folderPath);
        for(ObjectPathModel entry : pathsToFiles){
            if(entry.getObjectType().equals("file")) {
                //ContentStream cs = createDocument(entry.getObjectName(), entry.getObjectPath().toString());
                //try {
                    uploadDocument(newFolder.getId(), entry.getObjectName(), entry.getObjectPath().toString(), "cmis:document", "1", "");
//                } catch (NoSuchFileException e) {
//                    e.printStackTrace();
//                }
            }
            else if(entry.getObjectType().equals("folder")){
                // recursively call 'upload folder' to take care of sub-folders and their items
                uploadFolder(newFolder.getId(),entry.getObjectName(), entry.getObjectPath().toString());
            }
        }
        return newFolder;
    }

    //----------------
    // Read          |
    //----------------
    @Override
    public Folder getFolder(String objectId) {
        return (Folder)session.getObject(objectId);
    }

    @Override
    public Document getDocument(String objectId) {
        Document doc = (Document)session.getObject(objectId);
        return doc;
    }

    @Override
    public Document getDocumentByPath(String pathToDocument) {
        IObjectID objectId = getObjectIdByPath(pathToDocument);
        Document doc = (Document)session.getObject(objectId.getLongID());
        return doc;
    }

    @Override
    public Rendition getDocumentThumbnail(Document doc) {
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("cmis:thumbnail");
        CmisObject obj = session.getObject(doc.getId(), oc);
        List<Rendition> rl = obj.getRenditions();

        if(rl!=null) {
            for (Rendition x : rl) {
                return x; // there should only be one: the thumbnail rendition
            }
        }
        return null;
    }

    @Override
    public String getAlfrescoThumbnailUrl(Document doc){
        //Form ticket call
        StringBuilder ticket = new StringBuilder();
        ticket.append("alf_ticket=" + this.getTicket());

        //get short version of the object iD
        String shortId = this.createShortIdFromLong(doc.getId());


        StringBuilder sb = new StringBuilder();
        sb.append(this.getBoxNameUrl() + "alfresco/service/api/node/workspace/SpacesStore/" + shortId + "/content/thumbnails/doclib?" + ticket + "&c=queue&ph=true");

        return sb.toString();

    }

    @Override
    public Boolean doesAlfrescoThumbnailExist(Document doc) {
        //EXTRACT mimetype
        String[] mimeTypeDivides = doc.getContentStreamMimeType().split("/");
        String mimeType = mimeTypeDivides[0];

        //System.out.println("Does thumbnail exist for " + mimeType + "?");
        if(mimeType.equals("image") || mimeType.equals("img") || mimeType.equals("pdf")){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public String getAlfrescoDocumentUrl(Document doc){
        //Form a ticket call
        StringBuilder ticket = new StringBuilder();
        ticket.append("alf_ticket="+ this.getTicket());

        //get short version of the objectId
        String shortId= this.createShortIdFromLong(doc.getId());

        //CHECK doc.getname() for spaces: if there are spaces in the name, they must be replaced with _ otherwise the doc wont load properly in the browser
        String docName = removeSpacesInName(doc);

        // Build the URL
        StringBuilder fullImage = new StringBuilder();
        fullImage.append(this.getBoxNameUrl() +"alfresco/s/api/node/content/workspace/SpacesStore/" + shortId + "/" + docName + "/?" +ticket);

        return fullImage.toString();
    }

    private String removeSpacesInName(Document doc) {
        StringBuilder name = new StringBuilder(doc.getName());
        for(int i=0; i < name.length();i++){
            if(name.charAt(i)==' '){
                name.deleteCharAt(i);
                name.insert(i,'_');
            }
        }
        return name.toString();
    }

    @Override
    public String getDocumentMetadataAll(Document doc) {
        List<Property<?>> l = doc.getProperties();
        StringBuilder sb = new StringBuilder();
        Iterator<Property<?>> i = l.iterator();
        while (i.hasNext()) {
            Property<?> p = i.next();
            Object value = p.getValue();
            PropertyType t = p.getType();

            switch (t) {
                case INTEGER:
                    BigInteger n = (BigInteger) value;
                    //System.out.println(p.getDisplayName() + " = " + n);
                    sb.append(p.getDisplayName() + " = " + n + "\n");
                    break;
                case STRING:
                    String s = (String) value;
                   // System.out.println(p.getDisplayName() + " = " + s);
                    sb.append(p.getDisplayName() + " = " + s + "\n");
                    break;
                default:
                    break;
            }
        }

        return sb.toString();
    }

    @Override
    public String getDocumentMetadataByProperty(ArrayList<Property> properties) {
        StringBuilder sb = new StringBuilder();
        for(Property x : properties){
            if(x != null) {
                sb.append(x.getDisplayName() + " : ");
                if(x.getValue()!= null){
                    sb.append( x.getValue().toString() + "\n");
                }
                else{
                    sb.append("null\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String getDocumentVersionHistory(Document doc) {

        List<Document> versions = doc.getAllVersions();
        StringBuilder sb = new StringBuilder();

        for(Document x : versions){
            sb.append("Name : "+ x.getName() +
                    "\n" + "Version Label : " + x.getVersionLabel() +
                    "\n" + "Content Stream :  " + x.getContentStream().toString()+
                    "\n" + "Description : " + x.getProperty("cm:description").getValue().toString());
        }

        return sb.toString();
    }

    @Override
    public ItemIterable<CmisObject> getFolderContents(Folder folder) {
        return folder.getChildren();
    }



    //----------------
    // Update        |
    //----------------
    @Override
    public IObjectID updateDocumentMetadataByProperty(Document doc, Map<String, Object> properties) {

        CmisObject obj = doc.updateProperties(properties);
        IObjectID iObjectID = new ObjectID(obj.getId());

        return iObjectID;
    }

    @Override
    public IObjectID updateDocumentContent(ContentStream contentStream, Document docToUpdate) {

        Document doc = docToUpdate.setContentStream(contentStream, true);
        //^^^^^^^^^^^ setContentStream() is returning null b/c "the repository did not provide an objectId"
        // so I'm returning the objectId of the ORIGINAL doc before any changes were made.
        // It appears that when content is updated on a document, it does not change the id in any way...
        IObjectID iObjectID = new ObjectID(docToUpdate.getId());

        return iObjectID;
    }

    @Override
    public IObjectID updateFolderName(String name, String folderId) {

        Folder folder = getFolder(folderId);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, name);

        Folder newFolder = (Folder)folder.updateProperties(properties);
        IObjectID iObjectID = new ObjectID(newFolder.getId());

        return iObjectID;
    }

    @Override
    public IObjectID updateDocumentName(String name, String docId){

        Document doc = getDocument(docId);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, name);

        Document newDoc = (Document) doc.updateProperties(properties);
        IObjectID iobjectID = new ObjectID(newDoc.getId());

        return iobjectID;

    }


    //----------------
    // Delete        |
    //----------------
    @Override
    public boolean deleteDocument(final String documentId) {
        Document doc = (Document)session.getObject(documentId);
        ObjectId id = new ObjectId() {
            @Override
            public String getId() {
                return documentId;
            }
        };
        try {
            session.delete(id);
        }catch (Exception e){
            return false;
        }
       return true;
    }

    @Override
    public boolean deleteFolder(String folderId) {
       Folder folder = getFolder(folderId);
       try {
           folder.delete();
       }catch(Exception e){
           folder.deleteTree(true, UnfileObject.DELETE, true);
           return true;
        }
       return true;
    }

    //---------------
    // Other        |
    //---------------


    @Override
    public ItemIterable<QueryResult> executeQuery(String queryString) {

        String objectType = extractObjectType(queryString);

//        System.out.println("EXECUTE QUERY: QUERY EXECUTED THERE WAS BLOOD EVERYWHERE");

        // get the query name of cmis:objectId
        ObjectType type = session.getTypeDefinition(objectType);
        PropertyDefinition<?> objectIdPropDef = type.getPropertyDefinitions().get(PropertyIds.OBJECT_ID);
        String objectIdQueryName = objectIdPropDef.getQueryName();


        //execute query
        ItemIterable<QueryResult> results = session.query(queryString, false);
        return results;
    }

    public String extractObjectType(String queryString) {
        String[] split1 = queryString.split("FROM ");
        String[] split2 = split1[1].split(" WHERE");

        return split2[0];
    }

    @Override
    public boolean downloadDocument(Document doc, String localDestination) {
        String filename = doc.getName();
        java.io.InputStream stream = doc.getContentStream().getStream();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(localDestination+filename));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = stream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            //System.out.println("Finished downloading");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
        return true;
    }


    @Override
    public boolean downloadFolderContent(String folderId, String localDestination) {
        boolean success = folderFunction(folderId, 20, 0, false, true, localDestination);
        return success;
    }


    //-----------------
    // Helper Methods |
    //-----------------

    @Override
    public String createShortIdFromLong(String longId){
        StringBuilder sb = new StringBuilder();

        //split on '/'
        String[] urlSections = longId.split("/");

        //store the last section of the url as the shortID
        String shortCodeWithVersion = urlSections[urlSections.length-1];
        String[] shortPlusVers = shortCodeWithVersion.split(";");

        String shortCodeWithoutVersion = shortPlusVers[0];

        String version;
        if(shortPlusVers.length >1) {
            version = shortPlusVers[1];
        }

        return shortCodeWithoutVersion;
    }

    @Override
    public IObjectID getObjectIdByPath(String path) {
        CmisObject object = session.getObjectByPath(path);
        String id = object.getId();
        IObjectID iod = new ObjectID(id);

        return iod;
    }

    public ArrayList<ObjectPathModel> getFolderContentsForFolderUpload (String folderPath){

        File file = new File(folderPath);
        File[] fileContents = file.listFiles();

        ArrayList<ObjectPathModel> filePaths = new ArrayList<ObjectPathModel>();
        if(file.isDirectory())
        {
            for(File x : fileContents){
                if(x.isFile()) {
                    Path path = FileSystems.getDefault().getPath(x.getPath());
                    ObjectPathModel opm = new ObjectPathModel();
                    opm.setObjectName(x.getName());
                    opm.setObjectPath(path);
                    opm.setObjectType("file");
                    filePaths.add(opm);

                }
                else if (x.isDirectory()){
                    Path path = FileSystems.getDefault().getPath(x.getPath());
                    ObjectPathModel opm = new ObjectPathModel();
                    opm.setObjectName(x.getName());
                    opm.setObjectPath(path);
                    opm.setObjectType("folder");
                    filePaths.add(opm);
                }
            }
        }
        return filePaths;
    }
    @Override
    public InputStream createDocumentInputStream(String newName, String filePath) {
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

        return input;
    }
    public ContentStream createDocumentContentStream(String newName, String filePath) {
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



    private void printDocumentMetadata(Document doc) {
        List<Property<?>> l = doc.getProperties();
        StringBuilder sb = new StringBuilder();
        Iterator<Property<?>> i = l.iterator();
        while (i.hasNext()) {
            Property<?> p = i.next();
            Object value = p.getValue();
            PropertyType t = p.getType();

            switch (t) {
                case INTEGER:
                    BigInteger n = (BigInteger) value;
                    System.out.println(p.getDisplayName() + " = " + n);
                    //sb.append(p.getDisplayName() + " = " + n + "\n");
                    break;
                case STRING:
                    String s = (String) value;
                    System.out.println(p.getDisplayName() + " = " + s);
                    //sb.append(p.getDisplayName() + " = " + s);
                    break;
                default:
                    break;
            }
        }
    }


    private boolean folderFunction(String folderId, int maxItemsPerPage, int skipCount, boolean print, boolean download, String localDestinationIfDownload) {
        CmisObject object = session.getObject(session.createObjectId(folderId));
        Folder folder = (Folder)object;

        OperationContext opContext = session.createOperationContext();
        opContext.setMaxItemsPerPage(maxItemsPerPage);

        ItemIterable<CmisObject> children = folder.getChildren(opContext);
        ItemIterable<CmisObject> page = children.skipTo(skipCount).getPage();

        Iterator<CmisObject> pageItems = page.iterator();

        boolean result= false;
        while(pageItems.hasNext()) {
            CmisObject item = pageItems.next();

            if(item.getBaseType().getId().toString().equals("cmis:document")) { // IE if the file retrieved is a document, then we process it.
            // If its a folder, we should probably write some recursive download function
                Document docItem = (Document) item;
                if (print) {
                    System.out.println("**********************************************");
                    printDocumentMetadata(docItem);
                    result=true;
                }
                if (download) {
                    result = downloadDocument(docItem, localDestinationIfDownload);
                }
            }
        }
        return result;
    }
}
