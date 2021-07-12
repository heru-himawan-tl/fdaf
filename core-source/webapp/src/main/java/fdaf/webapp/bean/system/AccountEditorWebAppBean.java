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
import fdaf.base.CommonConfigurationInterface;
import fdaf.base.DatabaseServiceCheckerInterface;
import fdaf.base.FacadeInterface;
import fdaf.base.UserSessionManagerInterface;
import fdaf.webapp.base.AbstractWebAppBean;
import fdaf.webapp.base.WebAppOpMode;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ViewScoped
@Named
public class AccountEditorWebAppBean extends AbstractWebAppBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/AccountEditorFacade")
    private FacadeInterface facade;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/AdministratorAccountCheckerFacade")
    private AdministratorAccountCheckerInterface admAccountChecker;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/UserSessionManagerFacade")
    private UserSessionManagerInterface userSessionManager;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/DatabaseServiceCheckerFacade")
    private DatabaseServiceCheckerInterface dbServiceChecker;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/CommonConfigurationService")
    private CommonConfigurationInterface commonConfiguration;

    public AccountEditorWebAppBean() {
        // NO-OP
    }
    
    public void initAccountEditor(ComponentSystemEvent event) throws AbortProcessingException {
        if (loggedOn && opMode != WebAppOpMode.UPDATE) {
            primaryKey = userSessionManager.getUserId();
            prepareUpdate(primaryKey);
        }
    }

    protected AdministratorAccountCheckerInterface getAdministratorAccountChecker() {
        return admAccountChecker;
    }
    
    protected CommonConfigurationInterface getCommonConfiguration() {
        return commonConfiguration;
    }

    protected FacadeInterface getFacade() {
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
}
