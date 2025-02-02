package org.alljeju.alljejuserver.config.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

/**
 * 관리자 사이트의 메뉴정보를 제공하기위함.
 */
@Component
public class MenuMangeInterceptor implements HandlerInterceptor {

//    private final Logger logger = LoggerFactory.getLogger(getClass());
//    ManageService svc;
//
//    @Value("${spring.profiles.project.name}")
//    private String project_name;
//    @Value("${spring.profiles.active}")
//    private String ACTIVE_PROFILE;
//
//    @Autowired
//    public MenuMangeInterceptor(ManageService svc) {
//        this.svc = svc;
//    }
//
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//			ModelAndView modelAndView) throws Exception {
//		HashMap<String, Object> paramMap = new HashMap<String, Object>();
//
//		logger.debug("================== meneu manage Interceptor [[postHandle]] ==================");
//		String nowUri = request.getRequestURI();
//		//logger.debug(nowUri);
//
//		try {
//			if(request.getMethod().equals("GET")
//					&& (request.getContentType() == null || !request.getContentType().startsWith("application"))
//					&& modelAndView != null) {
//				paramMap.put("menuUrl", nowUri);
//				ManageMenu menu = svc.selectOneManageMenu( paramMap );
//
//				if(menu != null) {
//					modelAndView.addObject("nowManageMenu",menu);
//				}
//			}
//			if(modelAndView != null) {
//				if (!"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
//
//					Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//					Integer userAuthId = null;
//
//					if( object instanceof LoginUser ) {
//						LoginUser user = (LoginUser) object;
//
//						if(user.getUserRole().equals("STAFF")) {
//							userAuthId = user.getUserAuthId();
//						}
//
//					} else if( object instanceof OAuthUser ) {
//						OAuthUser user = (OAuthUser) object;
//
//					}
//
//					HashMap<String, Object> retMap = new HashMap<String, Object>();
//					paramMap.clear();
//					paramMap.put("useYn", "Y");
//					svc.selectList(paramMap, retMap, userAuthId);
//					modelAndView.addObject("listTopMenu", retMap.get("data"));
//					modelAndView.addObject("list", retMap.get("list"));
//
//					modelAndView.addObject("projectName",project_name);
//					modelAndView.addObject("activeProfile",ACTIVE_PROFILE);
//					//if(ACTIVE_PROFILE.equals("dev")) { }
//				}
//			}
//		}catch (Exception e){
//			logger.error(e.getMessage());
//			e.printStackTrace();
//		}
//		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//	}
//

}
