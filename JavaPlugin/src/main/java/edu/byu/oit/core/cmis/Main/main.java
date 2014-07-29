package edu.byu.oit.core.cmis.Main;

import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class main {
    public static void main(String[] args){

        //using SPRING, establish a connection and get a session
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        CMISSessionInterface session = (CMISSessionInterface)context.getBean("dev");
        session.setCredentials("admin", "Iamb0b123");
        session.startSession();




        //Do Stuff with the session
        Document doc = session.getDocumentByPath("/User Homes/abbott/Owen.jpg");
        session.downloadDocument(doc, "/home/wlane/Documents/testNoDependencies/");
    }
}
