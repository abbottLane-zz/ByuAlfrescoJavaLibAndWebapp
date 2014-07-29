package edu.byu.oit.core.cmis.CMISInterface;

public interface IObjectID {


	public String ID = null;


    /**
     *
     * @return Returns a truncated version of the regular long objectId
     */
	public String getShortID();

    /**
     *
     * @return Returns the standard long version of the objectId
     */
	public String getLongID();

    /**
     *
     * @return Returns the string version of the objectId compatible with all method calls that ask for an object id
     */
    public String toString();



}
