package org.alljeju.alljejuserver.config;

import org.alljeju.alljejuserver.config.handler.LoginFailureHandler;
import org.alljeju.alljejuserver.config.handler.LoginSuccessHandler;
//import org.alljeju.alljejuserver.config.handler.OAuth2LoginSuccessHandler;
import org.alljeju.alljejuserver.config.handler.SecurityAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	final LoginSuccessHandler loginSuccessHandler;

	final LoginFailureHandler loginFailureHandler;

	final SecurityAccessDeniedHandler accessDeniedHandler;
//	final OAuthUserService oAuthUserService;
//	final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	public SecurityConfig(LoginSuccessHandler loginSuccessHandler, LoginFailureHandler loginFailureHandler, SecurityAccessDeniedHandler accessDeniedHandler
//			, OAuthUserService oAuthUserService
//			, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler
	) {
		this.loginSuccessHandler = loginSuccessHandler;
		this.loginFailureHandler = loginFailureHandler;
		this.accessDeniedHandler = accessDeniedHandler;
//		this.oAuthUserService = oAuthUserService;
//		this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
	}

//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new CustomBcryptPasswordEncoder();
//	}

	@Bean
	static RoleHierarchy roleHierarchy() {
		var hierarchy = new RoleHierarchyImpl();
		hierarchy.setHierarchy("ROLE_MASTER > ROLE_ADMIN\nROLE_ADMIN > ROLE_MANAGER\nROLE_MANAGER > ROLE_STAFF\nROLE_STAFF > ROLE_USER\nROLE_USER > ROLE_GUEST"); //ROLE_ANONYMOUS
		return hierarchy;
	}

	@Bean
	static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy);
		return expressionHandler;
	}

	@Bean
	public HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository(){
		return new HttpSessionCsrfTokenRepository();
	}

	@Bean
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
		HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
		requestCache.setMatchingRequestParameterName("continue");
		http
			.authorizeHttpRequests(
				(authorize) ->
					authorize
						.requestMatchers("/superuser/**").hasAuthority("ROLE_MASTER")
						.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
						.requestMatchers("/manage/**").hasAuthority("ROLE_STAFF")
						.requestMatchers("/member/**").hasAuthority("ROLE_USER")
						.requestMatchers("/crm/**").hasAuthority("ROLE_GUEST")
						.requestMatchers("/order/**").hasAuthority("ROLE_GUEST")
						//.requestMatchers("/qna/**").hasAuthority("ROLE_GUEST")
						.requestMatchers("/user/login").anonymous()
						.requestMatchers("/user/authorize").anonymous()
						.requestMatchers("/oauth2/authorize").anonymous()
						.anyRequest().permitAll()
			)
			.formLogin(
				(formLogin) ->
					formLogin
						.loginPage("/user/login")
						.permitAll()
						.loginProcessingUrl("/user/authorize")
						.usernameParameter("userEmail")
						.passwordParameter("userPassword")
						.successHandler(loginSuccessHandler)
						.failureHandler(loginFailureHandler)
			)
//			.oauth2Login(
//				(oauth2) ->
//					oauth2
//						.loginPage("/user/login")
//						.successHandler(oAuth2LoginSuccessHandler)
//						.authorizationEndpoint(
//							authorization ->
//								authorization.baseUri("/oauth2/authorize")
//						)
//						.redirectionEndpoint(
//							redirect ->
//								redirect.baseUri("/oauth2/authorization/**")
//						)
//						.userInfoEndpoint(
//							userInfo ->
//								userInfo
//									.userService(oAuthUserService)
//						)
//			)
			.logout(
				(logout) ->
					logout
						.logoutUrl("/user/logout")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
			)
			.csrf(
				(csrf) ->
					csrf
						.csrfTokenRepository(httpSessionCsrfTokenRepository())
						.ignoringRequestMatchers("/nicepay/**")
						.ignoringRequestMatchers("/crm/**")
						.ignoringRequestMatchers("/api/bus/**")
			)
			.sessionManagement(
				(session) ->
					session
						.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
						.maximumSessions(1)
						.expiredUrl("/user/expired")
			)
			.requestCache(
				(cache) ->
					cache
						.requestCache(requestCache)
			)
			.exceptionHandling(
				(exception) ->
					exception
							.accessDeniedHandler(accessDeniedHandler)
			);

		return http.build();
	}

}
