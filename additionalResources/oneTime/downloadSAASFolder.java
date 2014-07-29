//package edu.byu.oit.core.cmis.tests.oneTime;
//
//import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
//import edu.byu.oit.core.cmis.CMISInterface.IObjectID;
//import edu.byu.oit.core.cmis.CmisUtilClasses.ObjectID;
//import org.apache.chemistry.opencmis.client.api.CmisObject;
//import org.apache.chemistry.opencmis.client.api.Document;
//import org.apache.chemistry.opencmis.client.api.Folder;
//import org.apache.chemistry.opencmis.client.api.ItemIterable;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import static org.junit.Assert.assertEquals;
//
///**
//* Created by wlane on 7/18/14.
//*/
//public class downloadSAASFolder {
//    CMISSessionInterface session;
//    IObjectID iod;
//    ObjectID id;
//
//    @Before
//    public void initialize(){
//
//        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
//        session = (CMISSessionInterface)context.getBean("brainiac-old");
//        session.setCredentials("admin", "Iamb0b123");
//        session.startSession();
//
//    }
//
//    @Test
//    public void testDownload() throws Exception {
//        IObjectID folderId = session.getObjectIdByPath("/User Homes/SAAS/Queues/Archived/");
//        Folder folder= session.getFolder(folderId.toString());
//        ItemIterable<CmisObject> items = session.getFolderContents(folder);
//
//        for(CmisObject item : items) {
//
//            if(item.getBaseType().getId().equals("cmis:document")) {
//                Document doc = (Document) item;
//                System.out.println("Doc dl: " + doc.getName());
//                session.downloadDocument(doc, "/home/wlane/Documents/SAASuserHome/Queues/Archived/");
//            }
//        }
//    }
//}