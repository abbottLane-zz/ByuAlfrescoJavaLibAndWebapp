package edu.byu.oit.core.cmis.tests.CMISAbstract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OldAlfrescoSessionTests {

//    CMISSessionInterface session;
//
//    @Before
//    public void initialize(){
//
//        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
//        session = (CMISSessionInterface)context.getBean("test");
//        session.setCredentials("admin", "Iamb0b123");
//        session.startSession();
//
//
//        //upload all test files to server prior to test
//        //Owen.jpg
//        IObjectID folderId= session.getObjectIdByPath("/User Homes/abbott/");
//        ContentStream cs = session.createDocument("Owen.jpg", "./additionalResources/mainFolderItems/Owen.jpg");
//        Document uploadedDoc = session.uploadDocument(folderId.toString(), "Owen.jpg", cs, null, "1", "This is a picture of Owen, 5 months old");
//
//        //rename folder
//        session.uploadFolder(folderId.toString(), "rename", "./additionalResources/mainFolderItems/rename/");
//
//        //pythondoc
//        ContentStream cspyth = session.createDocument("BeginningPythonForNathan.docx", "./additionalResources/mainFolderItems/BeginningPythonForNathan.docx");
//        Document uploadpythdoc = session.uploadDocument(folderId.toString(), "BeginningPythonForNathan.docx", cspyth, null, "1", null );
//
//        //newImage.jpg
//        ContentStream csNewImage = session.createDocument("newImage3.jpg", "./additionalResources/mainFolderItems/newImage3.jpg");
//        Document uploadnewimage = session.uploadDocument(folderId.toString(), "newImage3.jpg", csNewImage, null, "1", "Description: its a photo");
//    }
//
//    @After
//    public void teardown(){
//        //delete everything in the /User Homes/abbott folder
//
//        IObjectID folderId = session.getObjectIdByPath("/User Homes/abbott/");
//        Folder folder = session.getFolder(folderId.toString());
//
//        IObjectID obj1 = session.getObjectIdByPath("/User Homes/abbott/Owen.jpg");
//        session.deleteDocument(obj1.toString());
//
//        IObjectID obj2= session.getObjectIdByPath("/User Homes/abbott/BeginningPythonForNathan.docx");
//        session.deleteDocument(obj2.toString());
//
//        IObjectID obj3 = session.getObjectIdByPath("/User Homes/abbott/newImage3.jpg");
//        session.deleteDocument(obj3.toString());
//
//        IObjectID obj4 = session.getObjectIdByPath("/User Homes/abbott/rename/");
//        session.deleteFolder(obj4.toString());
//    }
////--------------------------- TESTS ---------------------------------------------
//    @Test
//    public void testUploadAndDeleteDocument() throws Exception {
//        IObjectID folderId= session.getObjectIdByPath("/User Homes/abbott/");
//        ContentStream cs = session.createDocument("OwenAtTheBeach.jpg", "./additionalResources/testImages/OwenAtTheBeach.jpg");
//        Document uploadedDoc = session.uploadDocument(folderId.getLongID(), "newImage.jpg", cs, null, "1", "Description: its a photo");
//
//        assertEquals("Description: its a photo", uploadedDoc.getProperty("cm:description").getValueAsString());
//        assertEquals("image/jpeg", uploadedDoc.getContentStreamMimeType());
//
//        //delete the uploaded doc from the server afterwards, otherwise the next run of this test will fail
//        session.deleteDocument(uploadedDoc.getId());
//
//    }
//
//    @Test
//    public void testUploadFolder() throws Exception {
//
//        //upload a folder
//        IObjectID folderId= session.getObjectIdByPath("/User Homes/abbott/");
//        Folder uploadedFolder = session.uploadFolder(folderId.getLongID(),"newFolder","./additionalResources/testFolder" );
//
//        //test with asserts
//        ItemIterable<CmisObject> itemsInFolder = session.getFolderContents(uploadedFolder);
//
//        int i =0;
//        for(CmisObject obj : itemsInFolder){
//            if(i == 0){assertEquals("package-list", obj.getName());}
//            else if (i == 1){assertEquals("notes.txt", obj.getName());}
//            else if (i == 2){assertEquals("CMISCRUD", obj.getName());}
//            else if (i == 4){assertEquals("pinky11boxNotes.odt", obj.getName());}
//            else if (i == 3){assertEquals("subFolder", obj.getName());}
//            i++;
//        }
//
//        //delete uploaded Folder
//        session.deleteFolder(uploadedFolder.getId());
//    }
//
//    @Test
//    public void testGetFolder() throws Exception {
//        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott/");
//        Folder folder = session.getFolder(objectId.getLongID());
//
//        ItemIterable<CmisObject> children = folder.getChildren();
//
//        assertEquals(4, children.getTotalNumItems());
//        assertEquals("abbott", folder.getName());
//    }
//
//    @Test
//    public void testGetDocument() throws Exception {
//        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott/Owen.jpg");
//        Document doc = session.getDocument(objectId.getLongID());
//
//        assertEquals("Owen.jpg", doc.getName());
//        assertEquals("This is a picture of Owen, 5 months old", doc.getProperty("cm:description").getValueAsString());
//    }
//
//    @Test
//    public void testGetDocumentByPath() throws Exception {
//        Document doc = session.getDocumentByPath("/User Homes/abbott/Owen.jpg");
//        assertEquals("Owen.jpg", doc.getName());
//    }
//
//    @Test
//    public void testGetDocumentThumbnail() throws Exception {
//        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott/Owen.jpg");
//        Document doc = session.getDocument(objectId.getLongID());
//
//        Rendition thumbnail = session.getDocumentThumbnail(doc);
//
//        if(thumbnail != null) {
//            assertEquals(100, thumbnail.getHeight());
//            assertEquals(100, thumbnail.getWidth());
//            assertEquals("image/png", thumbnail.getMimeType());
//            assertEquals("7d8eb170-54d8-438a-8a35-7652174b1732", thumbnail.getRenditionDocumentId());
//        }
//    }
//
//    @Test
//    public void testGetDocumentMetadataAll() throws Exception {
//        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott/Owen.jpg");
//        Document doc = session.getDocument(objectId.getLongID());
//
//
//        String docMetadata = session.getDocumentMetadataAll(doc);
//
//        assertEquals("Content Stream Length = 853144\n" +
//                "Version Series Checked Out By = null\n" +
//                "Name = Owen.jpg\n" +
//                "Content Stream MIME Type = image/jpeg\n" +
//                "Change token = null\n" +
//                "Version Label = 0.1\n" +
//                "Last Modified By = admin\n" +
//                "Created by = admin\n" +
//                "Checkin Comment = Initial Version\n" +
//                "Content Stream Filename = Owen.jpg\n" +
//                "Description = This is a picture of Owen, 5 months old\n" +
//                "Policy Text = null\n" +
//                "Title = null\n" , docMetadata);
//    }
//
//    @Test
//    public void testGetDocumentMetadataByProperty() throws Exception {
//        Document doc = session.getDocumentByPath("/User Homes/abbott/Owen.jpg");
//
//        Property p1 = doc.getProperty("cm:description");
//        Property p2 = doc.getProperty("cmis:name");
//        Property p3 = doc.getProperty("cmis:createdBy");
//        Property p4 = doc.getProperty("cm:author");
//
//        ArrayList<Property> properties = new ArrayList<Property>();
//        properties.add(p1);
//        properties.add(p2);
//        properties.add(p3);
//        properties.add(p4);
//
//        String propertyValues = session.getDocumentMetadataByProperty(properties);
//
//        System.out.println(propertyValues);
//
//        assertEquals("Description : This is a picture of Owen, 5 months old\n" +
//                "Name : Owen.jpg\n" +
//                "Created by : admin\n"
//               , propertyValues);
//    }
//
//    @Test
//    public void testGetDocumentVersionHistory() throws Exception {
//        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott/Owen.jpg");
//        Document doc = session.getDocument(objectId.getLongID());
//
//        String versionHistory = session.getDocumentVersionHistory(doc);
//
//
//        assertEquals("Name : Owen.jpg\n" +
//                "Version Label : 0.1\n" +
//                "Content Stream :  ContentStream [filename=Owen.jpg, length=null, MIME type=image/jpeg, has stream=true][extensions=null]\n" +
//                "Description : This is a picture of Owen, 5 months old", versionHistory);
//    }
//
//    @Test
//    public void testGetFolderContents() throws Exception {
//        IObjectID folderId= session.getObjectIdByPath("/User Homes/abbott/");
//        Folder folder = session.getFolder(folderId.getLongID());
//        ItemIterable<CmisObject> children = session.getFolderContents(folder);
//
//        assertEquals(4, children.getTotalNumItems());
//
//        int i =0;
//        for(CmisObject obj : children){
//            if(i == 0){assertEquals("Owen.jpg", obj.getName());}
//            else if (i == 1){assertEquals("rename", obj.getName());}
//            i++;
//        }
//    }
//
//    @Test
//    public void testUpdateDocumentMetadataByProperty() throws Exception {
//        Document doc = session.getDocumentByPath("/User Homes/abbott/Owen.jpg");
//
//        //Print a list of editable properties, for reference
//        List properties = doc.getProperties();
//        for(int i = 0; i < properties.size(); i ++){
//            Property current = (Property)properties.get(i);
//            System.out.println("["+current.getId()+"] : " + current.getValueAsString());
//        }
//
//        //change properties
//        Map<String, Object> props = new HashMap<String, Object>();
//        props.put("cmis:name", "testing.jpg");
//       // props.put("cm:author", "Vaughan Williams");
//        props.put("cm:description", "this is a new description, for testing");
//        IObjectID oid = session.updateDocumentMetadataByProperty(doc, props);
//
//
//        //run asserts
//        assertEquals("[testing.jpg]", doc.getProperty("cmis:name").getValuesAsString());
//       // assertEquals("Vaughan Williams", doc.getProperty("cm:author").getValueAsString());
//        assertEquals("this is a new description, for testing", doc.getProperty("cm:description").getValueAsString());
//
//        //change properties back to original so we don't fail a whole host of other tests
//        props.put("cmis:name", "Owen.jpg");
//        props.put("cmis:createdBy", "admin");
//        //props.put("cm:author", null);
//        props.put("cm:description", "This is a picture of Owen, 5 months old");
//        oid = session.updateDocumentMetadataByProperty(doc, props);
//        //System.out.println("---=== Update returns LongOID : " + oid.getLongID() + " ===--- \n---=== and short OID : " + oid.getShortID()+ " ===---");
//
//        //run asserts again to make sure we got switched back
//        assertEquals("[Owen.jpg]", doc.getProperty("cmis:name").getValuesAsString());
//        assertEquals("admin", doc.getProperty("cmis:createdBy").getValueAsString());
//        //assertEquals(null, doc.getProperty("cm:author").getValueAsString());
//        assertEquals("This is a picture of Owen, 5 months old", doc.getProperty("cm:description").getValueAsString());
//    }
//
//    @Test
//    public void testUpdateDocumentContent() throws Exception {
//        //upload an image
//        IObjectID oid = session.getObjectIdByPath("/User Homes/abbott/");
//        Folder folder = session.getFolder(oid.getLongID());
//        ContentStream contentStream = session.createDocument("newTestImage.jpg", "./additionalResources/testImages/testImage1.jpg");
//        Document doc = session.uploadDocument(folder.getId(), "newImageTest.jpg", contentStream, "cmis:document", "1", "a new image");
//
//        //take doc, and change its content stream
//        ContentStream contentStream2 = session.createDocument("newContent.jpg", "./additionalResources/testImages/Owen.jpg");
//        IObjectID objectId = session.updateDocumentContent(contentStream2, doc);
//
//        //delete the test image who's content we switched
//        Document document = session.getDocument(objectId.getLongID());
//        session.deleteDocument(document.getId());
//
//    }
//
//    @Test
//    public void testUpdateFolderName() throws Exception {
//        IObjectID oid = session.getObjectIdByPath("/User Homes/abbott/rename");
//        Folder folder = session.getFolder(oid.getLongID());
//
//        IObjectID renamedFolderId = session.updateFolderName("BAM", folder.getId());
//        Folder newFolder = session.getFolder(renamedFolderId.getLongID());
//
//        assertEquals("BAM", newFolder.getName());
//
//        //change it back so this test runs correctly nex time
//        IObjectID renamedFolderId2 = session.updateFolderName("rename", newFolder.getId());
//        Folder newFolder2 = session.getFolder(renamedFolderId2.getLongID());
//
//        assertEquals("rename", newFolder.getName());
//
//    }
//
//    @Test
//    public void testCreateAndDeleteFolder() throws Exception {
//        session.createFolder("/User Homes/abbott", "newFolder");
//        IObjectID folderId = session.getObjectIdByPath("/User Homes/abbott/newFolder");
//        Folder folder = session.getFolder(folderId.getLongID());
//
//        assertEquals("newFolder", folder.getName());
//        assertEquals(0, folder.getChildren().getTotalNumItems());
//
//
//        session.deleteFolder(folderId.getLongID());
//    }
//
//    @Test
//    public void testGetTicket() throws Exception {
//        String ticket = session.getTicket();
//        System.out.println("This is the ticket: "+ticket);
//        assertNotNull(ticket);
//    }
//
//    @Test
//    public void testExecuteQuery() throws Exception {
//
//        String queryString = "SELECT * FROM cmis:document WHERE cmis:name LIKE 'Owen.jpg' ";
//        // String queryString = "SELECT * FROM cmis:document WHERE cmis:name LIKE '%12-3123.%'";
//        ItemIterable<QueryResult> queryResult2 = session.executeQuery(queryString);
//
//        for (QueryResult item : queryResult2) {
//            System.out.println("Datei: " + item.getPropertyByQueryName("cmis:name").getFirstValue() + " createdBy "
//                    + item.getPropertyByQueryName("cmis:createdBy").getFirstValue());
//
//        }
//    }
//
//    @Test
//    public void testDownloadDocument() throws Exception {
//        ///Download a file from the repo by path
//        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott/Owen.jpg");
//        Document doc = session.getDocument(objectId.getLongID());
//        String destinationFolder = "./additionalResources/testImages/";
//
//        assertEquals(true, session.downloadDocument(doc, destinationFolder));
//        assertEquals(false, session.downloadDocument(doc, "invalid/Destination"));
//    }
//
//    @Test
//    public void testDownloadFolderContent() throws Exception {
//
//        IObjectID objectId = session.getObjectIdByPath("/User Homes/abbott");
//        Folder folder = session.getFolder(objectId.getLongID());
//
//        session.downloadFolderContent(folder.getId(), "./additionalResources/contentDownloads/");
//    }
//
//    @Test
//    public void testGetObjectIdByPath() throws Exception {
//        IObjectID owenOID = session.getObjectIdByPath("/User Homes/abbott/Owen.jpg");
//        IObjectID newImageOID = session.getObjectIdByPath("/User Homes/abbott/newImage3.jpg");
//        IObjectID beginningPython = session.getObjectIdByPath("/User Homes/abbott/BeginningPythonForNathan.docx");
//
//        //These are obsolete, since the initialize() and teardown() replace the documents between every test, these ids will be different every time
////        assertEquals("workspace://SpacesStore/6ac8124b-dc3e-4dd7-a5a9-a1f62f1e06c6;0.1" , owenOID.getLongID());
////        assertEquals("workspace://SpacesStore/fe131524-94cf-479e-bef2-430865aab373;0.1", newImageOID.getLongID());
////        assertEquals("workspace://SpacesStore/80cf0772-e428-4334-9193-7c2fef2ff1ae;1.0", beginningPython.getLongID());
//        if (owenOID != null && newImageOID != null && beginningPython != null) {
//            assertEquals(true, true);
//        }
//        else{
//            System.out.println("testGetObjectIdByPath() is failing to retrieve/create a valid objectID");
//            assertEquals(false, true);
//        }
//    }
//
//    @Test
//    public void testCreateDocument() throws Exception {
//        ContentStream cs =session.createDocument("newFile.txt", "./additionalResources/testFolder/notes.txt" );
//
//        assertEquals("text/plain", cs.getMimeType());
//        assertEquals("newFile.txt", cs.getFileName());
//    }
//
//
//    @Test
//    public void testExtractObjectType(){
//        AbstractCMISSession sess = (AbstractCMISSession)session;
//        String objType = sess.extractObjectType("SELECT * FROM cmis:document WHERE cmis:name LIKE '%.pdf'");
//
//        assertEquals("cmis:document", objType);
//    }
}