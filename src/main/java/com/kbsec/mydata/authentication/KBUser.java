
package com.kbsec.mydata.authentication;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.kbsec.mydata.authentication.entity.JWTTokenEntity;

import lombok.*;


@SuppressWarnings("serial")
@Getter
@Builder
@Data
public class KBUser implements Serializable {

	public KBUser() {}
	
	public KBUser(String sid, String accessToken, String refreshToken, boolean isSignIn, Date created) {
		this.sid = sid;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.isSignIn = isSignIn;
		this.created = created;	
	}
    
	
    private String sid;
    private String accessToken;
    private String refreshToken;
    
    @Builder.Default
    private boolean isSignIn = false;

    private Date created;

//    public boolean hasExpired() {
//        if(created == null){
//            return true;
//        }
//        LocalDateTime localDateTime = created.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//        localDateTime = localDateTime.plusHours(1);
//        return  Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()).before(new Date());
//    }
}
