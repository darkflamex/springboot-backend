
package com.kbsec.mydata.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.ToString;

@ToString(callSuper = true)
public class RefreshTokenAuthenticationTokenImpl extends AbstractAuthenticationToken {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2834089109988062923L;
	
	private String refreshTokenJwt;
	
	public RefreshTokenAuthenticationTokenImpl(String refreshTokenJwt, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.refreshTokenJwt = refreshTokenJwt;
	}
	
	public RefreshTokenAuthenticationTokenImpl(String refreshTokenJwt) {
		super(null);
		this.refreshTokenJwt = refreshTokenJwt;
	}
	

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return refreshTokenJwt != null ? refreshTokenJwt.toString() : "";
    }

//    public String getHash() {
//        return DigestUtils.md5DigestAsHex(String.format("%s_%d", username, ((KBUser) getDetails()).getCreated().getTime()).getBytes());
//    }

}
