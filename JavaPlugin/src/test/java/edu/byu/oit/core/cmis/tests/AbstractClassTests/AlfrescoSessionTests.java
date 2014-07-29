package edu.byu.oit.core.cmis.tests.AbstractClassTests;

import edu.byu.oit.core.cmis.CMISAbstract.AbstractCMISSession;
import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import edu.byu.oit.core.cmis.CMISInterface.IObjectID;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



public class AlfrescoSessionTests {

    CMISSessionInterface session;

    @Before
    public void initialize(){

        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        session = (CMISSessionInterface)context.getBean("test");
        session.setCredentials("admin", "Iamb0b123");
        session.startSession();

        //DELETE anything existing in test folder
        IObjectID folderId = session.getObjectIdByPath("/User Homes/abbott/test/");
        Folder folder = session.getFolder(folderId.toString());
        ItemIterable<CmisObject> items = session.getFolderContents(folder);

        for(CmisObject obj : items){
            System.out.println(obj.getBaseType().toString());
            if(obj.getBaseType().getId().equals("cmis:document")){
                Document doc = (Document)obj;
                session.deleteDocument(doc.getId());
            }
            else if (obj.getBaseType().getId().equals("cmis:folder")){
                Folder fol = (Folder)obj;
                session.deleteFolder(fol.getId());
            }
        }
    }


    //--------------------------- TESTS ---------------------------------------------
    @Test
    public void testUploadAndDeleteDocument() throws Exception {

        //UPLOAD image
        IObjectID rootFolderId = session.getObjectIdByPath("/User Homes/abbott/test/");
        ContentStream cs = session.createDocument("owen.jpg", "./additionalResources/testImages/Owen.jpg");
        session.uploadDocument(rootFolderId.toString(),"owenPic.jpg",cs,null,"1.0","this is a picture of owen");

        //VERIFY that the upload completed successfully
        IObjectID imgId = session.getObjectIdByPath("/User Homes/abbott/test/owenPic.jpg");
        Document doc = session.getDocument(imgId.toString());
        assertEquals("owenPic.jpg",doc.getName());
        assertEquals(853144, doc.getContentStreamLength());

        //DELETE the image
        session.deleteDocument(doc.getId());

    }

    @Test
    public void testUploadFolder() throws Exception {


        //UPLOAD test folder
        IObjectID rootFolderId = session.getObjectIdByPath("/User Homes/abbott/test");
        Folder folder = session.uploadFolder(rootFolderId.toString(), "temp", "./additionalResources/testFolder/");

        //VERIFY the upload
        assertEquals("temp",folder.getName());
        assertEquals("/User Homes/abbott/test/temp",folder.getPath());
        assertEquals(5,folder.getChildren().getTotalNumItems());

        //DELETE the upload
        session.deleteFolder(session.getObjectIdByPath("/User Homes/abbott/test/temp/").toString());
    }

    @Test
    public void testGetFolder() throws Exception {


        //UPLOAD test folder
        IObjectID rootFolderId = session.getObjectIdByPath("/User Homes/abbott/test/");
        Folder folder = session.uploadFolder(rootFolderId.toString(), "temp", "./additionalResources/testFolder/");

        //GET the folder
        IObjectID id = session.getObjectIdByPath("/User Homes/abbott/test/temp");
        Folder gottenFolderById = session.getFolder(id.toString());

        //VERIFY the folder's content
        assertEquals("temp",gottenFolderById.getName());
        assertEquals("/User Homes/abbott/test/temp",folder.getPath());
        assertEquals(5,folder.getChildren().getTotalNumItems());

        //DELETE the folder
        session.deleteFolder(session.getObjectIdByPath("/User Homes/abbott/test/temp/").toString());

    }

    @Test
    public void testGetDocument() throws Exception {


        //UPLOAD the document
        IObjectID rootFolderID= session.getObjectIdByPath("/User Homes/abbott/test/");
        ContentStream cs = session.createDocument("image1.jpg", "./additionalResources/testImages/testImage1.jpg");
        Document uploadedDoc = session.uploadDocument(rootFolderID.toString(), "testImage.jpg", cs, null, "1.0","Just another test image");

        //GET the document
        Document doc = session.getDocumentByPath("/User Homes/abbott/test/testImage.jpg");

        //VERIFY the document
        assertEquals("testImage.jpg", doc.getName());
        assertEquals("Just another test image", doc.getProperty("cm:description").getValueAsString());

        //DELETE the document
        session.deleteDocument(doc.getId());


    }

