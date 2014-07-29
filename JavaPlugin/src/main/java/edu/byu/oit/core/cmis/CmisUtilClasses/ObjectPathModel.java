package edu.byu.oit.core.cmis.CmisUtilClasses;


import edu.byu.oit.core.cmis.CMISInterface.IObjectPathModel;

import java.nio.file.Path;

public class ObjectPathModel implements IObjectPathModel {

    private String objectName;
    private Path objectPath;
    private String objectType;

    @Override
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public void setObjectPath(Path objectPath) {
        this.objectPath = objectPath;
    }

    @Override
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    @Override
    public Path getObjectPath() {
        return objectPath;
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public String getObjectType() {
        return objectType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ObjectName : " + objectName + "\n");
        sb.append("ObjectPath : " + objectPath + "\n");
        sb.append("ObjectType : " + objectType + "\n");

        return sb.toString();
    }
}
