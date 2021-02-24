
package com.kbsec.mydata.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class SignInAuthenticationToken extends AbstractAuthenticationToken {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2834089109988062923L;
	
	private String sid;
	
	
	public SignInAuthenticationToken(String sid, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		super.setAuthenticated(true);
		this.sid = sid;
	}
	
    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return sid != null ? sid.toString() : "";
    }

//    public String getHash() {
//        return DigestUtils.md5DigestAsHex(String.format("%s_%d", username, ((KBUser) getDetails()).getCreated().getTime()).getBytes());
//    }

}
