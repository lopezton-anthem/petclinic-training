/**
 *
 */
package com.atlas.petclinic;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author Swetha Vemuri
 * @author Dileep Roopreddy
 */
@Controller
public class LoginController {
	
	@Autowired
	private ObjectMapper om;
	
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.addCookie(new Cookie("NIMBUS_TENANT_ID", "/anthem/nimbus/petclinic"));
    	response.sendRedirect("/petclinic/#/h/petclinicdashboard/vpDashboard");
    }
    

    @RequestMapping(value = "/apperror", method = RequestMethod.GET)
    public String error(@RequestParam Map<String,String> allParams, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return "customerror";
    }

}
