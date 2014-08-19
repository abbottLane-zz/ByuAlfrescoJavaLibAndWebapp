package edu.byu.oit.core.cmis.Sessions;


import edu.byu.oit.core.cmis.CMISAbstract.AbstractCMISSession;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.chemistry.opencmis.commons.impl.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Required;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AlfrescoSession extends AbstractCMISSession {

    private String boxName;
    private String ticket;
    private String username;
    private String password;
    private String boxNameUrl;

    @Required
    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    //Override of the initializeSession() method: establishes a session with the designated box
    @Override
    public void startSession() {
        Map<String, String> parameter = new HashMap<String, String>();

        // Set the user credentials
        parameter.put(SessionParameter.USER, username);
        parameter.put(SessionParameter.PASSWORD, password);
        //"http://wakko:8080/alfresco/cmisatom"
        parameter.put(SessionParameter.ATOMPUB_URL, getURL(boxName)+"alfresco/cmisatom");

        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

        // Create a session
        SessionFactory factory = SessionFactoryImpl.newInstance();
        setSession(factory.getRepositories(parameter).get(0).createSession());

        setTicket(boxName, username, password);

    }

    private void setTicket(String boxName, String username, String password) {
        URL url;
        HttpURLConnection connection;
        try {
            String urlParameters = "{ \"username\" : \""+username + "\", \"password\" : \"" + password + "\" }";
            url= new URL(getURL(boxName)+"alfresco/service/api/login");
            connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null)
            {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            String _jsonResponse = response.toString();

            JSONObject _jsonResponseObject = (JSONObject)new JSONParser().parse(_jsonResponse);
            JSONObject jsonDataObject = (JSONObject)new JSONParser().parse(_jsonResponseObject.get("data").toString());
            ticket = jsonDataObject.get("ticket").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // This should be updated to inject based on a config file
    // Supports: test/tst/dev, stage/stg, prod/prd, or the box name
    // Returns: base url, ie: http://wakko:8080/
    private String getURL(String boxName) {
        // Specify the connection settings
        String result;

        if (boxName.equals("prod") || boxName.equals("prd")) {
            result = "http://inigo:8080/";
            //result = "https://alfresco.byu.edu/";
        } else if (boxName.equals("stage") || boxName.equals("stg")) {
            result = "http://wakko:8080/";
            //result= "https://alfresco-stg.byu.edu/";
        } else if (boxName.equals("test") || boxName.equals("tst") || boxName.equals("dev")) {
            //result = "https://alfresco-dev.byu.edu/";
            result="http://brainiac:8080/";
        } else {
            result = "http://" + boxName + ":8080/";
        }

        boxNameUrl=result;
        return boxNameUrl;
    }

    @Override
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getTicket() {
        return ticket;
    }

    @Override
    public String getBoxNameUrl() {
        return boxNameUrl;
    }

}