    @Test
    public void testGetDocumentThumbnail() throws Exception {


        //UPLOAD a document
        IObjectID rootFolderID= session.getObjectIdByPath("/User Homes/abbott/test/");
        ContentStream cs = session.createDocument("image1.jpg", "./additionalResources/testImages/testImage1.jpg");
        Document uploadedDoc = session.uploadDocument(rootFolderID.toString(), "testImage2.jpg", cs, null, "1.0","Just another test image");

        //GET the thumbnail
        String thumbUrl = session.getAlfrescoThumbnailUrl(uploadedDoc);

        //VERIFY the thumbnail
            //Impossible to verify the url with an assertEquals() method, because the id and ticket change every time this method is called.
                //But it currently WORKS
                System.out.println(thumbUrl);

        //DELETE the document
        session.deleteDocument(uploadedDoc.getId());
    }

    @Test
    public void testGetAlfrescoDocumentUrl() throws Exception{
        //UPLOAD a document
        IObjectID rootFolderID= session.getObjectIdByPath("/User Homes/abbott/test/");
        ContentStream cs = session.createDocument("image1.jpg", "./additionalResources/testImages/testImage1.jpg");
        Document uploadedDoc = session.uploadDocument(rootFolderID.toString(), "testImage2.jpg", cs, null, "1.0","Just another test image");

        //GET the thumbnail
        String thumbUrl = session.getAlfrescoDocumentUrl(uploadedDoc);

        //VERIFY the thumbnail
        //Impossible to verify the url with an assertEquals() method, because the id and ticket change every time this method is called.
        //But it currently WORKS
        System.out.println(thumbUrl);

        //DELETE the document
        session.deleteDocument(uploadedDoc.getId());
    }

    @Test
    public void testGetDocumentMetadataAll() throws Exception {


        //UPLOAD a document
        IObjectID rootFolderID= session.getObjectIdByPath("/User Homes/abbott/test/");
        ContentStream cs = session.createDocument("image1.jpg", "./additionalResources/testImages/testImage1.jpg");
        Document uploadedDoc = session.uploadDocument(rootFolderID.toString(), "testImage2.jpg", cs, null, "1.0","Just another test image");

        //GET document metadata(all)
        String allMetadata = session.getDocumentMetadataAll(uploadedDoc);

        //VERIFY metadata
        assertEquals("Content Stream Length = 10735\n" +
                "Version Series Checked Out By = null\n" +
                "Version Label = 0.1\n" +
                "Last Modified By = admin\n" +
                "Created by = admin\n" +
                "Name = testImage2.jpg\n" +
                "Content Stream MIME Type = image/jpeg\n" +
                "Change token = null\n" +
                "Checkin Comment = Initial Version\n" +
                "Description = null\n" +
                "Content Stream Filename = testImage2.jpg\n" +
                "Image Width = 222\n" +
                "Policy Text = null\n" +
                "Camera Software = null\n" +
                "Camera Model = null\n" +
                "ISO Speed = null\n" +
                "Orientation = null\n" +
                "Resolution Unit = null\n" +
                "Image Height = 227\n" +
                "Camera Manufacturer = null\n" +
                "Description = Just another test image\n" +
                "Title = null\n" +
                "Author = null\n", allMetadata);

        //DELETE the document
        session.deleteDocument(uploadedDoc.getId());
    }

    @Test
    public void testGetDocumentMetadataByProperty() throws Exception {


        //UPLOAD a document
        IObjectID rootFolderID= session.getObjectIdByPath("/User Homes/abbott/test/");
        ContentStream cs = session.createDocument("image1.jpg", "./additionalResources/testImages/Owen.jpg");
        Document uploadedDoc = session.uploadDocument(rootFolderID.toString(), "Owen.jpg", cs, null, "1.0","Just another test image");

        //GET metadata properties
        Document doc = session.getDocumentByPath("/User Homes/abbott/test/Owen.jpg");

        Property p1 = doc.getProperty("cm:description");
        Property p2 = doc.getProperty("cmis:name");
        Property p3 = doc.getProperty("cmis:createdBy");
        Property p4 = doc.getProperty("cm:author");

        ArrayList<Property> properties = new ArrayList<Property>();
        properties.add(p1);
        properties.add(p2);
        properties.add(p3);
        properties.add(p4);

        String propertyValues = session.getDocumentMetadataByProperty(properties);

        //VERIFY metadata properties
        assertEquals("Description : Just another test image\n" +
                "Name : Owen.jpg\n" +
                "Created by : admin\n" +
                "Author : null\n"
                , propertyValues);

        //DELETE document
        session.deleteDocument(uploadedDoc.getId());
    }

