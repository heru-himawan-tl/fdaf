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
package fdaf.logic.ejb.services;

import fdaf.base.ApplicationIdentifier;
import fdaf.base.CommonConfigurationInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import javax.activation.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;


@Remote({CommonConfigurationInterface.class})
@Singleton
@Startup
public class CommonConfigurationService extends ApplicationIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(CommonConfigurationService.class.getName());
    
    private String configPath;
    private boolean enabled;
    
    private boolean allowPerUserMultipleLogins;
    private String webSocketClientSecureKey;
    
    private boolean offlineSite;
    private String siteName;
    private String domain;
    private boolean domainAsDefaultSite;
    private String webmasterEmail;
    private String siteDescription;
    private String regionalLanguage;
    private String companyName;
    private String companyDescription;
    private String companyAddress1;
    private String companyAddress2;
    private String companyPhone1;
    private String companyPhone2;
    
    @PostConstruct
    public void initCommonConfiguration() {
    
    	boolean isUNIX = (!System.getProperty("os.name").matches(".*Windows.*"));
        String fsp = File.separator;
        String userHome = null;
        
        try {
            userHome = System.getProperty("user.home");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }
        
        try {
            Path configDirPath = null;
            if (isUNIX) {
                configDirPath = Paths.get(userHome + fsp + "." + getApplicationCodeName());
            } else {
                configDirPath = Paths.get("C:\\" + getApplicationCodeName());
            }
            if (!Files.exists(configDirPath)) {
                Files.createDirectory(configDirPath);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        
        if (!isUNIX) {
            try {
                configPath = "C:\\" + getApplicationCodeName() + "\\" + getApplicationCodeName() + "-common.conf";
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
        } else {
            try {
                configPath = userHome + fsp + "." + getApplicationCodeName() + fsp + getApplicationCodeName() + "-common.conf";
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
            try {
                configPath = "/usr/local/etc/" + getApplicationCodeName() + "/" + getApplicationCodeName() + "-common.conf";
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
            try {
                configPath = "/etc/" + getApplicationCodeName() + "/" + getApplicationCodeName() + "-common.conf";
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
	    }
        
        if (isUNIX) {
            configPath = userHome + fsp + "." + getApplicationCodeName() + fsp + getApplicationCodeName() + "-common.conf";
        } else {
            configPath = "C:\\" + getApplicationCodeName() + "\\" + getApplicationCodeName() + "-common.conf";
        }
    }
    
    public void loadConfig() {
        try {
            InputStream input = new FileInputStream(configPath);
            Properties prop = new Properties();
            prop.load(input);
            
            offlineSite = Boolean.parseBoolean(prop.getProperty("offlineSite"));
            siteName = prop.getProperty("siteName");
            domain = prop.getProperty("domain");
            allowPerUserMultipleLogins = Boolean.parseBoolean(prop.getProperty("allowPerUserMultipleLogins"));
            webSocketClientSecureKey = prop.getProperty("webSocketClientSecureKey");
            domainAsDefaultSite = Boolean.parseBoolean(prop.getProperty("domainAsDefaultSite"));
            webmasterEmail = prop.getProperty("webmasterEmail");
            siteDescription = prop.getProperty("siteDescription");
            regionalLanguage = prop.getProperty("regionalLanguage");
            companyName = prop.getProperty("companyName");
            companyDescription = prop.getProperty("companyDescription");
            companyAddress1 = prop.getProperty("companyAddress1");
            companyAddress2 = prop.getProperty("companyAddress2");
            companyPhone1 = prop.getProperty("companyPhone1");
            companyPhone2 = prop.getProperty("companyPhone2");
                
            input.close();       
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            saveConfig();
            return;
        }
        enabled = true;
    }
    
    public boolean saveConfig() {
        if ((domain != null && domain.isEmpty()) || domain == null) {
            domain = "localhost";
        }
        try {
            FileOutputStream out = new FileOutputStream(configPath);
            Properties prop = new Properties();
            prop.setProperty("offlineSite", (offlineSite) ? "true" : "false");
            prop.setProperty("siteName", siteName);
            prop.setProperty("domain", domain);
            prop.setProperty("domainAsDefaultSite", (domainAsDefaultSite) ? "true" : "false");
            prop.setProperty("allowPerUserMultipleLogins", (allowPerUserMultipleLogins) ? "true" : "false");
            try {
                prop.setProperty("webSocketClientSecureKey", webSocketClientSecureKey);
            } catch (Exception ex) {
            }
            try {
                prop.setProperty("webmasterEmail", webmasterEmail);
            } catch (Exception ex) {
            }
            prop.setProperty("siteDescription", siteDescription);
            prop.setProperty("regionalLanguage", regionalLanguage);
            prop.setProperty("companyName", companyName);
            prop.setProperty("companyDescription", companyDescription);
            prop.setProperty("companyAddress1", companyAddress1);
            prop.setProperty("companyAddress2", companyAddress2);
            prop.setProperty("companyPhone1", companyPhone1);
            prop.setProperty("companyPhone2", companyPhone2);   
            prop.store(out, null);
            out.close();
            loadConfig();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }
    
    public void setOfflineSite(boolean offlineSite) {
        this.offlineSite = offlineSite;
    }

    public boolean getOfflineSite() {
        return offlineSite;
    }
    
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomainAsDefaultSite(boolean domainAsDefaultSite) {
        this.domainAsDefaultSite = domainAsDefaultSite;
    }

    public boolean getDomainAsDefaultSite() {
        return domainAsDefaultSite;
    }
    
    public void setWebSocketClientSecureKey(String webSocketClientSecureKey) {
        this.webSocketClientSecureKey = webSocketClientSecureKey;
    }

    public String getWebSocketClientSecureKey() {
        return webSocketClientSecureKey;
    }
    
    public void setAllowPerUserMultipleLogins(boolean allowPerUserMultipleLogins) {
        this.allowPerUserMultipleLogins = allowPerUserMultipleLogins;
    }
    
    public boolean getAllowPerUserMultipleLogins() {
        return allowPerUserMultipleLogins;
    }

    public void setWebmasterEmail(String webmasterEmail) {
        this.webmasterEmail = webmasterEmail;
    }

    public String getWebmasterEmail() {
        return webmasterEmail;
    }

    public void setSiteDescription(String siteDescription) {
        this.siteDescription = siteDescription;
    }

    public String getSiteDescription() {
        return siteDescription;
    }

    public void setRegionalLanguage(String regionalLanguage) {
        this.regionalLanguage = regionalLanguage;
    }

    public String getRegionalLanguage() {
        return regionalLanguage;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyAddress1(String companyAddress1) {
        this.companyAddress1 = companyAddress1;
    }

    public String getCompanyAddress1() {
        return companyAddress1;
    }

    public void setCompanyAddress2(String companyAddress2) {
        this.companyAddress2 = companyAddress2;
    }

    public String getCompanyAddress2() {
        return companyAddress2;
    }

    public void setCompanyPhone1(String companyPhone1) {
        this.companyPhone1 = companyPhone1;
    }

    public String getCompanyPhone1() {
        return companyPhone1;
    }

    public void setCompanyPhone2(String companyPhone2) {
        this.companyPhone2 = companyPhone2;
    }

    public String getCompanyPhone2() {
        return companyPhone2;
    }
    
    public String getConfigPath() {
        return configPath;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
