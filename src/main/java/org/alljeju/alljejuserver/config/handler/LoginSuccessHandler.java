package org.alljeju.alljejuserver.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 로그인 성공 시 데이터 처리
 */
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

//    private final UserService userService;
//    private final UserPointService userPointService;
//    private final CookieUtil cookieUtil;
//    private final Logger logger = LoggerFactory.getLogger(getClass());
//
//    private RequestCache requestCache = new HttpSessionRequestCache();
//
//    @Value("${cookie-set.domain}")
//    private String cookieDomain;
//    @Value("${cookie-set.prefix}")
//    private String cookiePrefix;
//    @Value("${cookie-set.tracking-age}")
//    private int trackerMaxAge;
//
//    @Autowired
//    public LoginSuccessHandler(UserService userService, UserPointService userPointService, CookieUtil cookieUtil) {
//        this.userService = userService;
//        this.userPointService = userPointService;
//        this.cookieUtil = cookieUtil;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        HttpSession session = request.getSession();
//        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        try {
//            if (user.getUserBirthday() != null || !user.getUserBirthday().equals("")) {
//                int currentYear =  Calendar.getInstance().get(Calendar.YEAR);
//                user.setUserAge(String.valueOf(currentYear - Integer.parseInt(user.getUserBirthday())));
//            }
//        }catch (Exception e){
//            logger.error(e.getMessage());
//            e.printStackTrace();
//        }
//
//        logger.debug("Login Success. User id : " + user.getUserEmail() + ", Session Id : " + session.getId());
//        //세션 및 DB에 로그인 정보 저장
//        user.setUserPassword("");
//        session.setAttribute("login", user);
//        session.setAttribute("loginType", "form");
//
//
//        SavedRequest savedRequest = requestCache.getRequest(request, response);
//
//        HashMap<String, String> parameterMap = new HashMap<>();
//        parameterMap.put("userEmail", user.getUserEmail());
//        parameterMap.put("sessionId", session.getId());
//        parameterMap.put("loginIp", IPAddrUtil.getClientIpAddr(request));
//        parameterMap.put("userAgent", request.getHeader("User-Agent"));
//        if(savedRequest != null) parameterMap.put("referer", savedRequest.getRedirectUrl());
//        else parameterMap.put("referer", request.getHeader("Referer"));
//
//        logger.debug("Write Login Log... Param: " + parameterMap);
//
//        //로그인 로그 저장
//        userService.writeUserLoginLog(parameterMap);
//
//        //마지막 로그인 일자 업데이트
//        userService.updateUserLastLoginDate(user);
//
//        //로그인 포인트 적용
//        userPointService.createLoginPoint(user);
//
//        //추적용 쿠키 아이디 업데이트
//        Cookie trackingCookie = cookieUtil.getCookieByName(request, "tracker.id");
//        if(trackingCookie != null){
//            if(!trackingCookie.getValue().equals(user.getUserTokenId())){
//                HashMap<String, String> param = new HashMap<>();
//                param.put("before", trackingCookie.getValue());
//                param.put("after", user.getUserTokenId());
//                userService.updateUserWebLog(param);
//            }
//        }
//
//        //아이디 저장 체크 확인
//        Cookie loginCookie = null;
//        if(request.getParameter("remember") != null
//            && request.getParameter("remember").equals("Y")){
//            loginCookie = cookieUtil.createCookie("login.id", user.getUserEmail(), 60*60*24*90);
//        }else{
//            loginCookie = cookieUtil.createCookie("login.id", "", -1);
//        }
//        response.addCookie(loginCookie);
//
//        //로그인 전 요청페이지로 리다이렉션
//        if(savedRequest != null){
//        //if(savedRequest != null && !savedRequest.getRedirectUrl().contains("/user/")){
//            logger.debug("Redirect Saved URL : " + savedRequest.getRedirectUrl());
//            response.sendRedirect(savedRequest.getRedirectUrl());
//        }else{
//        	String url = request.getParameter("redirect");
//        	//System.out.println( url );
//        	if(url != null) {
//        		response.sendRedirect( url );
//        	}else {
//        		response.sendRedirect("/");
//        	}
//
//        }
//
//    }
}
