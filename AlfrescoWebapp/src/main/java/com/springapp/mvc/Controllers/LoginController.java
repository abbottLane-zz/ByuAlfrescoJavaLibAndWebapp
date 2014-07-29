package com.springapp.mvc.Controllers;

import com.springapp.mvc.models.LoginModel;
import edu.byu.oit.core.cmis.CMISInterface.CMISSessionInterface;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class LoginController {

    CMISSessionInterface session;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("index", "login", new LoginModel());
    }
}


