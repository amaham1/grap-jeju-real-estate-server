package org.alljeju.alljejuserver.config.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import kr.co.wayplus.travel.model.MenuUser;
//import kr.co.wayplus.travel.model.SettingCompanyInfo;
//import kr.co.wayplus.travel.service.front.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 사용자 정보 기록을 위해 쿠키 기반으로 접속자 고유 아이디를 확인한다.
 */
@Component
public class UserPageCommonInterceptor implements HandlerInterceptor {

//    private final Logger logger = LoggerFactory.getLogger(getClass());
//
//    private final PageService pageService;
//
//    @Value("${cookie-set.domain}")
//    private String cookieDomain;
//    @Value("${cookie-set.prefix}")
//    private String cookiePrefix;
//
//    @Autowired
//    public UserPageCommonInterceptor(PageService pageService) {
//        this.pageService = pageService;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//        logger.debug("================== Start User Page Common Interceptor ==================");
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                    @Nullable ModelAndView modelAndView) throws Exception {
//        try {
//            /*
//             Get 데이터 요청 및 ContentType 이 application이 아닐 경우에만 메뉴, 헤더/팝업 정보 조회
//             */
//        	logger.info("/******* "+ request.getRequestURI() +" *********/");
//        	logger.info("/******* "+ request.getMethod() +" *********/");
//        	logger.info("/******* "+ request.getContentType() +" *********/");
//        	logger.info("/******* "+ modelAndView +" *********/");
//
//        	if( modelAndView != null ) {
//	        	if((request.getMethod().equals("GET") || request.getMethod().equals("POST"))
//	               || (request.getContentType() != null && request.getContentType().startsWith("application"))) {
//	               //&& (request.getContentType() == null || !request.getContentType().startsWith("application"))) {
//		            Cookie[] cookies = request.getCookies();
//		            HashMap<String, Object> param = new HashMap<>();
//		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
//		            String strMenuUserFullUri = request.getRequestURI();
//
//		            param.put("now", sdf.format(new Date()));
//		            List<String> ids = new ArrayList<>();
//		            if(cookies != null)
//		                for (Cookie cookie : cookies) {
//		                    if (cookie.getName().startsWith(cookiePrefix + "popup.")) {
//		                        ids.add(cookie.getName().replace(cookiePrefix + "popup.", ""));
//		                    }
//		                }
//		            param.put("list", ids);
//		            modelAndView.addObject("navbar", pageService.getNavbar());
//
//		            modelAndView.addObject("menuList", pageService.getUserMenuList());
//		            modelAndView.addObject("menuTreeList", pageService.getUserMenuTreeList());
//
//		            MenuUser nowMenu = pageService.getMenuUserFullMenuUrl( strMenuUserFullUri );
//		            modelAndView.addObject("nowMenu", nowMenu );
//
//		            modelAndView.addObject("noticePopupList", pageService.getUserPageNoticePopupList(param));
//		            modelAndView.addObject("noticeBarList", pageService.getUserPageNoticeBarList(param));
//
//		            if( nowMenu!= null && nowMenu.getUpperMenuId() != null) {
//		            	modelAndView.addObject("bannerList", pageService.getListBannerImage( nowMenu.getUpperMenuId() ));
//		            }
//
//		            SettingCompanyInfo company = pageService.getUserPageFooterInfo();
//		            //modelAndView.addObject("footer", company);
//		            modelAndView.addObject("company", company);
//					//cnk전용. 00일안에 생성된 특가상품이 존재하는지 확인.
//					param.put("dayRange", 2);
//					modelAndView.addObject("specialPriceCount", pageService.getCountSpecialPriceProductDayRange(param));
//					param.put("reservationYn", "Y");
//					modelAndView.addObject("categoryInfo", pageService.selectOneInquiryCategory( param ));
//					modelAndView.addObject("policyPrivacy", pageService.selectOnePolicyByPolicyType( "4" ));
//		        }
//        	}
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            e.printStackTrace();
//        }
//        logger.debug("================== End User Page Common Interceptor ==================");
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
//                                 @Nullable Exception ex) throws Exception {
//    }


}
