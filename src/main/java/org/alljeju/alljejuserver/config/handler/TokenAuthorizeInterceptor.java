package org.alljeju.alljejuserver.config.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import kr.co.wayplus.travel.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 사용자 정보 기록을 위해 쿠키 기반으로 접속자 고유 아이디를 확인한다.
 */
@Component
public class TokenAuthorizeInterceptor implements HandlerInterceptor {

//    private final Logger logger = LoggerFactory.getLogger(getClass());
//
//    private final CookieUtil trackingUtil;
//
//    @Value("${cookie-set.tracking}")
//    private boolean cookieTracking;
//
//    @Autowired
//    public TokenAuthorizeInterceptor(CookieUtil trackingUtil) {
//        this.trackingUtil = trackingUtil;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//        logger.debug("================== Start Token Authorize Interceptor ==================");
//
//        if(cookieTracking){
//            //웹사이트 트래킹 로그 작성
//            logger.debug(trackingUtil.writeWebTrackingLog(request, response));
//        }
//
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                    @Nullable ModelAndView modelAndView) throws Exception {
//        logger.debug("================== End Token Authorize Interceptor ==================");
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
//                                 @Nullable Exception ex) throws Exception {
//    }


}
