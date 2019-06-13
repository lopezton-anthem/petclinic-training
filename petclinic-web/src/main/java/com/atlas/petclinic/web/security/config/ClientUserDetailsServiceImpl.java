/**
 * 
 */
package com.atlas.petclinic.web.security.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.antheminc.oss.nimbus.app.extension.config.ClientUserDetails;
import com.antheminc.oss.nimbus.app.extension.config.ClientUserDetailsService;
import com.antheminc.oss.nimbus.app.extension.config.DefaultClientUserDetails;
import com.antheminc.oss.nimbus.domain.cmd.Action;
import com.antheminc.oss.nimbus.domain.cmd.Command;
import com.antheminc.oss.nimbus.domain.cmd.CommandBuilder;
import com.antheminc.oss.nimbus.domain.cmd.CommandMessage;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecution.MultiOutput;
import com.antheminc.oss.nimbus.domain.cmd.exec.CommandExecutorGateway;
import com.antheminc.oss.nimbus.domain.session.SessionProvider;
import com.antheminc.oss.nimbus.entity.client.access.ClientAccessEntity;
import com.antheminc.oss.nimbus.entity.client.access.ClientUserRole;
import com.antheminc.oss.nimbus.entity.client.user.ClientUser;
import com.antheminc.oss.nimbus.entity.user.UserRole;
import com.atlas.petclinic.ApplicationBeanResolver;

import lombok.RequiredArgsConstructor;

/**
 * @author AC63348
 *
 */
@RequiredArgsConstructor
public class ClientUserDetailsServiceImpl implements ClientUserDetailsService {

public final static Logger LOG = LoggerFactory.getLogger(ClientUserDetailsServiceImpl.class);
	
	
	private final SessionProvider sessionProvider;	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String searchUri = "/anthem/ltss/p/clientuser/_search?fn=query&where=clientuser.loginId.eq('"+username+"')&fetch=1";
		MultiOutput multiOp = executeCommand(searchUri, Action._search);
		if(multiOp.getSingleResult() != null) {
			ClientUser clientUser  = (ClientUser)multiOp.getSingleResult();
			createResolvedAccessEntities(clientUser);
			System.out.println("@@ Set Web Session Provider with logged in user "+clientUser.getLoginId());
			sessionProvider.setLoggedInUser(clientUser);
			return new DefaultClientUserDetails(clientUser);
		} 
		throw new UsernameNotFoundException("Unauthorized Access. Please set up user information in the system");
	}
	
	@SuppressWarnings("unchecked")
	private void createResolvedAccessEntities(ClientUser clientUser) {
		if(CollectionUtils.isEmpty(clientUser.getRoles()))
			return;
		
		Set<String> userRoleCodes = clientUser.getRoles().stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
		
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(String userRole: userRoleCodes) {
			if(i == userRoleCodes.size() - 1) {
				sb.append("\"").append(userRole).append("\"");
			}
			else{
				sb.append("\"").append(userRole).append("\"").append(",");
			}
			i++;
		}
		
		// get client user role(s)
		String searchRolesUri = "Anthem/fep/ltss/p/userrole/_search?fn=query&where=userrole.code.in("+sb.toString()+")";
		MultiOutput multiOp = executeCommand(searchRolesUri, Action._search);
		
		if(multiOp.getSingleResult() == null)
			return;
		
		List<ClientUserRole> clientUserRoles = (List<ClientUserRole>) multiOp.getSingleResult();
		
		// get authorities for each client user role
		Set<String> allAuthorityCodes = new HashSet<>();
		
		for(ClientUserRole cUserRole: clientUserRoles) {
			allAuthorityCodes.addAll(cUserRole.getAccessEntities());
		}
		
		StringBuilder sb2 = new StringBuilder();
		int j = 0;
		for(String authorityCode: allAuthorityCodes) {
			if(j == allAuthorityCodes.size() - 1) {
				sb2.append("\"").append(authorityCode).append("\"");
			}
			else{
				sb2.append("\"").append(authorityCode).append("\"").append(",");
			}
			j++;
		}
		String searchAuthoritiesUri = "Anthem/fep/ltss/p/authorities/_search?fn=query&where=authorities.code.in("+sb2.toString()+")";
		multiOp = executeCommand(searchAuthoritiesUri, Action._search);
		
		if(multiOp.getSingleResult() == null)
			return;
		
		List<ClientAccessEntity> authorities = (List<ClientAccessEntity>) multiOp.getSingleResult();
		
		clientUser.setResolvedAccessEntities(authorities);
	}
	

	private MultiOutput executeCommand(String searchRolesUri, Action action) {
		Command cmd = CommandBuilder.withUri(searchRolesUri).getCommand();
		cmd.setAction(action);
		CommandMessage cmdMsg = new CommandMessage();
		cmdMsg.setCommand(cmd);
		CommandExecutorGateway commandExecutorGateway = ApplicationBeanResolver.getBean(CommandExecutorGateway.class);
		MultiOutput multiOp = commandExecutorGateway.execute(cmdMsg);
		return multiOp;
	}

	@Override
	public Object getAuthenticatedPrincipal() {
		return null;
	}

	@Override
	public ClientUserDetails getClientUserDetails() {
		ClientUser petclinic_user = (ClientUser) sessionProvider.getLoggedInUser();
		
		if (petclinic_user != null ) {
			return new DefaultClientUserDetails(petclinic_user);
		} else
			return null;
	}
}
