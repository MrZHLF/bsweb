package com.lj.app.bsweb.flows.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lj.app.core.common.security.CmSecurityContext;
import com.lj.app.core.common.security.SecurityConstants;
import com.lj.app.core.common.util.AjaxResult;
import com.lj.app.core.common.util.StringUtil;
import com.lj.app.core.common.web.Struts2Utils;

/**
 * 自定义权限验证
 *
 */
public class SecurityFilterExt implements Filter {

  private static Log logger = LogFactory.getLog(SecurityFilterExt.class);

  private String isNeedAuthen;

  private Set<String> skipValidatePageSet = new HashSet<String>();

  private Set<String> skipValidateDirSet = new HashSet<String>();

  private Set<String> skipSessionValidateDirSet = new HashSet<String>();

  private Set<String> needValidateUrlSet;// uap_permission中需要验证的url集合

  private Set<String> disabledAccessUrlSet;// uap_permission中不可访问的url集合

  /**
   * 初始化
   */
  public void init(FilterConfig filterConfig) {
    isNeedAuthen = filterConfig.getInitParameter("isNeedAuthen");
    String contextPath = filterConfig.getInitParameter("contextPath");

    String skipPrivilegeValidatePage = filterConfig.getInitParameter("skipPrivilegeValidatePage");

    if (StringUtil.isNotBlank(skipPrivilegeValidatePage)) {
      String[] urls = skipPrivilegeValidatePage.split(",");
      for (int i = 0; i < urls.length; i++) {
        skipValidatePageSet.add(contextPath + urls[i].trim());
      }
    }
    String skipPrivilegeValidateDir = filterConfig.getInitParameter("skipPrivilegeValidateDir");
    if (StringUtil.isNotBlank(skipPrivilegeValidateDir)) {
      String[] dirs = skipPrivilegeValidateDir.split(",");
      for (int i = 0; i < dirs.length; i++) {
        skipValidateDirSet.add(contextPath + dirs[i].trim());
      }
    }
    String skipSessionValidate = filterConfig.getInitParameter("skipSessionValidate");
    if (StringUtil.isNotBlank(skipSessionValidate)) {
      String[] pages = skipSessionValidate.split(",");
      for (int i = 0; i < pages.length; i++) {
        skipSessionValidateDirSet.add(contextPath + pages[i].trim());
      }
    }

    // FIXME:查询获取权限校验信息 加载uap_permission中需要验证的url集合
   // UpmPermissionService upmPermissionService = SpringContextHolder.getBean(UpmPermissionService.class);
    logger.info("needValidateUrlSet load......");
   // needValidateUrlSet = upmPermissionService.findPermissionUrlByAppId("FLOWS");

    logger.info("needValidateUrlSet load success");
    // 加载uap_permission中不可访问的url集合
    logger.info("disabledAccessUrlSet load......");
  //  disabledAccessUrlSet = upmPermissionService.findDisabledPermissionUrlByAppId("FLOWS");
    logger.info("disabledAccessUrlSet load success");
  }

