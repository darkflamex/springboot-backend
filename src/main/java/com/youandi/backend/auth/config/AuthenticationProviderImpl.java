
package com.youandi.backend.auth.config;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

import com.youandi.backend.auth.domain.AuthenticationTokenImpl;
import com.youandi.backend.auth.domain.KBUser;
import com.youandi.backend.auth.domain.LgAccountUser;
import com.youandi.backend.auth.redis.RedisService;


public class AuthenticationProviderImpl implements org.springframework.security.authentication.AuthenticationProvider {

    private RedisService service;

    public AuthenticationProviderImpl(RedisService service) {
        this.service = service;
    }

    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    	String username = (String)authentication.getPrincipal();
    	String password = (String)authentication.getCredentials();

    	
 //        //Right now just authenticate on the basis of the user=pass
//        
    	
    	
//    	if (username.equalsIgnoreCase(password)) {
//    	  		
//    	}
//            ApiToken u = new ApiToken();
//            u.setUsername(username);
//            u.setCreated(new Date());
//            AuthenticationTokenImpl auth = new AuthenticationTokenImpl(u.getUsername(), Collections.emptyList());
//            auth.setAuthenticated(true);
//            auth.setDetails(u);
//            service.setValue(String.format("%s:%s", u.getUsername().toLowerCase(), auth.getHash()), u, TimeUnit.SECONDS, 3600L, true);
//            return auth;
//        } else {
//
//        }
//        return null;
    	
    	KBUser user = KBUser.builder()
    	.username(username)
    	.created(new Date())
    	.build()
    	;
//    	AuthenticationTokenImpl auth = new AuthenticationTokenImpl(username, Collections.emptyList());
//    	auth.setAuthenticated(true);
//    	auth.setDetails(user);

    	AuthenticationTokenImpl authToken = new AuthenticationTokenImpl(username, AuthorityUtils.createAuthorityList("USER"));
    	authToken.setAuthenticated(true);
    	authToken.setDetails(user);
    	service.setValue(String.format("%s:%s", user.getUsername().toLowerCase(), authToken.getHash()), user, TimeUnit.SECONDS, 3600L, true);
    	return authToken;
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }
    
    private void basicCheck(Authentication authentication) {
      	String username = (String)authentication.getPrincipal();
    	String password = (String)authentication.getCredentials();

    	
//      if (username == null || username.length() < 5) {
//      throw new BadCredentialsException("Username not found.");
//  }
//  if (password.length() < 5) {
//      throw new BadCredentialsException("Wrong password.");
//  }

    }
    
    

}
