
package com.kbsec.mydata.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.DigestUtils;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;


@Data
@ToString(callSuper = true)
public class RefreshAuthenticationTokenImpl extends AbstractAuthenticationToken {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2834089109988062923L;
	
	private String refreshToken;
	
	public RefreshAuthenticationTokenImpl(String refreshToken, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.refreshToken = refreshToken;
	}
	
	public RefreshAuthenticationTokenImpl(String refreshToken) {
		super(null);
		this.refreshToken = refreshToken;
		setAuthenticated(false);
	}
	

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return refreshToken != null ? refreshToken.toString() : "";
    }

//    public String getHash() {
//        return DigestUtils.md5DigestAsHex(String.format("%s_%d", username, ((KBUser) getDetails()).getCreated().getTime()).getBytes());
//    }

}
