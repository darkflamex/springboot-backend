
package com.youandi.backend.auth.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.*;


@Getter
@Builder
@Data
public class KBUser {

	public KBUser() {}
	
	public KBUser(String username, boolean isSignIn, Date created) {
		this.username = username;
		this.isSignIn = isSignIn;
		this.created = created;
	}
    
	
    private String username;
    
    @Builder.Default
    private boolean isSignIn = false;

    private Date created;

    public boolean hasExpired() {
        if(created == null){
            return true;
        }
        LocalDateTime localDateTime = created.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.plusHours(1);
        return  Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()).before(new Date());
    }
    
    public KBUser(LgAccountUser lgAccountUser) {
    	this.username = lgAccountUser.getUserid();
    	
    }
    
    

}