  /**
   * 过滤 
   */
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
    try {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;
      HttpSession session = request.getSession();

      String requestUri = request.getRequestURI().toString();
      String contextPath = request.getContextPath();
      CmSecurityContext securityContext = (CmSecurityContext) session.getAttribute(SecurityConstants.SECURITY_CONTEXT);

      // 如果用户已经登录，那么就直接到首页，防止重复登录
      if (requestUri.equals(contextPath + "/login.jsp")) {
        if (securityContext != null) {
          response.sendRedirect(contextPath + "/loginAction!goToIndex.action");
          return;
        }
      }

      // session验证
      boolean sessionValidate = validateSession(securityContext, requestUri, contextPath);

      if (!sessionValidate ) {
        response.sendRedirect(contextPath + "/jsp/common/sessionexpire.jsp");
        return;
      }
      if (!sessionValidate && isAjax(request)) {
        AjaxResult ar = new AjaxResult();
        ar.setOpResult("登陆超时");
        Struts2Utils.renderJson(ar);
        return;
      }

      // 权限验证
      if (isNeedAuthen.equals("true")) {
        boolean permissionValidate = validatePermission(securityContext, requestUri, contextPath);

        if (!permissionValidate && !isAjax(request)) {
          response.sendRedirect(contextPath + "/jsp/common/nopermission.jsp");
          return;
        }
        if (!permissionValidate && isAjax(request)) {
          AjaxResult ar = new AjaxResult();
          ar.setOpResult("对不起,您没有操作权限");
          Struts2Utils.renderJson(ar);
          return;
        }
      }

      String queryStr = request.getQueryString();
      String fullRequestUri = contextPath + requestUri;
      if (null != queryStr && !"".equals(queryStr)) {
        fullRequestUri += "?" + queryStr;
      }

      if (needValidateUrlSet != null) {
        for (Iterator iter = needValidateUrlSet.iterator(); iter.hasNext();) {
          String url = contextPath + (String) iter.next();
          if (fullRequestUri.indexOf(url) != -1) {
            if (!securityContext.hasUrlPermission(url) && !isAjax(request)) {
              response.sendRedirect(contextPath + "/jsp/common/nopermission.jsp");
              return;
            }
            if (!securityContext.hasUrlPermission(url) && isAjax(request)) {
              AjaxResult ar = new AjaxResult();
              ar.setOpResult("对不起,您没有操作权限");
              Struts2Utils.renderJson(ar);
              return;
            }
          }
        }
      }

      filterChain.doFilter(servletRequest, servletResponse);

    } catch (ServletException sx) {
    	 logger.error("[" + this.getClass().getName() + "] 异常信息:" + sx);

      logger.error(sx);

    } catch (IOException iox) {
      logger.error(iox);
    }

  }

  /**
   * 功能权限验证
   * 
   * @param securityContext 权限上下文
   * @return 是否
   */
  private boolean validatePermission(CmSecurityContext securityContext, String requestUri, String contextPath) {
	  
	 if(securityContext==null){
		 return true;
	 }
	 
    if (!isSkipValidate(requestUri)) {
      if (!securityContext.hasUrlPermission(requestUri)) {
        return false;
      }
    }

    return true;
  }

  /**
   * 验证session
   * 
   * @param securityContext 权限上下文
   * @param contextPath  上下文
   * @param requestUri url
   * @return 是否
   */
  private boolean validateSession(CmSecurityContext securityContext, String requestUri, String contextPath) {
    if (!isSkipSessionValidate(requestUri)) {
      if (securityContext == null) {
        return false;
      }
    }

    return true;
  }

  private boolean isSkipSessionValidate(String requestUri) {
    for (Iterator iterator = skipSessionValidateDirSet.iterator(); iterator.hasNext();) {
      String page = (String) iterator.next();
      if (requestUri.indexOf(page) != -1) {
        return true;
      }

    }

    return false;
  }

  private boolean isSkipValidate(String requestUri) {
    // 先根据目录过滤
    for (Iterator iterator = skipValidateDirSet.iterator(); iterator.hasNext();) {
      String dir = (String) iterator.next();
      if (requestUri.indexOf(dir) != -1) {
        return true;
      }

    }

    // 再过滤具体请求
    if (skipValidatePageSet.contains(requestUri)) {
      return true;
    }

    return false;
  }

  /**
   * 校验是否是ajax请求
   * 
   * @param request 请求对象
   * @return 是否
   */
  private boolean isAjax(HttpServletRequest request) {
    if (request != null && request.getHeader("x-requested-with") != null
        && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
      return true;
    }
    return false;
  }

  public void destroy() {

  }

  public String getIsNeedAuthen() {
    return isNeedAuthen;
  }

  public void setIsNeedAuthen(String isNeedAuthen) {
    this.isNeedAuthen = isNeedAuthen;
  }

  public Set<String> getSkipValidatePageSet() {
    return skipValidatePageSet;
  }

  public void setSkipValidatePageSet(Set<String> skipValidatePageSet) {
    this.skipValidatePageSet = skipValidatePageSet;
  }

  public Set<String> getSkipValidateDirSet() {
    return skipValidateDirSet;
  }

  public void setSkipValidateDirSet(Set<String> skipValidateDirSet) {
    this.skipValidateDirSet = skipValidateDirSet;
  }

  public Set<String> getSkipSessionValidateDirSet() {
    return skipSessionValidateDirSet;
  }

  public void setSkipSessionValidateDirSet(Set<String> skipSessionValidateDirSet) {
    this.skipSessionValidateDirSet = skipSessionValidateDirSet;
  }

  public Set<String> getNeedValidateUrlSet() {
    return needValidateUrlSet;
  }

  public void setNeedValidateUrlSet(Set<String> needValidateUrlSet) {
    this.needValidateUrlSet = needValidateUrlSet;
  }

  public Set<String> getDisabledAccessUrlSet() {
    return disabledAccessUrlSet;
  }

  public void setDisabledAccessUrlSet(Set<String> disabledAccessUrlSet) {
    this.disabledAccessUrlSet = disabledAccessUrlSet;
  }
  
}