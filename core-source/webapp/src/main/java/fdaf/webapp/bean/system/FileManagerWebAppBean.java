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
import fdaf.base.UserSessionManagerInterface;
import fdaf.base.UserType;
import fdaf.webapp.base.AbstractBaseWebAppBean;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

// UNDER DEVELOPMENT !
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
    
    private boolean error;
    private String baseDirectory;

    public FileManagerWebAppBean() {
        // NO-OP
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
    
    private Map<String, String> directoriesMap = new HashMap<String, String>();
    private Map<String, String> filesMap = new HashMap<String, String>();

    public void populateNodes(ComponentSystemEvent event) throws AbortProcessingException {
        try {
            if (baseDirectory == null) {
                baseDirectory = System.getProperty("user.home");
            }
        } catch (Exception e) {
            error = true;
            return;
        }
        Map<String, String> localDirectoriesShortByName = new HashMap<String, String>();
        Map<String, String> localFilesShortByName = new HashMap<String, String>();
        Map<String, String> localDirectoriesShortByDate = new HashMap<String, String>();
        Map<String, String> localDilesShortByDate = new HashMap<String, String>();
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(baseDirectory));
            for (Path path : directoryStream) {

            }
            directoryStream.close();
        } catch (Exception e) {
        }
    }
}
