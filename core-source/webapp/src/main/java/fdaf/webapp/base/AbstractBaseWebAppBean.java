/*
 * Copyright (c) Heru Himawan Tejo Laksono. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fdaf.webapp.base;

import fdaf.base.AdministratorAccountCheckerInterface;
import fdaf.base.CommonConfigurationInterface;
import fdaf.base.DatabaseServiceCheckerInterface;
import fdaf.base.FacadeInterface;
import fdaf.base.UserSessionManagerInterface;
import fdaf.base.UserType;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public abstract class AbstractBaseWebAppBean extends AbstractWebAppCommon {

    private static final Logger LOGGER = Logger.getLogger(AbstractBaseWebAppBean.class.getName());
    protected static final FacesMessage.Severity SV_ERROR = FacesMessage.SEVERITY_ERROR;
    protected static final FacesMessage.Severity SV_INFO = FacesMessage.SEVERITY_INFO;
    protected static final FacesMessage.Severity SV_WARN = FacesMessage.SEVERITY_WARN;
    protected Long modifierId = 1L;
    protected Long authorId = 1L;
    protected Long userGroupId = 1L;
    protected boolean administratorAccountExists;
    protected boolean loggedOn;
    protected boolean userSessionRequired;
    protected boolean brokenUserSession;
    protected boolean isMultipart;
    protected String userSessionID;
    protected boolean serviceIsError;
    protected UserType userType = UserType.NONE;
    protected boolean forAdministratorOnly;
    protected String enctype = "application/x-www-form-urlencoded";
    protected String requestAddress;
    protected String websocketURL;
    protected String requestURL;
    protected String requestScheme;
    protected String contextPath;
    protected String webappURL;
    protected int requestPort;
    protected Part dummyFile;
    protected Object dummy;
    protected String viewLayerName;
    protected boolean databaseIsError;

    protected AbstractBaseWebAppBean() {
        // NO-OP
    }
    
    protected abstract CommonConfigurationInterface getCommonConfiguration();
    
    protected void addCustomCallbackMessage(FacesMessage.Severity severity, String key, String addInfo) {
        try {
            String content = String.format("%s %s", customCallbackMessageBundle.getString(key), addInfo).trim();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, content, content));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, key, key));
        }
    }

    protected void addCustomCallbackMessage(FacesMessage.Severity severity, String key) {
        addCustomCallbackMessage(severity, key, "");
    }

    protected void addCallbackMessage(FacesMessage.Severity severity, String key, String addInfo) {
        try {
            String content = String.format("%s %s", callbackMessageBundle.getString(key), addInfo).trim();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, content, content));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, key, key));
        }
    }

    protected void addCallbackMessage(FacesMessage.Severity severity, String key) {
        addCallbackMessage(severity, key, "");
    }
    
    protected void addCustomMessage(FacesMessage.Severity severity, String key, String addInfo) {
        try {
            String content = String.format("%s %s", customMessageBundle.getString(key), addInfo).trim();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, content, content));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, key, key));
        }
    }

    protected void addCustomMessage(FacesMessage.Severity severity, String key) {
        addCustomMessage(severity, key, "");
    }

    protected void addMessage(FacesMessage.Severity severity, String key, String addInfo) {
        try {
            String content = String.format("%s %s", messageBundle.getString(key), addInfo).trim();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, content, content));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, key, key));
        }
    }

    protected void addMessage(FacesMessage.Severity severity, String key) {
        addMessage(severity, key, "");
    }

    protected abstract AdministratorAccountCheckerInterface getAdministratorAccountChecker();
    public abstract UserSessionManagerInterface getUserSessionManager();
    
    protected DatabaseServiceCheckerInterface getDatabaseServiceChecker() {
        return null;
    }
    
    public void checkDatabaseService(ComponentSystemEvent event) throws AbortProcessingException {
        try {
            if (getDatabaseServiceChecker() != null) {
                getDatabaseServiceChecker().triggerCheck();
            }
        } catch (Exception e) {
            indicateServiceError(e);
            databaseIsError = true;
        }
    }
    
    public boolean getDatabaseIsError() {
        return databaseIsError;
    }
    
    protected void indicateServiceError(Throwable t) {
        LOGGER.log(Level.SEVERE, "Service error", t);
        addMessage(SV_ERROR, "serviceErrorWarning");
        serviceIsError = true;
    }
    
    public boolean getIsServiceError() {
        return serviceIsError;
    }
    
    public void initMain(ComponentSystemEvent event) throws AbortProcessingException {
        HttpServletRequest request = getRequest();
        requestURL = request.getRequestURL().toString();
        websocketURL = requestURL.replaceAll("^http", "ws").replaceAll("\\/[a-zA-Z0-9\\-\\_]+\\.(jsf|xhtml).*", "").replaceAll("[\\/]+$", "");
        viewLayerName = requestURL.replaceAll(".*\\/", "").replaceAll("\\.(jsf|xhtml).*", "");
        String domain = getCommonConfiguration().getDomain();
        requestAddress = websocketURL.replaceAll("ws(s)?\\:\\/\\/", "").replaceAll("([\\/]+.*|\\:.*)", "");
        requestPort = request.getLocalPort();
        if (!requestAddress.matches("^(localhost|127\\..*|0\\.0\\.0\\.0|10\\..*|192\\.168\\..*|172\\.(1[6-9]|2[0-9]|3[0-1])\\..*)$")
            && getCommonConfiguration().isEnabled() && domain != null && !domain.isEmpty() && !domain.equals("localhost")
            && getCommonConfiguration().getDomainAsDefaultSite()) {
            websocketURL = websocketURL.replaceAll("(^.*\\:\\/\\/)(.*)(\\:[0-9]+)?(\\/.*)", "$1" + domain + ((requestPort != 80 && requestPort != 443) ? ":" + requestPort : "") + "$4");
        }
        requestScheme = requestURL.replaceAll("\\:.*", "");
        contextPath = request.getContextPath();
        webappURL = requestScheme + "://" + requestAddress + ((requestPort != 80 && requestPort != 443) ? ":" + requestPort : "") + contextPath;
    }
    
    public String getViewLayerName() {
        return viewLayerName;
    }

    public void checkAdministratorAccount(ComponentSystemEvent event) throws AbortProcessingException {
        try {
            if (getAdministratorAccountChecker() != null) {
                administratorAccountExists = getAdministratorAccountChecker().isAdministratorAccountExists();
            }
        } catch (Exception e) {
            indicateServiceError(e);
            administratorAccountExists = true;
        }
    }
    
    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }
    
    public String getRequestAddress() {
        return requestAddress;
    }
    
    public String getRequestScheme() {
        return requestScheme;
    }
    
    public String getContextPath() {
        return contextPath;
    }
    
    public String getRequestURL() {
        return requestURL;
    }

    public String getWebsocketURL() {
        return websocketURL;
    }
    
    public int getRequestPort() {
        return requestPort;
    }
    
    public String getWebappURL() {
        return webappURL;
    }
    
    public void setForAdministratorOnly(ComponentSystemEvent event) throws AbortProcessingException {
        forAdministratorOnly = true;
    }

    public boolean getForAdministratorOnly() {
        return forAdministratorOnly;
    }
    
    public boolean getAllowRender() {
        return (userType == UserType.ADMINISTRATOR && forAdministratorOnly) || !forAdministratorOnly;
    }

    public void setRequireUserSession(ComponentSystemEvent event) throws AbortProcessingException {
        userSessionRequired = true;
    }

    public boolean getIsUserSessionRequired() {
        return userSessionRequired;
    }

    public boolean getAdministratorAccountExists() {
        return administratorAccountExists;
    }
    
    protected String getUserSessionIDCookie() {
        Map<String, Object> cookieMap = FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap();
        for (String k : cookieMap.keySet()) {
            Cookie cookie = (Cookie) cookieMap.get(k);
            if (cookie.getName().equals(USER_SESSION_ID_FIELD_NAME)) {
                return cookie.getValue().trim();
            }
        }
        return null;
    }
    
    protected void fetchAccountId() {
        userGroupId = getUserSessionManager().getUserGroupId();
        authorId = getUserSessionManager().getAuthorId();
        userType = getUserSessionManager().getUserType();
        modifierId = getUserSessionManager().getModifierId();
    }

    public void watchSession(ComponentSystemEvent event) throws AbortProcessingException {
        String userSessionIDCookie = getUserSessionIDCookie();
        userSessionID = ((userSessionIDCookie != null) && !userSessionIDCookie.isEmpty()) ? userSessionIDCookie : userSessionID;
        loggedOn = false;
        userType = UserType.NONE;
        if (userSessionID == null) {
            return;
        }
        try {
            getUserSessionManager().watchSession(userSessionID, getRequest().getHeader("User-Agent"));
        } catch (Exception e) {
            indicateServiceError(e);
            return;
        }
        loggedOn = getUserSessionManager().isLoggedOn();
        brokenUserSession = (loggedOn && userSessionIDCookie == null);
        if (!loggedOn && userSessionIDCookie != null) {
            presetUserSessionIDCookie("", 0);
        }
        if (loggedOn) {
            fetchAccountId();
        }
    }
    
    protected boolean presetUserSessionIDCookie(String userSessionID, Integer maxAge) {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("path", getRequest().getContextPath());
            properties.put("maxAge", maxAge);
            externalContext.addResponseCookie(USER_SESSION_ID_FIELD_NAME, userSessionID, properties);
            return true;
        } catch (Exception e) {
            addMessage(SV_ERROR, "presetUserSessionIDCookieFailed");
        }
        return false;
    }
    
    public UserType getUserType() {
        return userType;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Long getUserGroupId() {
        return userGroupId;
    }

    public boolean getIsBrokenUserSession() {
        return brokenUserSession;
    }

    public boolean getIsLoggedOn() {
        return loggedOn;
    }
    
    public void useMultipartForm(ComponentSystemEvent event) throws AbortProcessingException {
        enctype = "multipart/form-data";
        isMultipart = true;
    }
    
    public String getEnctype() {
        return enctype;
    }
    
    public void setDummyFile(Part dummyFile) {
        this.dummyFile = dummyFile;
    }
    
    public Part getDummyFile() {
        return dummyFile;
    }
    
    public void setDummy(Object dummy) {
        this.dummy = dummy;
    }
    
    public Object getDummy() {
        return dummy;
    }
    
    public void deinit(ComponentSystemEvent event) throws AbortProcessingException {
        // NO-OP
    }
}
