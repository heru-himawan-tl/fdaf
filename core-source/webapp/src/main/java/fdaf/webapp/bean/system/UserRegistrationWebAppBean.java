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
import fdaf.base.DatabaseServiceCheckerInterface;
import fdaf.base.MailerInterface;
import fdaf.base.UserRegistrationInterface;
import fdaf.base.UserSessionManagerInterface;
import fdaf.base.UserType;
import fdaf.webapp.base.AbstractWebAppBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ViewScoped
@Named
public class UserRegistrationWebAppBean extends AbstractWebAppBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(UserRegistrationWebAppBean.class.getName());
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/AdministratorAccountCheckerFacade")
    private AdministratorAccountCheckerInterface admAccountChecker;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/UserSessionManagerFacade")
    private UserSessionManagerInterface userSessionManager;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/DatabaseServiceCheckerFacade")
    private DatabaseServiceCheckerInterface dbServiceChecker;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/UserRegistrationFacade")
    private UserRegistrationInterface facade;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/Mailer")
    private MailerInterface mailer;
    
    private UserType userType;
    
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Invalid format for user name (allowed: a-z, A-Z, 0-9 and spaces).")
    @Size(min = 4, max = 128, message = "User name length out of range (min = 4, max = 128).")
    @NotBlank(message = "User name not specified.")
    private String userName;
    
    @Size(min = 8, max = 512, message = "The password length out of range (min = 8, max = 512).")
    @NotBlank(message = "Password is blank.")
    private String password;
    
    private String passwordConfirm;
    
    @Pattern(regexp = "^[a-zA-Z0-9\\-\\.]+@[a-zA-Z0-9\\-\\.]+$", message = "Invalid format of e-mail address.")
    @NotBlank(message = "E-mail address not specified.")
    private String email;
    
    private String signature;
    
    private String referer;
    
    private boolean success;

    public UserRegistrationWebAppBean() {
        // NO-OP
    }
    
    protected AdministratorAccountCheckerInterface getAdministratorAccountChecker() {
        return admAccountChecker;
    }

    @Override
    protected DatabaseServiceCheckerInterface getDatabaseServiceChecker() {
        return dbServiceChecker;
    }

    @Override
    public UserSessionManagerInterface getUserSessionManager() {
        return userSessionManager;
    }

    @Override
    protected void postConstructTask() {
        referer = getRequest().getHeader("Referer");
    }

    protected UserRegistrationInterface getFacade() {
        return facade;
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
    
    public void setPasswordConfirm(String password) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public String getReferer() {
        return referer;
    }
    
    public boolean getSuccess() {
        return success;
    }

    public void register(AjaxBehaviorEvent event) throws AbortProcessingException {
        try {
            if (userType == UserType.STAFF && !facade.validateInvitation(email, signature)) {
                addMessage(SV_WARN, "invalidInvitationSignature");
                success = false;
                return;
            }
            if (facade.findUserByName(userName)) {
                addMessage(SV_WARN, "userByNameExists");
                success = false;
                return;
            }
            if (facade.findUserByEmail(email)) {
                addMessage(SV_WARN, "userByEmailExists");
                success = false;
                return;
            }
            if (!facade.register(userType, userName, password, email)) {
                addMessage(SV_ERROR, facade.getCustomMessage());
                return;
            }
            facade.reloadEntity();
            if (mailer.isEnabled()) {
                String subject = label.getString("userRegistrationNotificationSubject");
                String message = String.format(messageBundle.getString("userRegistrationNotificationMessage"), getWebappURL(), getWebappURL(), userName, password);
                if (!mailer.send("noreply@" + mailer.getDomain(), email, subject, message)) {
                    rollbackOnError();
                    return;
                }
            } else {
                addMessage(SV_WARN, "mailerDisabledWarning");
                success = false;
                rollbackCreateTask();
                return;
            }            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            rollbackOnError();
            return;
        }
        success = true;
    }
    
    private void rollbackOnError() {
        if (!mailer.isValidAddress()) {
            addMessage(SV_ERROR, "mailerErrorInvalidAddressWarning");
        } else {
            addMessage(SV_ERROR, "mailerErrorWarning");
        }
        success = false;
        rollbackCreateTask();
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
