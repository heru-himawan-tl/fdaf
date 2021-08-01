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

import fdaf.base.PasswordResetInterface;
import fdaf.webapp.base.AbstractWebAppBean;
import fdaf.webapp.base.WebAppOpMode;
import fdaf.base.MailerInterface;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;

@ViewScoped
@Named
public class PasswordResetWebAppBean extends AbstractWebAppBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PasswordResetWebAppBean.class.getName());
    
    private static final long serialVersionUID = 1L;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/PasswordResetFacade")
    private PasswordResetInterface facade;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/MailerService")
    private MailerInterface mailer;
    
    private String resetPassword;
    private boolean success;
    private String email;
    private boolean resetting;
    
    private boolean mailerFailure;
    
    @Inject
    private Controller controller;

    public PasswordResetWebAppBean() {
        // NO-OP
    }
    
    protected Controller getController() {
        return controller;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void tryAgain(AjaxBehaviorEvent event) throws AbortProcessingException {
        resetting = false;
        success = false;
    }
    
    public boolean getResetting() {
        return resetting;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    public void tryReset(AjaxBehaviorEvent event) throws AbortProcessingException {
        mailerFailure = false;
        resetting = true;
        if (!mailer.isEnabled()) {
            addMessage(SV_WARN, "mailerDisabledWarning");
            return;
        }
        if (email == null || (email != null && email.isEmpty())) {
            addMessage(SV_WARN, "passwordResetEmailEmpty");
            return;
        }
        if (email != null && !email.isEmpty()
            && !email.matches("^[a-zA-Z0-9\\-\\.\\_]+\\@[a-zA-Z0-9\\-\\.\\_]+$")) {
            addMessage(SV_WARN, "passwordResetEmailInvalid");
            return;
        }
        try {
            if (!facade.findUser(email)) {
                addMessage(SV_WARN, "passwordResetUserNotFound");
                return;
            }
            if (!facade.preUpdateCheck()) {
                addMessage(SV_ERROR, "serviceErrorWarning");
                return;
            }
            if (mailer.isEnabled()) {
                String subject = label.getString("passwordResetMailNotificationSubject");
                String message = String.format(messageBundle.getString("passwordResetMailNotificationMessage"), facade.getUserName(), facade.getPassword());
                if (!mailer.send("noreply@" + mailer.getDomain(), email, subject, message)) {
                    addMessage(SV_ERROR, "mailerErrorWarning");
                    mailerFailure = true;
                    return;
                }
                facade.update();
            } else {
                addMessage(SV_WARN, "mailerDisabledWarning");
                return;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            addMessage(SV_ERROR, "serviceErrorWarning");
            return;
        }
        addMessage(SV_INFO, "passwordResetSuccess");
        success = true;
    }
    
    public boolean getMailerFailure() {
        return mailerFailure;
    }

    @Override
    public void executeCreate(AjaxBehaviorEvent event) throws AbortProcessingException {
        // NO-OP
    }
    
    @Override
    public void executeUpdate(AjaxBehaviorEvent event) throws AbortProcessingException {
        // NO-OP
    }

    protected PasswordResetInterface getFacade() {
        return facade;
    }
}
