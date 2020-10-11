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
package fdaf.webapp.bean.common;

import fdaf.base.AdministratorAccountCheckerInterface;
import fdaf.base.CommonConfigurationInterface;
import fdaf.base.DatabaseServiceCheckerInterface;
import fdaf.base.MailerInterface;
import fdaf.base.StaffInvitationInterface;
import fdaf.base.UserSessionManagerInterface;
import fdaf.logic.entity.StaffInvitation;
import fdaf.webapp.base.AbstractWebAppBean;
import fdaf.webapp.base.WebAppOpMode;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ViewScoped
@Named
public class StaffInvitationWebAppBean extends AbstractWebAppBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(StaffInvitationWebAppBean.class.getName());
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/StaffInvitationFacade")
    private StaffInvitationInterface facade;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/Mailer")
    private MailerInterface mailer;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/AdministratorAccountCheckerFacade")
    private AdministratorAccountCheckerInterface admAccountChecker;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/UserSessionManagerFacade")
    private UserSessionManagerInterface userSessionManager;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/DatabaseServiceCheckerFacade")
    private DatabaseServiceCheckerInterface dbServiceChecker;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/CommonConfiguration")
    private CommonConfigurationInterface commonConfiguration;
    
    private boolean mailerFailure;

    public StaffInvitationWebAppBean() {
        // NO-OP
    }

    protected AdministratorAccountCheckerInterface getAdministratorAccountChecker() {
        return admAccountChecker;
    }
    
    protected CommonConfigurationInterface getCommonConfiguration() {
        return commonConfiguration;
    }

    protected StaffInvitationInterface getFacade() {
        return facade;
    }
    
    @Override
    protected DatabaseServiceCheckerInterface getDatabaseServiceChecker() {
        return dbServiceChecker;
    }

    @Override
    public UserSessionManagerInterface getUserSessionManager() {
        return userSessionManager;
    }
    
    public boolean getMailerEnabled() {
        return mailer.isEnabled();
    }
    
    public void initStaffInvitation(ComponentSystemEvent event) throws AbortProcessingException {
        if (!mailer.isEnabled()) {
            addMessage(SV_WARN, "mailerDisabledWarning");
        }
        if (opMode != WebAppOpMode.CREATE && opMode != WebAppOpMode.UPDATE) {
            prepareCreateMode();
        }
    }
    
    private void prepareCreateMode() {
        opMode = WebAppOpMode.CREATE;
        disableValidation = false;
        exitSearch(null);
        getFacade().prepareCreate();
        presetEntity();
    }
    
    @Override
    public void executeCreate(AjaxBehaviorEvent event) throws AbortProcessingException {
        mailerFailure = false;
        try {
            feedBackEntity();
            if (!getFacade().preCreateCheck()) {
                addCallbackMessage(SV_WARN, getFacade().getMessage());
                return;
            } else {
                getFacade().create();
                getFacade().reloadNewEntity();
                presetEntity();
                feedBackEntity();
                getFacade().postCreateTask();
                opMode = WebAppOpMode.UPDATE;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            addMessage(SV_ERROR, "serviceErrorWarning");
            rollbackCreateTask();
            return;
        }
        try {
            if (mailer.isEnabled()) {
                String message = facade.getInvitaionMessage().trim();
                message = (message.isEmpty()) ?
                    String.format(messageBundle.getString("staffInvitationMailNotificationMessage"), 
                            getWebappURL(), "user-registration.jsf",
                            getWebappURL(), "user-registration.jsf",
                            facade.getInvitaionSignature())
                        : message + " " + String.format(messageBundle.getString("staffInvitationAdditionalMailNotificationMessage"),
                        getWebappURL(), "user-registration.jsf",
                        getWebappURL(), "user-registration.jsf",
                        facade.getInvitaionSignature());
                String subject = facade.getInvitaionSubject().trim();
                subject = (subject.isEmpty()) ? label.getString("staffInvitationMailNotificationSubject") : subject;
                if (!mailer.send("noreply@" + mailer.getDomain(), facade.getEmail(), subject, message)) {
                    indicateMailerFailure();
                    rollbackCreateTask();
                    prepareCreateMode();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            indicateMailerFailure();
            rollbackCreateTask();
            prepareCreateMode();
        }
    }
    
    private void indicateMailerFailure() {
        if (!mailer.isValidAddress()) {
            addMessage(SV_ERROR, "mailerErrorInvalidAddressWarning");
        } else {
            addMessage(SV_ERROR, "mailerErrorWarning");
        }
        mailerFailure = true;
    }
    
    public boolean getMailerFailure() {
        return mailerFailure;
    }
    
    @Override
    public void executeUpdate(AjaxBehaviorEvent event) throws AbortProcessingException {
        // NO-OP
    }
}
