package edu.byu.oit.core.cmis.Sessions;

import edu.byu.oit.core.cmis.CMISAbstract.AbstractCMISSession;
import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import org.springframework.beans.factory.annotation.Required;


public class SharepointSession  extends AbstractCMISSession implements CMISSessionInterface {
    private String boxName;
    private String ticket;
    private String username;
    private String password;
    private String boxNameUrl;

    @Required
    public void setBoxName(String boxName){
        this.boxName=boxName;
    }

    @Override
    public String getTicket() {
        return null;
    }

    @Override
    public String getBoxNameUrl() {
        return boxNameUrl;
    }

    @Override
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void startSession() {

    }
}