    @Test
    public void testGetDocumentVersionHistory() throws Exception {


        //UPLOAD Document
        IObjectID rootFolderID= session.getObjectIdByPath("/User Homes/abbott/test/");
        ContentStream cs = session.createDocument("image1.jpg", "./additionalResources/testImages/Owen.jpg");
        Document uploadedDoc = session.uploadDocument(rootFolderID.toString(), "Owen.jpg", cs, null, "1.0","Just another test image");

        //GET version history
        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott/test/Owen.jpg");
        Document doc = session.getDocument(objectId.getLongID());

        String versionHistory = session.getDocumentVersionHistory(doc);

        //VERIFY version data
        assertEquals("Name : Owen.jpg\n" +
                "Version Label : 0.1\n" +
                "Content Stream :  ContentStream [filename=Owen.jpg, length=null, MIME type=image/jpeg, has stream=true][extensions=null]\n" +
                "Description : Just another test image", versionHistory);

        //DELETE document
        session.deleteDocument(uploadedDoc.getId());
    }

    @Test
    public void testGetFolderContents() throws Exception {


        //UPLOAD folder
        IObjectID rootFolderId = session.getObjectIdByPath("/User Homes/abbott/test");
        Folder folder = session.uploadFolder(rootFolderId.toString(), "temp", "./additionalResources/testFolder/");

        //GET folder contents
        ItemIterable<CmisObject> items = session.getFolderContents(folder);

        //VERIFY folder contents
        assertEquals(5,items.getTotalNumItems());

        //DELETE folder
        session.deleteFolder(folder.getId());

    }

    @Test
    public void testUpdateDocumentMetadataByProperty() throws Exception {


        //UPLOAD document
        IObjectID rootFolderID= session.getObjectIdByPath("/User Homes/abbott/test/");
        ContentStream cs = session.createDocument("image1.jpg", "./additionalResources/testImages/Owen.jpg");
        Document doc = session.uploadDocument(rootFolderID.toString(), "Owen.jpg", cs, null, "1.0","Just another test image");

        //UPDATE metadata
        //Print a list of editable properties, for reference
        List properties = doc.getProperties();
        for(int i = 0; i < properties.size(); i ++){
            Property current = (Property)properties.get(i);
            System.out.println("["+current.getId()+"] : " + current.getValueAsString());
        }

        //change properties
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("cmis:name", "testing.jpg");
        props.put("cm:author", "Vaughan Williams");
        props.put("cm:description", "this is a new description, for testing");
        IObjectID oid = session.updateDocumentMetadataByProperty(doc, props);

        //VERIFY metadata changes
        //run asserts
        assertEquals("[testing.jpg]", doc.getProperty("cmis:name").getValuesAsString());
        assertEquals("Vaughan Williams", doc.getProperty("cm:author").getValueAsString());
        assertEquals("this is a new description, for testing", doc.getProperty("cm:description").getValueAsString());

        //change properties back to original so we don't fail a whole host of other tests
        props.put("cmis:name", "Owen.jpg");
        props.put("cmis:createdBy", "admin");
        props.put("cm:author", null);
        props.put("cm:description", "This is a picture of Owen, 5 months old");
        oid = session.updateDocumentMetadataByProperty(doc, props);
        //System.out.println("---=== Update returns LongOID : " + oid.getLongID() + " ===--- \n---=== and short OID : " + oid.getShortID()+ " ===---");

        //run asserts again to make sure we got switched back
        assertEquals("[Owen.jpg]", doc.getProperty("cmis:name").getValuesAsString());
        assertEquals("admin", doc.getProperty("cmis:createdBy").getValueAsString());
        assertEquals(null, doc.getProperty("cm:author").getValueAsString());
        assertEquals("This is a picture of Owen, 5 months old", doc.getProperty("cm:description").getValueAsString());
        assertEquals("NX2000", doc.getProperty("exif:model").getValueAsString());

        //DELETE document
        session.deleteDocument(doc.getId());
    }

    @Test
    public void testUpdateDocumentContent() throws Exception {


        //UPLOAD a Document
        IObjectID oid = session.getObjectIdByPath("/User Homes/abbott/test/");
        Folder folder = session.getFolder(oid.getLongID());
        ContentStream contentStream = session.createDocument("newTestImage.jpg", "./additionalResources/testImages/testImage1.jpg");
        Document doc = session.uploadDocument(folder.getId(), "newImageTest.jpg", contentStream, "cmis:document", "1", "a new image");

        //UPDATE document content
        ContentStream contentStream2 = session.createDocument("newContent.jpg", "./additionalResources/testImages/Owen.jpg");
        Document retrievedDoc = session.getDocument(doc.getId());
        IObjectID objectId = session.updateDocumentContent(contentStream2, retrievedDoc);

        //VERIFY update of content
        Document doc1 = session.getDocument(objectId.toString());
        assertEquals("newImageTest.jpg",doc1.getName());

        //DELETE document
        session.deleteDocument(doc1.getId());
    }

