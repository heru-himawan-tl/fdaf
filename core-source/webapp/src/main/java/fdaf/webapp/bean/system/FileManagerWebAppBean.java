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

import fdaf.base.AddAdministratorInterface;
import fdaf.base.AdministratorAccountCheckerInterface;
import fdaf.base.CommonConfigurationInterface;
import fdaf.base.DatabaseServiceCheckerInterface;
import fdaf.base.FileManagerInterface;
import fdaf.base.UserSessionManagerInterface;
import fdaf.base.UserType;
import fdaf.webapp.base.AbstractBaseWebAppBean;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// UNDER DEVELOPMENT!
@ViewScoped
@Named
public class FileManagerWebAppBean extends AbstractBaseWebAppBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/AdministratorAccountCheckerFacade")
    private AdministratorAccountCheckerInterface rootAccountChecker;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/UserSessionManagerFacade")
    private UserSessionManagerInterface userSessionManager;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/CommonConfigurationService")
    private CommonConfigurationInterface commonConfiguration;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/DatabaseServiceCheckerFacade")
    private DatabaseServiceCheckerInterface dbServiceChecker;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/FileManagerUtil")
    private FileManagerInterface fileManagerUtil;
    
    private LinkedHashMap<String, Map<String, Boolean>> nodes = new LinkedHashMap<String, Map<String, Boolean>>();
    
    @Inject
    private FileManagerDirectoryInfoBean directoryInfo;
    
    @Pattern(regexp = "^[a-zA-Z0-9\\.\\-\\_\\(\\)\\[\\]\\{\\}\\$\\@\\~]+$", message = "{newDirectoryNameBadFormat}")
    @Size(min = 1, max = 128, message = "{newDirectoryNameLengthOutOfRange}")
    @NotBlank(message = "{newDirectoryNameBlank}")
    private String newDirectoryName;
    
    private boolean inPrepareCreateDirectory;
    
    @Inject
    private Controller controller;

    public FileManagerWebAppBean() {
        // NO-OP
    }
    
    protected Controller getController() {
        return controller;
    }
    
    @Override
    protected DatabaseServiceCheckerInterface getDatabaseServiceChecker() {
        return dbServiceChecker;
    }

    protected AdministratorAccountCheckerInterface getAdministratorAccountChecker() {
        return rootAccountChecker;
    }

    protected CommonConfigurationInterface getCommonConfiguration() {
        return commonConfiguration;
    }

    public UserSessionManagerInterface getUserSessionManager() {
        return userSessionManager;
    }
    
    public void toHomeDirectory() {
        fileManagerUtil.toHomeDirectory();
        directoryInfo.setCurrentDirectory(fileManagerUtil.getCurrentDirectory());
    }
    
    public void toParentDirectory() {
        fileManagerUtil.toParentDirectory();
        directoryInfo.setCurrentDirectory(fileManagerUtil.getCurrentDirectory());
    }

    public void populateNodes(ComponentSystemEvent event) throws AbortProcessingException {
        if (directoryInfo.getCurrentDirectory() == null) {
            directoryInfo.setCurrentDirectory(fileManagerUtil.getCurrentDirectory());
        }
        fileManagerUtil.setCurrentDirectory(directoryInfo.getCurrentDirectory());
        directoryInfo.setCurrentDirectory(fileManagerUtil.getCurrentDirectory());
        fileManagerUtil.populateNodes();
        nodes = fileManagerUtil.getNodeMap();
    }
    
    public String toURL(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (Exception e) {
        }
        return src;
    }
    
    public LinkedHashMap<String, Map<String, Boolean>> getNodes() {
        return nodes;
    }
    
    public void setCurrentDirectory(String currentDirectory) {
        directoryInfo.setCurrentDirectory(currentDirectory);
    }
    
    public String getCurrentDirectory() {
        return directoryInfo.getCurrentDirectory();
    }
    
    public void prepareCreateDirectory() {
        inPrepareCreateDirectory = true;
    }
    
    public boolean getInPrepareCreateDirectory() {
        return inPrepareCreateDirectory;
    }
    
    public void setNewDirectoryName(String newDirectoryName) {
        this.newDirectoryName = newDirectoryName;
    }
    
    public String getNewDirectoryName() {
        return newDirectoryName;
    }
    
    public void createNewDirectory() {
        if (!fileManagerUtil.createNewDirectory(newDirectoryName)) {
            addMessage(SV_ERROR, "newDirectoryCreationError");
        }
    }
}
