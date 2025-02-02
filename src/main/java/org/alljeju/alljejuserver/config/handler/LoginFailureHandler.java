package org.alljeju.alljejuserver.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

//    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

//    @Autowired
//    public LoginFailureHandler(UserService userService) {
//        this.userService = userService;
//    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.debug("Login Failed. " + exception.getMessage());

        RequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);
//        LoginAttemptLog attemptLog = new LoginAttemptLog();
//        attemptLog.setUserEmail(request.getParameter("userEmail"));
//        attemptLog.setAttemptIp(IPAddrUtil.getClientIpAddr(request));
//        attemptLog.setAttemptAgent(request.getHeader("User-Agent"));
//        if(savedRequest != null) attemptLog.setAttemptReferer(savedRequest.getRedirectUrl());
//        else attemptLog.setAttemptReferer(request.getHeader("Referer"));

        String errorCode = "n";
        if(exception instanceof AuthenticationServiceException){
            errorCode = "NON";
//            attemptLog.setErrorMessage("등록된 아이디가 아닙니다.");
        } else if (exception instanceof BadCredentialsException) {
            errorCode = "PAS";
//            attemptLog.setErrorMessage("비밀번호가 일치하지 않습니다.");
        } else if (exception instanceof LockedException) {
            errorCode = "LOC";
//            attemptLog.setErrorMessage("잠긴 사용자 아이디입니다.");
        } else if (exception instanceof DisabledException) {
            errorCode = "DIS";
//            attemptLog.setErrorMessage("휴면상태의 아이디입니다.");
        } else if (exception instanceof AccountExpiredException) {
            errorCode = "EXD";
//            attemptLog.setErrorMessage("만료된 아이디입니다.");
        } else if (exception instanceof CredentialsExpiredException) {
            errorCode = "EXP";
//            attemptLog.setErrorMessage("비밀번호가 만료됐습니다.");
        }
//        attemptLog.setErrorCode(errorCode);

        try{
            HttpSession session = request.getSession();
            if(session.getAttribute("login") != null) session.removeAttribute("login");
//            userService.writeUserLoginAttemptLog(attemptLog);
        } catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath()+"/user/login?error=y&code="+errorCode);
    }
}
