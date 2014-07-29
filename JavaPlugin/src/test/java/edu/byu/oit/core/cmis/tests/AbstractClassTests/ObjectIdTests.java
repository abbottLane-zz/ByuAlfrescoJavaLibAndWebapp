package edu.byu.oit.core.cmis.tests.AbstractClassTests;


import edu.byu.oit.core.cmis.CmisUtilClasses.ObjectID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObjectIdTests {

/*
    private String createShortIdFromLong(String longId){

    // Example: workspace://SpacesStore/bba878fe-19e7-495a-987b-c5ba55795a25;1.2
    // Alt:     workspace://SpacesStore/bd058e6e-40e2-485f-82cf-45ecc98837a9

    int index1 = longId.lastIndexOf('/')+1;
    int index2 = longId.contains(";") ? longId.lastIndexOf(';') : longId.length();

    return longId.substring(index1, index2);
*/

    @Test
    public void testCreateObjectId() {
        ObjectID id = new ObjectID("workspace://SpacesStore/bba878fe-19e7-495a-987b-c5ba55795a25;1.2");
        String shortID = id.getShortID();

        assertEquals("bba878fe-19e7-495a-987b-c5ba55795a25", shortID);

        id = new ObjectID("workspace://SpacesStore/bba878fe-19e7-495a-987b-c5ba55795a25");
        shortID = id.getShortID();

        assertEquals("bba878fe-19e7-495a-987b-c5ba55795a25", shortID);
    }
}
