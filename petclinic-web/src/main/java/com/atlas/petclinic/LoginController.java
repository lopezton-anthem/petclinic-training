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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.antheminc.oss.nimbus.FrameworkRuntimeException;
import com.antheminc.oss.nimbus.domain.defn.Constants;
import com.antheminc.oss.nimbus.domain.model.state.multitenancy.Tenant;
import com.antheminc.oss.nimbus.domain.model.state.multitenancy.TenantRepository;
import com.antheminc.oss.nimbus.domain.session.SessionProvider;
import com.antheminc.oss.nimbus.entity.client.user.ClientUser;


/**
 * @author Swetha Vemuri
 * @author Dileep Roopreddy
 */
@Controller
public class LoginController {

	@Autowired
	private TenantRepository tenantRepository;
	
	@Autowired
	private SessionProvider sessionProvider;
	
    @GetMapping(value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
    	return "login-multitenant";
    }
    
    @GetMapping(value = "/selectTenant")
    public ModelAndView selectTenant(HttpServletRequest request, HttpServletResponse response) {
    	ClientUser clientUser = getLoggedInClientUser();
    	Set<Tenant> tenants = this.tenantRepository.findByIds(clientUser.getTenantIds());
    	return new ModelAndView("selectTenant", "tenants", tenants);
    }
    
    @GetMapping(value = "/processLogin")
    public void processLogin(HttpServletRequest request, HttpServletResponse response) {
    	
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
		try {
			response.sendRedirect("/petclinic/selectTenant");
		} catch (IOException e) {
			throw new FrameworkRuntimeException("Failed to redirect to select tenant page", e);
		}
    }

    @GetMapping(value = "/chooseTenant")
    public void chooseTenant(HttpServletRequest request, HttpServletResponse response, @RequestParam Long tenantId) {
    	Tenant tenant = this.tenantRepository.findById(tenantId);
    	response.addCookie(new Cookie("NIMBUS_ACTIVE_TENANT_PREFIX", tenant.getPrefix()));
    	sessionProvider.setAttribute(Constants.ACTIVE_TENANT_COOKIE.code, tenant.getPrefix());
		redirectToDashboard(response);
    }
    
    @GetMapping(value = "/apperror")
    public String error(@RequestParam Map<String,String> allParams, HttpServletRequest request, HttpServletResponse response) {
    	return "customerror";
    }

    private void redirectToDashboard(HttpServletResponse response) {
    	try {
			response.sendRedirect("/petclinic/#/h/petclinicdashboard/vpDashboard");
		} catch (IOException e) {
			throw new FrameworkRuntimeException("Failed to redirect to dashboard", e);
		}
    }
    
    /**
     * Mock implementation. Replace with real implementation in case of authentication.
     */
    private ClientUser getLoggedInClientUser() {
    	ClientUser clientUser = new ClientUser();
    	clientUser.setTenantIds(new HashSet<>());
    	clientUser.getTenantIds().add(1L);
    	clientUser.getTenantIds().add(2L);
    	sessionProvider.setLoggedInUser(clientUser);
    	return clientUser;
    }
}
