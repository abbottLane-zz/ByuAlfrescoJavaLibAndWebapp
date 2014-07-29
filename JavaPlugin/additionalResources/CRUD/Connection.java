package CMIS.CRUD;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Connection {

    private String repositoryURL;
    private SessionFactory sessionFactory;
    private Map<String,String> sessionParams;
    private Repository currentRepository;


    public Connection(String RepositoryURL_in) {
        repositoryURL = RepositoryURL_in;
        initializeSessionFactory();
        initializeParameterMap();
    }

    public Session getFirstRepositorySession(String username, String password){

        sessionParams.put(SessionParameter.USER, username);
        sessionParams.put(SessionParameter.PASSWORD, password);
        sessionParams.put(SessionParameter.ATOMPUB_URL, repositoryURL); //"http://wakko1:8080/alfresco/cmisatom"
        sessionParams.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        // find all the repositories at this URL - there should only be one.
        List<Repository> repositories = new ArrayList<Repository>();
        repositories = sessionFactory.getRepositories(sessionParams);
        for (Repository r : repositories) {
            System.out.println("Found repository: " + r.getName());
        }

        // create session with the first (and only) repository
        currentRepository = repositories.get(0);
        sessionParams.put(SessionParameter.REPOSITORY_ID, currentRepository.getId());
        Session session = sessionFactory.createSession(sessionParams);

        return session;
    }


    private void initializeParameterMap() {
        sessionParams = new HashMap<String, String>();
    }

    public void initializeSessionFactory() {
        sessionFactory = SessionFactoryImpl.newInstance();
    }
    public Repository getCurrentRepository(){
        return currentRepository;
    }
}
