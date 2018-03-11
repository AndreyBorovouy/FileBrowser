package com.waverley.fileBrowser.controller;


import com.waverley.fileBrowser.service.api.UserService;
import com.waverley.fileBrowser.service.impl.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @Autowired
    private AuthService authenticate;
    @Autowired
    private UserService userService;

//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public ModelAndView defaultPage2() {
//
//        ModelAndView modelAndView = new ModelAndView();
//
//        modelAndView.setViewName("/fileBrowserTestForm/homePage.jsp");
//        return modelAndView;

 //   }

    @PreAuthorize("hasRole('access')")
    @RequestMapping(value = {"/home", "/"}, method = RequestMethod.GET)
    public String defaultPage() {


        String res = "" +
                "/fileBrowserTestForm/homePage.jsp";
        //general user must be authenticated always
     //   authenticate.authenticate();
      //  model = new ModelAndView("redirect:"+ "fileBrowserTestForm/homePage.jsp");
        if(authenticate.checkTimeChanging()){
            res = "redirect:/home";
        }
        return res;
    }



    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error, @RequestParam(value = "logout", required = false) String logout) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }
        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }
        model.setViewName("/fileBrowserTestForm/userLogin.jsp");

        return model;
    }

    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public ModelAndView userLogin(@RequestParam(value = "username", required = false) String username, @RequestParam(value = "password", required = false) String password, @RequestParam(value = "error", required = false) String error) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
            model.setViewName("/fileBrowserTestForm/userLogin.jsp");
        }else {
            model = userService.authanticationUser(username, password);
/*
            HttpSession sess = request.getSession(true);

            if (sess.isNew()) {
                Cookie cookie = new Cookie("JSESSIONID", sess.getId());
                cookie.setMaxAge(JSESSIONID_MAX_AGE);
                ((HttpServletResponse) res).reset();
                ((HttpServletResponse) res).addCookie(cookie);
            }
            */
        }
        return model;
    }


//    @RequestMapping(value = "/enter", method = RequestMethod.GET)
//    public String enter() {
//
//        authenticate.authenticate();
//        return "redirect:/home";
//
//    }

}