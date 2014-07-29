package org.byu.oit.cmis;

import org.byu.oit.cmis.CMISInterface.IObjectID;

public interface ICMISSession {
	
	public boolean setCredentials(String username, String password);
	public boolean connectTo(String stage);
	
	public IObjectID getIDByPath(String path);
//	TODO: Flesh out API
}
