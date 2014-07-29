package CMIS.CRUD;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;


public class Navigation {

    Folder currentRoot;

    public Navigation(Folder CurrentRoot_in){
        currentRoot=CurrentRoot_in;
    }
    /**
     *
     * @param currentChildren The children of the current root folder.
     * @param folderName The name of the folder we wish to navigate into, ie designate as our new root folder
     * @return returns the new root node.
     */
    public Folder enterFolder(ItemIterable<CmisObject> currentChildren, String folderName){

        for(CmisObject currentFolder : currentChildren){

            System.out.println("CurrentFolder: " + currentFolder.getName());
            System.out.println("FolderName: " + folderName);
            if(currentFolder.getName().equals(folderName)){

                CmisObject selectedFolder = currentFolder;
                currentRoot = (Folder)selectedFolder;
                return currentRoot;
            }
        }
        return null;
    }

}
