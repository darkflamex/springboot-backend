
package com.youandi.backend.auth.domain;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.DigestUtils;

import lombok.Setter;
import lombok.ToString;


@ToString(callSuper = true)
public class AuthenticationTokenImpl2 extends AbstractAuthenticationToken {

   
	/**
	 * 
	 */
	private static final long serialVersionUID = -7410222213594564748L;
	
	@Setter
    private String username;

    public AuthenticationTokenImpl2(String principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = principal;
    }

    public void authenticate() {
        if (getDetails() != null && getDetails() instanceof KBUser && !((KBUser) getDetails()).hasExpired()) {
            setAuthenticated(true);
        } else {
            setAuthenticated(false);
        }
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return username != null ? username.toString() : "";
    }

}
