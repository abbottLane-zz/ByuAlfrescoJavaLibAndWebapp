package com.springapp.mvc.Controllers;

import com.springapp.mvc.models.uploadRequestModel;
import com.springapp.mvc.service.CmisSessionService;
import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import edu.byu.oit.core.cmis.CMISInterface.IObjectID;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.nio.file.NoSuchFileException;

@Controller
public class uploadController {

    @Resource(lookup="cmis")
    CmisSessionService sessionService;
    private CMISSessionInterface session;

    @RequestMapping(value="/upload", method = RequestMethod.GET)
    public ModelAndView uploadView() {
        return new ModelAndView("upload", "command", new uploadRequestModel());
    }

    @RequestMapping(value="/upload", method = RequestMethod.POST)
    public ModelAndView execute(@ModelAttribute("SpringWeb")uploadRequestModel uploadModel, ModelMap model){

        //ESTABLISH the connection
        session = sessionService.getSession();

        //UPLOAD the file
        String userHome;
        if(sessionService.getUsername().equals("admin")){
            userHome = "abbott";
        }
        else{userHome=sessionService.getUsername();}
        IObjectID folderId = session.getObjectIdByPath("/User Homes/" + userHome+"/");
        String fileName = extractFilename(uploadModel.getUploadPath());

        Document doc;
        try {
            doc = session.uploadDocument(folderId.toString(), fileName, uploadModel.getUploadPath(), null, "1.0", uploadModel.getDescription()); //"cmis:document" was where null is
        }
        catch(java.io.IOException nsf){
            //If an invalid file type is sent through notify the user
            model.addAttribute("invalid", true);
            return new ModelAndView("upload", "command", new uploadRequestModel());
        }
        //FORM thumb/doc urls based on docMimeType:
        String thumbUrl;
        if(session.doesAlfrescoThumbnailExist(doc)) {
            thumbUrl = session.getAlfrescoThumbnailUrl(doc);
        }
        else { // allow for the use of custom thumbnail images
            if (doc.getContentStreamMimeType().contains("text")) {
                thumbUrl = "/resources/additionalResources/customThumbImages/docThumb.png";
            } else if (doc.getContentStreamMimeType().contains("audio")) {
                thumbUrl = "/resources/additionalResources/customThumbImages/musicThumb.png";
            } else if (doc.getContentStreamMimeType().contains("video")) {
                thumbUrl = " /resources/additionalResources/customThumbImages/videoThumb.png";
            } else {
                thumbUrl = "/resources/additionalResources/customThumbImages/docThumb.png";
            }
        }
        String fullUrl = session.getAlfrescoDocumentUrl(doc);

        /////LOAD relevant data into the ModelMap model
        model.addAttribute("docName", doc.getName());
        model.addAttribute("thumbUrl", thumbUrl);
        model.addAttribute("fullUrl", fullUrl);

        //Return the model and view
        return new ModelAndView("upload", "command", new uploadRequestModel());
    }

    private String extractFilename(String filePath) {

        String[] split = filePath.split("/");
        String filename = split[split.length-1];

        return filename;
    }


}

