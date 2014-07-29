package cmisTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.client.util.FileUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

public class AlfrescoSession {

	private Session session;
	
	public AlfrescoSession(String alfBox) {
		Map<String, String> parameter = new HashMap<String, String>();
		
		System.out.println("Starting...\n");
		// Set the user credentials
		parameter.put(SessionParameter.USER, "admin");
		parameter.put(SessionParameter.PASSWORD, "Iamb0b123");
	
		// Specify the connection settings
		if (alfBox.equals("prod")) {
			parameter.put(SessionParameter.ATOMPUB_URL, "https://alfresco.byu.edu/alfresco/cmisatom");
		} else {
			parameter.put(SessionParameter.ATOMPUB_URL, "http://"+alfBox+":8080/alfresco/cmisatom");
		}
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
	
		// Set the alfresco object factory
		parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
	
		// Create a session
		SessionFactory factory = SessionFactoryImpl.newInstance();
		session = factory.getRepositories(parameter).get(0).createSession();
	}
	
	// Get an Object Id from Path
	public String getObjectIdByPath(String path) {
		CmisObject object = session.getObjectByPath(path);
		String id = object.getId();
		return id;
	}
	
	// Get Document by Id
	public Document getDocument(String id) {
		Document doc = (Document)session.getObject(id);
		return doc;
	}
	
	// Get Folder by Id
	public Folder getFolder(String id) {
		return (Folder)session.getObject(id);
	}

	// Display document metadata
	public void printMetadata(Document doc) {
		List<Property<?>> l = doc.getProperties();
		Iterator<Property<?>> i = l.iterator();
		while (i.hasNext()) {
			Property<?> p = i.next();
			Object value = p.getValue();
			PropertyType t = p.getType();
			
			switch (t) {
			case INTEGER:
				BigInteger n = (BigInteger) value;
				System.out.println(p.getDisplayName() + " = " + n);
				break;
			case STRING:
				String s = (String) value;
				System.out.println(p.getDisplayName() + " = " + s);
				break;
			default:
				break;
			}
		}
	}
		
	// Download the document
	public void downloadDocument(Document doc) {
		String filename = doc.getName();
		java.io.InputStream stream = doc.getContentStream().getStream();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File("/home/kyleAmos/Downloads/cmisTest/"+filename));
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = stream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			System.out.println("Finished downloading");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void printFolderContentMetadata(String folderId) {
		folderFunction(folderId, 20, 0, true, false);
	}
	
	public void downloadFolderContent(String folderId) {
		folderFunction(folderId, 20, 0, false, true);
	}
	
	/**
	 * Prints metadata from and/or downloads every object in a folder
	 * 
	 * @param folderId
	 * @param maxItemsPerPage
	 * @param skipCount
	 * @param print
	 * @param download
	 */
	public void folderFunction(String folderId, int maxItemsPerPage, int skipCount, boolean print, boolean download) {
		CmisObject object = session.getObject(session.createObjectId(folderId));
		Folder folder = (Folder)object;
		
		OperationContext opContext = session.createOperationContext();
		opContext.setMaxItemsPerPage(maxItemsPerPage);
		
		ItemIterable<CmisObject> children = folder.getChildren(opContext);
		ItemIterable<CmisObject> page = children.skipTo(skipCount).getPage();
		
		Iterator<CmisObject> childItr = page.iterator();
		while(childItr.hasNext()) {
			Document item = (Document)childItr.next();
			if (print) {
				System.out.println("**********************************************");
				printMetadata(item);
			}
			if (download) {
				downloadDocument(item);
			}
		}
	}
	
	public ContentStream createDocument(String newName, String filePath) {
		Path path = FileSystems.getDefault().getPath(filePath);
		byte[] buf = {};
		String mimetype = "";
		
		try {
			buf = Files.readAllBytes(path);
			mimetype = Files.probeContentType(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ByteArrayInputStream input = new ByteArrayInputStream(buf);
		
		return session.getObjectFactory().createContentStream(newName, buf.length, mimetype, input);
	}
	
	public Document uploadDocument(String folderId, String filename, ContentStream contentStream, String contentType, String version) {
		if (contentType == null) contentType = "cmis:document";
		
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, contentType);
		properties.put(PropertyIds.NAME, filename);
		
		Folder folder = getFolder(folderId);
		
		VersioningState v = VersioningState.MINOR;
		if (version != null && version.toLowerCase().equals("major")) v = VersioningState.MAJOR;
		
		return folder.createDocument(properties, contentStream, v);
	}
}
