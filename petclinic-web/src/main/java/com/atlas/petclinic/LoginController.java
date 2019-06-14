package com.atlas.petclinic;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.antheminc.oss.nimbus.FrameworkRuntimeException;
import com.antheminc.oss.nimbus.domain.defn.Constants;
import com.antheminc.oss.nimbus.domain.model.state.multitenancy.Tenant;
import com.antheminc.oss.nimbus.domain.model.state.multitenancy.TenantRepository;
import com.antheminc.oss.nimbus.entity.client.user.ClientUser;


/**
 * @author Swetha Vemuri
 * @author Dileep Roopreddy
 */
@Controller
public class LoginController {

	@Autowired
	private TenantRepository tenantRepository;
	
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
    	return "login-multitenant";
    }
    
    @RequestMapping(value = "/selectTenant", method = RequestMethod.GET)
    public ModelAndView selectTenant(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	ClientUser clientUser = getLoggedInClientUser();
    	Set<Tenant> tenants = this.tenantRepository.findByIds(clientUser.getTenantIds());
    	return new ModelAndView("selectTenant", "tenants", tenants);
    }
    
    @RequestMapping(value = "/processLogin", method = RequestMethod.GET)
    public void processLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	ClientUser clientUser = getLoggedInClientUser();
    	
    	// if no tenants are defined
    	if (CollectionUtils.isEmpty(clientUser.getTenantIds())) {
    		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        	throw new FrameworkRuntimeException("User is missing tenant information and is not allowed to access the system. Please contact a system administrator.");
    	}
    	
		Iterator<Long> iter = clientUser.getTenantIds().iterator();
		Long id = iter.next();
		if (!iter.hasNext()) {
			// if a single tenant is defined
    		Tenant tenant = this.tenantRepository.findById(id);
    		response.addCookie(new Cookie(Constants.ACTIVE_TENANT_COOKIE.code, tenant.getPrefix()));
    		redirectToDashboard(response);
    		return;
		}
    	
		// if more than one tenant is defined
    	response.sendRedirect("/petclinic/selectTenant");
    }

    @RequestMapping(value = "/chooseTenant", method = RequestMethod.GET)
    public void chooseTenant(HttpServletRequest request, HttpServletResponse response, @RequestParam Long tenantId) throws Exception {
    	Tenant tenant = this.tenantRepository.findById(tenantId);
    	response.addCookie(new Cookie("NIMBUS_ACTIVE_TENANT_PREFIX", tenant.getPrefix()));
		redirectToDashboard(response);
    }
    
    @RequestMapping(value = "/apperror", method = RequestMethod.GET)
    public String error(@RequestParam Map<String,String> allParams, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return "customerror";
    }

    private void redirectToDashboard(HttpServletResponse response) throws IOException {
    	response.sendRedirect("/petclinic/#/h/petclinicdashboard/vpDashboard");
    }
    
    /**
     * Mock implementation. Replace with real implementation in case of authentication.
     */
    private ClientUser getLoggedInClientUser() {
    	ClientUser clientUser = new ClientUser();
    	clientUser.setTenantIds(new HashSet<>());
    	// DEMO: Add mock tenant ids here
    	clientUser.getTenantIds().add(1L);
    	clientUser.getTenantIds().add(2L);
    	return clientUser;
    }
}
