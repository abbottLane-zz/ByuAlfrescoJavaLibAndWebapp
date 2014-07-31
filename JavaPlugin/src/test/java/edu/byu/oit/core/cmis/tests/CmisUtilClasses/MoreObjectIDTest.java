package edu.byu.oit.core.cmis.tests.CmisUtilClasses;

import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import edu.byu.oit.core.cmis.CMISInterface.IObjectID;
import edu.byu.oit.core.cmis.CmisUtilClasses.ObjectID;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;

public class MoreObjectIDTest {

    CMISSessionInterface session;
    IObjectID iod;
    ObjectID id;

    @Before
    public void initialize(){

        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        session = (CMISSessionInterface)context.getBean("test");
        session.setCredentials("admin", "Iamb0b123");
        session.startSession();


        //upload all test files to server prior to test
        //Owen.jpg
        IObjectID folderId= session.getObjectIdByPath("/User Homes/abbott/test/");
        //ContentStream cs = session.createDocument("Owen.jpg", "./additionalResources/mainFolderItems/Owen.jpg");
        Document uploadedDoc = session.uploadDocument(folderId.toString(), "Owen.jpg","./additionalResources/mainFolderItems/Owen.jpg" , null, "1", "This is a picture of Owen, 5 months old");

        //rename folder
        session.uploadFolder(folderId.toString(), "rename", "./additionalResources/mainFolderItems/rename/");

        //pythondoc
        //ContentStream cspyth = session.createDocument("BeginningPythonForNathan.docx", "./additionalResources/mainFolderItems/BeginningPythonForNathan.docx");
        Document uploadpythdoc = session.uploadDocument(folderId.toString(), "BeginningPythonForNathan.docx", "./additionalResources/mainFolderItems/BeginningPythonForNathan.docx", null, "1", null );

        //newImage.jpg
        //ContentStream csNewImage = session.createDocument("newImage3.jpg", "./additionalResources/mainFolderItems/newImage3.jpg");
        Document uploadnewimage = session.uploadDocument(folderId.toString(), "newImage3.jpg", "./additionalResources/mainFolderItems/newImage3.jpg", null, "1", "Description: its a photo");

        iod = session.getObjectIdByPath("/User Homes/abbott/test/Owen.jpg");
        id = (ObjectID)session.getObjectIdByPath("/User Homes/abbott/test/Owen.jpg");

    }

    @After
    public void teardown(){
        //delete everything in the /User Homes/abbott folder

        IObjectID folderId = session.getObjectIdByPath("/User Homes/abbott/test/");
        Folder folder = session.getFolder(folderId.toString());

        IObjectID obj1 = session.getObjectIdByPath("/User Homes/abbott/test/Owen.jpg");
        session.deleteDocument(obj1.toString());

        IObjectID obj2= session.getObjectIdByPath("/User Homes/abbott/test/BeginningPythonForNathan.docx");
        session.deleteDocument(obj2.toString());

        IObjectID obj3 = session.getObjectIdByPath("/User Homes/abbott/test/newImage3.jpg");
        session.deleteDocument(obj3.toString());

        IObjectID obj4 = session.getObjectIdByPath("/User Homes/abbott/test/rename/");
        session.deleteFolder(obj4.toString());
    }



    @Test
    public void testGetShortID() throws Exception {
        // This test case is rendered obsolete by the initialize() function, which uploads a new object with a new id every time.
        //assertEquals("19b0985c-5750-48b3-886c-135efd3f2b2e", iod.getShortID());

        if(!iod.getShortID().isEmpty()){
            assertEquals(true,true);
        }
        else{
            System.out.println("Failed to retrieve a shortId at 'MoreObjectIDTest' line 81");
            assertEquals(false, true);
        }

    }

    @Test
    public void testGetLongID() throws Exception {
       // assertEquals("workspace://SpacesStore/19b0985c-5750-48b3-886c-135efd3f2b2e;1.0", iod.getLongID());
        if(!iod.getLongID().isEmpty()){
            assertEquals(true,true);
        }
        else{
            System.out.println("Failed to retrieve a shortId at 'MoreObjectIDTest' line 94");
            assertEquals(false, true);
        }
    }

    @Test
    public void testCreateShortIdFromLong() throws Exception {
        assertEquals("this_is_the_short_id", id.createShortIdFromLong("workspace://SpacesStore/etc/ect/ect/this_is_the_short_id;1.0"));
    }
}