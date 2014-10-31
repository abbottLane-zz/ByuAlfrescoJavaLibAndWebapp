package com.springapp.mvc.service;

import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import org.springframework.stereotype.Service;


@Service("cmis")
public class CmisSessionService {

    private CMISSessionInterface session;
    private String username;
    private String password;
    private String environment;

    public CmisSessionService(){}

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setSession(CMISSessionInterface session) {
        this.session = session;
    }

    public CMISSessionInterface getSession() {
        return session;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
