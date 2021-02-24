
package com.kbsec.mydata.authentication;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@SuppressWarnings("serial")
@Getter
@Builder
@Data
public class KBUser implements Serializable {

    private String sid;
    private String accessToken;
    private String refreshToken;
    
    @Builder.Default
    private boolean isSignIn = false;

    private String created;
    
    private String accessTokenIssued;
    private String refreshTokenIssued;
    
    public KBUser() {}
	
	public KBUser(String sid, String accessToken
			, String refreshToken, boolean isSignIn
			, String created, String accessTokenIssued, String refreshTokenIssued) {
		this.sid = sid;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.isSignIn = isSignIn;
		this.created = created;	
		this.accessTokenIssued = accessTokenIssued;
		this.refreshTokenIssued = refreshTokenIssued;
	}
    

//    public boolean hasExpired() {
//        if(created == null){
//            return true;
//        }
//        LocalDateTime localDateTime = created.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//        localDateTime = localDateTime.plusHours(1);
//        return  Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()).before(new Date());
//    }
}
