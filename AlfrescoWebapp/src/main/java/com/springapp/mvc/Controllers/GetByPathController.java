package com.springapp.mvc.Controllers;

import com.springapp.mvc.models.ByPathForm;
import com.springapp.mvc.models.LoginModel;
import com.springapp.mvc.service.CmisSessionService;
import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import edu.byu.oit.core.cmis.CMISInterface.IObjectID;
import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;


@Controller
public class GetByPathController {

    @Resource(lookup="cmis")
    private CmisSessionService sessionService;
    private CMISSessionInterface session;

    @RequestMapping(value="/byPath", method = RequestMethod.GET)
    public ModelAndView ByPathForm() {
        return new ModelAndView("path", "command", new ByPathForm());
    }

    @RequestMapping(value="/byPath", method = RequestMethod.POST)
    public ModelAndView execute(@ModelAttribute("SpringWeb")ByPathForm pathModel, ModelMap model){

        session = sessionService.getSession();

        //retrieve requested doc
        String objectName = pathModel.getPath();

        //form full path to object
        StringBuilder sb = new StringBuilder("/User Homes/");
        if(sessionService.getUsername().equals("admin")) {
            sb.append("abbott/");
        }
        else{
            sb.append(sessionService.getUsername() + "/");
        }
        sb.append(objectName);

        //Extract object id based on full path
        IObjectID objectId= session.getObjectIdByPath(sb.toString());
        Document doc = session.getDocument(objectId.toString());

        //form urls
        String thumbUrl;
        if(session.doesAlfrescoThumbnailExist(doc)) {
            thumbUrl = session.getAlfrescoThumbnailUrl(doc);
        }
        else{ // allow for the use of custom thumbnail images
            if(doc.getContentStreamMimeType().contains("text")) {
                thumbUrl = "./resources/additionalResources/customThumbImages/docThumb.png";
            }
            else if(doc.getContentStreamMimeType().contains("audio")){
                thumbUrl= "./resources/additionalResources/customThumbImages/musicThumb.png";
            }
            else if (doc.getContentStreamMimeType().contains("video")){
                thumbUrl="./resources/additionalResources/customThumbImages/videoThumb.png";
            }
            else{
                thumbUrl = "./resources/additionalResources/customThumbImages/docThumb.png";
            }
        }
        String fullUrl = session.getAlfrescoDocumentUrl(doc);

        //Put full/thumb urls in the model.addAttribute container
        model.addAttribute("thumbnail",thumbUrl );
        model.addAttribute("docName", doc.getName());
        model.addAttribute("docDescription", doc.getProperty("cm:description").getValueAsString());
        model.addAttribute("fullUrl", fullUrl);

        //Return the model and view
        return new ModelAndView("path", "command", pathModel);
    }
}




