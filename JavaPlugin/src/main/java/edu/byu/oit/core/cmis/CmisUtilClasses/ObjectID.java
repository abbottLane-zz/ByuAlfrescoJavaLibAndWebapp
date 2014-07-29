package edu.byu.oit.core.cmis.CmisUtilClasses;

import edu.byu.oit.core.cmis.CMISInterface.IObjectID;


public class ObjectID implements IObjectID {

    String shortId;
    String longId;
    String version;

    public ObjectID(String fullId_in){
        //set longId
        longId = fullId_in;

        //set shortID
        shortId = createShortIdFromLong(longId);
    }

    @Override
    public String getShortID() {
        return shortId;
    }

    @Override
    public String getLongID() {
        return longId;
    }

    @Override
    public String toString(){
        return longId;
    }


    public String createShortIdFromLong(String longId){
        StringBuilder sb = new StringBuilder();

        //split on '/'
        String[] urlSections = longId.split("/");

        //store the last section of the url as the shortID
        String shortCodeWithVersion = urlSections[urlSections.length-1];
        String[] shortPlusVers = shortCodeWithVersion.split(";");

        String shortCodeWithoutVersion = shortPlusVers[0];

        if(shortPlusVers.length >1) {
            version = shortPlusVers[1];
        }

        return shortCodeWithoutVersion;
    }
}
