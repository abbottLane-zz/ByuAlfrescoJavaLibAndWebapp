package com.springapp.mvc.Controllers;

import com.springapp.mvc.models.ByPathForm;
import com.springapp.mvc.models.LoginModel;
import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class LoginController {

    CMISSessionInterface session;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public ModelAndView loginPageLoad() {
        return new ModelAndView("index", "login", new LoginModel());
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public ModelAndView loginAction(@ModelAttribute("SpringWeb")LoginModel loginModel, ModelMap model){

        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
            session = (CMISSessionInterface) context.getBean("test");
            session.setCredentials(loginModel.getUser() ,loginModel.getPassword());
            session.startSession();
        }
        catch(Exception e){
            model.addAttribute("loginError", true);
            return new ModelAndView("index", "login", new LoginModel());
            //return "redirect:/";
        }
        //return "redirect:/byPath";
        return new ModelAndView("path", "command", new ByPathForm());
    }
}