    @Test
    public void testUpdateFolderName() throws Exception {


        //UPLOAD a folder
        IObjectID folderId = session.getObjectIdByPath("/User Homes/abbott/test/");
        Folder folder = session.uploadFolder(folderId.toString(), "rename", "/additionalResources/testFolder/");

        //UPDATE the folder name
        IObjectID oid = session.getObjectIdByPath("/User Homes/abbott/test/rename");
        Folder folder2 = session.getFolder(oid.toString());

        IObjectID renamedFolderId = session.updateFolderName("BAM", folder2.getId());
        Folder newFolder = session.getFolder(renamedFolderId.getLongID());

        //VERIFY the update
        assertEquals("BAM", newFolder.getName());

        //DELETE the folder
        session.deleteFolder(newFolder.getId());
    }

    @Test
    public void testCreateAndDeleteFolder() throws Exception {


        //CREATE folder
        session.createFolder("/User Homes/abbott/test/", "testFolderCreation");

        //VERIFY folder's existence
        IObjectID folderId = session.getObjectIdByPath("/User Homes/abbott/test/testFolderCreation/");
        Folder folder = session.getFolder(folderId.toString());
        assertEquals("testFolderCreation",folder.getName());

        //DELETE folder
        session.deleteFolder(folder.getId());
    }

    @Test
    public void testGetTicket() throws Exception {


        String ticket = session.getTicket();
        System.out.println("The ticket that we done got: "+ticket);
        //assertEquals("TICKET_5deb0841d204fbd9937b4c98e7de0be28c7d4b90", ticket);
        assertNotNull(ticket);
    }

    @Test
    public void testExecuteQuery() throws Exception {


        String queryString = "SELECT * FROM cmis:document WHERE cmis:name LIKE 'Owen.jpg' ";
        // String queryString = "SELECT * FROM cmis:document WHERE cmis:name LIKE '%12-3123.%'";
        ItemIterable<QueryResult> queryResult2 = session.executeQuery(queryString);

        for (QueryResult item : queryResult2) {
            System.out.println("Date: " + item.getPropertyByQueryName("cmis:name").getFirstValue() + " createdBy "
                    + item.getPropertyByQueryName("cmis:createdBy").getFirstValue());
        }
    }

    @Test
    public void testDownloadDocument() throws Exception {


        //UPLOAD a file
        IObjectID oid = session.getObjectIdByPath("/User Homes/abbott/test/");
        Folder folder = session.getFolder(oid.getLongID());
        ContentStream contentStream = session.createDocument("newTestImage.jpg", "./additionalResources/testImages/testImage1.jpg");
        Document doc = session.uploadDocument(folder.getId(), "newImageTest.jpg", contentStream, "cmis:document", "1", "a new image");


        ///DOWNLOAD the file from the repo by path
        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott/test/newImageTest.jpg");
        Document doc2 = session.getDocument(objectId.getLongID());
        String destinationFolder = "./additionalResources/testImages/";

        assertEquals(true, session.downloadDocument(doc2, destinationFolder));
        assertEquals(false, session.downloadDocument(doc2, "invalid/Destination"));


    }

    @Test
    public void testDownloadFolderContent() throws Exception {


        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott");
        Folder folder = session.getFolder(objectId.getLongID());

        session.downloadFolderContent(folder.getId(), "./additionalResources/contentDownloads/");
    }


    @Test
    public void testCreateDocument() throws Exception {


        ContentStream cs =session.createDocument("newFile.txt", "./additionalResources/testFolder/notes.txt" );

        assertEquals("text/plain", cs.getMimeType());
        assertEquals("newFile.txt", cs.getFileName());
    }


    @Test
    public void testExtractObjectType(){


        AbstractCMISSession sess = (AbstractCMISSession)session;
        String objType = sess.extractObjectType("SELECT * FROM cmis:document WHERE cmis:name LIKE '%.pdf'");

        assertEquals("cmis:document", objType);
    }

    @Test
    public void testGetBoxNameUrl(){
        String boxname = session.getBoxNameUrl();
        assertEquals("http://brainiac:8080/", boxname);
    }

    @Test
    public void testCreateShortIdFromLong(){
        IObjectID id = session.getObjectIdByPath("/User Homes/abbott/test/");
        String shortId = session.createShortIdFromLong(id.getLongID());

        System.out.println(shortId);
        assertEquals("09ab574b-ce4f-4e0c-8a49-b4e11952bbf6", shortId);
    }

    @Test public void getAlfrescoThumbnail(){
        Document doc = session.getDocumentByPath("/User Homes/abbott/kanaky.jpg");
        String thumbnailUrl = session.getAlfrescoThumbnailUrl(doc);

        assertNotNull(thumbnailUrl);
        System.out.println(thumbnailUrl);
    }
}