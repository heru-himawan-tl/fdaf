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
package fdaf.base;

public interface CommonConfigurationInterface {

    public void setOfflineSite(boolean offlineSite);

    public boolean getOfflineSite();

    public void setSiteName(String siteName);

    public String getSiteName();

    public void setDomain(String domain);

    public String getDomain();

    public void setDomainAsDefaultSite(boolean domainAsDefaultSite);

    public boolean getDomainAsDefaultSite();
    
    public void setWebSocketClientSecureKey(String webSocketClientSecureKey);

    public String getWebSocketClientSecureKey();
    
    public void setAllowPerUserMultipleLogins(boolean allowPerUserMultipleLogins);
    
    public boolean getAllowPerUserMultipleLogins();

    public void setWebmasterEmail(String webmasterEmail);

    public String getWebmasterEmail();

    public void setSiteDescription(String siteDescription);

    public String getSiteDescription();

    public void setRegionalLanguage(String regionalLanguage);

    public String getRegionalLanguage();
    
    public void setFileManagerHomeDirectory(String fileManagerHomeDirectory);

    public String getFileManagerHomeDirectory();

    public void setCompanyName(String companyName);

    public String getCompanyName();

    public void setCompanyDescription(String companyDescription);

    public String getCompanyDescription();

    public void setCompanyAddress1(String companyAddress1);

    public String getCompanyAddress1();

    public void setCompanyAddress2(String companyAddress2);

    public String getCompanyAddress2();

    public void setCompanyPhone1(String companyPhone1);

    public String getCompanyPhone1();

    public void setCompanyPhone2(String companyPhone2);

    public String getCompanyPhone2();
    
    public boolean saveConfig();
    
    public boolean isEnabled();
    
    public void loadConfig();
    
    public boolean underUnixLikeOS();
}
