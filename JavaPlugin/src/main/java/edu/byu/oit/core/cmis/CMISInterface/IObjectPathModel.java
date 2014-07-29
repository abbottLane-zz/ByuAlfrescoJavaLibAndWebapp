package edu.byu.oit.core.cmis.CMISInterface;

import java.nio.file.Path;

/**
 * A data modelling class that holds and object name, path, and type. This is used primarily in the Cmis
 * API to upload folders, where it was necessary to keep information about local objects and folder in one spot, and distinguish
 * between types.
 */
public interface IObjectPathModel {

    /**
     * Sets the ObjectName
     * @param objectName A String object representing the objects name
     */
    public void setObjectName(String objectName);

    /**
     * Sets the object's local path
     * @param objectPath A String object representing the local path to the object.
     */
    public void setObjectPath(Path objectPath);

    /**
     * Sets the objectType ... either "folder" or "document"
     * @param objectType A String object representing the obejct type. Usually "folder" or "document".
     */
    public void setObjectType(String objectType);

    /**
     * Retrieves the objects Path.
     * @return Path to the object.
     */
    public Path getObjectPath();

    /**
     * Retrieves the object's name
     * @return String representing the objects name
     */
    public String getObjectName();

    /**
     * Retrieves the objects type.
     * @return A String. Either "folder" or "document".
     */
    public String getObjectType();


}
