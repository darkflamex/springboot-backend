package com.kbsec.mydata.authentication.filter;

import static com.kbsec.mydata.authentication.config.AuthenticationConfig.ACCESS_TOKEN;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.kbsec.mydata.authentication.KBUser;
import com.kbsec.mydata.authentication.RefreshAuthenticationTokenImpl;
import com.kbsec.mydata.authentication.RefreshTokenAuthenticationTokenImpl;
import com.kbsec.mydata.authentication.config.AuthenticationConfig;
import com.kbsec.mydata.authentication.config.DefaultApiResponse;
import com.kbsec.mydata.authentication.entity.JWTTokenEntity;
import com.kbsec.mydata.authentication.exception.KBAuthenticationException;
import com.kbsec.mydata.authentication.jsonwebtoken.JwtUtils;
import com.kbsec.mydata.authentication.redis.RedisEntity;
import com.kbsec.mydata.authentication.redis.RedisService;
import com.kbsec.mydata.authentication.response.ApiResponse;
import com.kbsec.mydata.authentication.response.DefaultApiResponseBuilder;

/**
 * Sign-In Filter
 * @author skyrun
 *
 */
public class RefreshTokenFilter extends AbstractAuthenticationProcessingFilter {



	static final Logger logger = LoggerFactory.getLogger(RefreshTokenFilter.class);

	private RedisService redisService;
	private String secret;
	private AuthenticationConfig apiTokenConfig;

	private String profile;

	private MessageSource messageSource;

	public RefreshTokenFilter(String url, AuthenticationManager authenticationManager, 
			RedisService redisService, AuthenticationConfig apiTokenConfig,
			MessageSource messageSource) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authenticationManager);

		this.redisService = redisService;
		this.apiTokenConfig = apiTokenConfig;
		this.messageSource = messageSource;
		secret = Sha512DigestUtils.shaHex(apiTokenConfig.getSigningKey());
	}   

	@Override
	public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {    	
		return getAuthenticationManager()

				.authenticate(extract(httpServletRequest));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

		logger.info("Successful Auth=========");

		RefreshAuthenticationTokenImpl auth = (RefreshAuthenticationTokenImpl) authentication;
		String refreshToken = (String)auth.getPrincipal();

		logger.error("refreshToken : " + refreshToken);

		KBUser user1 = (KBUser)authentication.getDetails();

		logger.info("KBUser user :" + user1.toString());
		logger.info("AuthenticationConfig.REFRESH_TOKEN_PREFIX + refreshToken : " + AuthenticationConfig.REFRESH_TOKEN_PREFIX + refreshToken);


		KBUser user = (KBUser) redisService.getValue(AuthenticationConfig.REFRESH_TOKEN_PREFIX + refreshToken, KBUser.class);



		if(redisService.hasKey(AuthenticationConfig.ACCESS_TOKEN_PREFIX + user.getAccessToken())) {

			// 이전에 발급한 accessToken 이 존재한다면
			// accessToken 관련 삭제  

			redisService.delete(AuthenticationConfig.ACCESS_TOKEN_PREFIX + user.getAccessToken());
			redisService.delete(AuthenticationConfig.SID_PREFIX + AuthenticationConfig.ACCESS_TOKEN_PREFIX + user.getSid());
		}

		LocalDateTime now = LocalDateTime.now();
		String sNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")); 



		// accessToken 신규 생
		String accessToken = UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
		user.setAccessToken(accessToken);
		user.setAccessTokenIssued(sNow);

		logger.info("apiTokenConfig.getExpired() : " + apiTokenConfig.getExpired().toString());

		/**
		 * Access Token JWT 생
		 */
		Map<String, Object> claims = new HashMap<>();
		claims.put(ACCESS_TOKEN, accessToken);
		String accessTokenJwt = JwtUtils.build(AuthenticationConfig.ACCESS_TOKEN, claims, now, apiTokenConfig.getExpired(), secret);

		// accessToken token store에 저장
		RedisEntity accessTokenEntity = RedisEntity.builder()
				.key(AuthenticationConfig.ACCESS_TOKEN_PREFIX + accessToken)
				.value(user)
				.unit(TimeUnit.SECONDS)
				.timeout(apiTokenConfig.getExpired())
				.build();

		RedisEntity sidAccessToken = RedisEntity.builder()
				.key(AuthenticationConfig.SID_PREFIX + AuthenticationConfig.ACCESS_TOKEN_PREFIX + user.getSid())
				.value(accessToken)
				.unit(TimeUnit.SECONDS)
				.timeout(apiTokenConfig.getExpired())
				.build();

		redisService.add( Arrays.<RedisEntity>asList(
				accessTokenEntity
				), now);

		redisService.add( Arrays.<RedisEntity>asList( 
				sidAccessToken
				), now);

		// response 출력
		JWTTokenEntity jwtToken = JWTTokenEntity.builder()
				.accessToken(accessTokenJwt)
				.build();

		profile = "dev";
		ApiResponse apiResponse = DefaultApiResponseBuilder.defaultApiResponse(profile, messageSource, HttpStatus.OK, 
				"200", "인증에 성공하였습니다", jwtToken);
		DefaultApiResponseBuilder.responseWrite(response, apiResponse);

		//DefaultApiResponse.response(response, jwtToken);

	}



	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// super.unsuccessfulAuthentication(request, response, failed);

		String profile = "dev";

		if(failed instanceof KBAuthenticationException) {
			KBAuthenticationException kbAuthException = (KBAuthenticationException)failed;
			logger.info("KBAuthenticationExcepton : " + kbAuthException.getCause().getClass().toGenericString());
			

			logger.error("KBAuthenticationException :" + failed.getMessage());
			ApiResponse apiResponse = DefaultApiResponseBuilder.defaultApiResponse(profile, messageSource, 
					HttpStatus.UNAUTHORIZED, kbAuthException.getCode(), kbAuthException.getReason(), kbAuthException.getMessage());
			DefaultApiResponseBuilder.responseWrite(response, apiResponse);

			//			responseMsessage(response, kbAuthException);
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}

	}

	private RefreshTokenAuthenticationTokenImpl extract(HttpServletRequest httpServletRequest) {
		RefreshTokenAuthenticationTokenImpl jwtAuthenticationTokenImpl = null;
		String token = httpServletRequest.getHeader(AuthenticationConfig.AUTHORIZATION_HEADER_NAME);
		jwtAuthenticationTokenImpl = new RefreshTokenAuthenticationTokenImpl(token);
		return jwtAuthenticationTokenImpl;
	}

}
