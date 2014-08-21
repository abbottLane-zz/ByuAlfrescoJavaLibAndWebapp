package edu.byu.oit.core.cmis.tests.ContentLister;

import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import edu.byu.oit.core.cmis.CMISInterface.IObjectID;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by wlane on 8/21/14.
 */
public class Lister {
   public static void main(String[] args){

       System.out.println("Hello! started program");

        CMISSessionInterface session;
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        session = (CMISSessionInterface)context.getBean("test");
        session.setCredentials("admin", "Iamb0b123");
        session.startSession();

        IObjectID folderId = session.getObjectIdByPath("/User Homes/abbott");
        Folder folder = session.getFolder(folderId.toString());

       int indents=0;
       StringBuilder sb = new StringBuilder();
       listItems(folder, sb, session, indents);

       //write sb to file
       PrintWriter writer = null;
       try {
           writer = new PrintWriter("RepoContent.txt", "UTF-8");
           writer.print(sb.toString());
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }

       writer.close();
    }

    public static void listItems(Folder root, StringBuilder sb, CMISSessionInterface session, int indents) {
        System.out.println("Discovering contents of : "+root.getName());

        //get folder name, append to string with /n and tab
        sb.append(getIndents(indents) +"FOLDER: " + root.getName() + "\n");
        indents++;
        //get children: append all doc names: if not a doc, call listItems on the folder
        ItemIterable<CmisObject> items = session.getFolderContents(root);

        for (CmisObject obj : items) {
            if (obj.getBaseType().getId().equals("cmis:document")) {
                Document doc = (Document) obj;
                sb.append(getIndents(indents) + doc.getName());
                sb.append("\n");
            } else if (obj.getBaseType().getId().equals("cmis:folder")) {
                Folder fol = (Folder) obj;
                listItems(fol, sb, session, indents);
            }
        }
        indents--;
    }

    private static String getIndents(int indents) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < indents;i++){
            sb.append("    ");

        }
        return sb.toString();

    }
}
