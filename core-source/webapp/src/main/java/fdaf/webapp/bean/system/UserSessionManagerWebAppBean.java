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
package fdaf.webapp.bean.system;

import fdaf.base.AdministratorAccountCheckerInterface;
import fdaf.base.FacadeInterface;
import fdaf.base.UserSessionManagerInterface;
import fdaf.webapp.base.AbstractWebAppBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;

@ViewScoped
@Named
public class UserSessionManagerWebAppBean extends AbstractWebAppBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/AdministratorAccountCheckerFacade")
    private AdministratorAccountCheckerInterface rootAccountChecker;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/UserSessionManagerFacade")
    private UserSessionManagerInterface userSessionManager;
    
    private boolean inProcessLogin;
    private boolean loginRefused;
    private String password;
    private String referer;
    private String userName;
    private boolean keepLogin;

    public UserSessionManagerWebAppBean() {
        // NO-OP
    }

    protected AdministratorAccountCheckerInterface getAdministratorAccountChecker() {
        return rootAccountChecker;
    }

    @Override
    protected void postConstructTask() {
        String ref = getRequest().getHeader("Referer");
        if (ref != null) {
            referer = (ref.matches(".*password-reset\\..*")) ? "index.jsf" : ref;
        } else {
            referer = "index.jsf";
        }
    }

    @Override
    public UserSessionManagerInterface getUserSessionManager() {
        return userSessionManager;
    }

    protected FacadeInterface getFacade() {
        return null;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    
    public void setKeepLogin(boolean keepLogin) {
        this.keepLogin = keepLogin;
    }

    public boolean getKeepLogin() {
        return keepLogin;
    }

    public boolean getIsInProcessLogin() {
        return inProcessLogin;
    }

    public String getReferer() {
        return referer;
    }

    public void reset(AjaxBehaviorEvent event) throws AbortProcessingException {
        loginRefused = false;
    }

    public void login(AjaxBehaviorEvent event) throws AbortProcessingException {
        String userAgent = getRequest().getHeader("User-Agent");
        inProcessLogin = true;
        loggedOn = false;
        loginRefused = false;
        try {
            if (userSessionManager.login(userName, password, userAgent)) {
                userSessionID = userSessionManager.getUserSessionID();
                if (!presetUserSessionIDCookie(userSessionID, (keepLogin) ? 24*365*3600 : -1)) {
                    userSessionManager.rollbackLogin(userSessionID, userAgent);
                    addMessage(SV_WARN, "loginRefused");
                    return;
                }
                fetchAccountId();
                addMessage(SV_INFO, "loginAcceptedInfo");
                loggedOn = true;
            } else {
                addMessage(SV_WARN, "loginRefused");
                loginRefused = true;
            }
        } catch (Exception e) {
            addMessage(SV_ERROR, "serviceErrorWarning");
            loginRefused = true;
        }
    }

    public boolean getIsloginRefused() {
        return loginRefused;
    }

    public void logout(ComponentSystemEvent event) throws AbortProcessingException {
        AjaxBehaviorEvent e = null;
        logout(e);
    }

    public void logout(AjaxBehaviorEvent event) throws AbortProcessingException {
        String userSessionID = getUserSessionIDCookie();
        if ((userSessionID == null) && !loggedOn) {
            addMessage(SV_WARN, "logoutNoUserSessionWarning");
            return;
        }
        if ((userSessionID == null) && loggedOn) {
            addMessage(SV_WARN, "logoutFailed");
            return;
        }
        try {
            if (userSessionManager.logout(userSessionID, getRequest().getHeader("User-Agent"))
                    && presetUserSessionIDCookie("", 0)) {
                addMessage(SV_INFO, "logoutSucces");
                brokenUserSession = false;
                this.userSessionID = null;
                loggedOn = false;
            } else {
                addMessage(SV_WARN, "logoutFailed");
            }
        } catch (Exception e) {
            addMessage(SV_ERROR, "serviceErrorWarning");
        }
    }

    @Override
    public void executeCreate(AjaxBehaviorEvent event) throws AbortProcessingException {
        // NO-OP
    }

    @Override
    public void executeUpdate(AjaxBehaviorEvent event) throws AbortProcessingException {
        // NO-OP
    }
}
