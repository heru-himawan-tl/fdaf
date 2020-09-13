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
import fdaf.base.MailerInterface;
import java.io.File;
import java.io.FileInputStream;
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
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;

@Remote({MailerInterface.class})
@Singleton
@Startup
public class Mailer extends ApplicationIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(Mailer.class.getName());
    
    private boolean enabled;
    private String configPath;
    
    private boolean starttls;
    private boolean useSSL;
    private boolean auth;
    private String domain = "localhost";
    private String host = "localhost";
    private String user = "";
    private String pass = "";
    private String port = "25";
    
    private boolean validAddress;
    
    @PostConstruct
    public void initMailer() {
    
        String fsp = File.separator;
        String userHome = null;
        
        try {
            userHome = System.getProperty("user.home");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }
        
        try {
            configPath = "C:\\" + getApplicationCodeName() + "\\" + getApplicationCodeName() + "-mail.conf";
            if (Files.exists(Paths.get(configPath))) {
                loadConfig();
                return;
            }
        } catch (Exception e) {
        }
        
        try {
            configPath = userHome + fsp + "." + getApplicationCodeName() + fsp + getApplicationCodeName() + "-mail.conf";
            if (Files.exists(Paths.get(configPath))) {
                loadConfig();
                return;
            }
        } catch (Exception e) {
        }
        
        try {
            configPath = "/usr/local/etc/" + getApplicationCodeName() + "/" + getApplicationCodeName() + "-mail.conf";
            if (Files.exists(Paths.get(configPath))) {
                loadConfig();
                return;
            }
        } catch (Exception e) {
        }
        
        try {
            configPath = "/etc/" + getApplicationCodeName() + "/" + getApplicationCodeName() + "-mail.conf";
            if (Files.exists(Paths.get(configPath))) {
                loadConfig();
                return;
            }
        } catch (Exception e) {
        }
    }
    
    public void loadConfig() {
        try {
            InputStream input = new FileInputStream(configPath);
            
            Properties prop = new Properties();
            prop.load(input);
            
            useSSL = Boolean.parseBoolean(prop.getProperty("useSSL"));
            auth = Boolean.parseBoolean(prop.getProperty("auth"));
            starttls = Boolean.parseBoolean(prop.getProperty("starttls"));
            host = prop.getProperty("host");
            user = prop.getProperty("user");
            pass = prop.getProperty("pass");
            port = prop.getProperty("port");
            domain = prop.getProperty("domain");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }
        
        enabled = true;
    }
    
    public String getConfigPath() {
        return configPath;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean send(String sender, String reception, String subject, String messages) {
        if (!isEnabled()) {
            return false;
        }
        
        validAddress = true;
        
        try {
        
            Properties prop = System.getProperties();
            prop.put("mail.smtp.auth", (auth) ? "true" : "false");
            prop.put("mail.smtp.starttls.enable", (starttls) ? "true" : "false");
            prop.put("mail.smtp.host", host);
            prop.put("mail.smtp.port", port);
            
            if (useSSL) {
                prop.put("mail.smtp.ssl.trust", host);
            }

            Session session = null;
            
            if (auth) {
                session = Session.getInstance(prop, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });
            } else {
                session = Session.getInstance(prop);
            }
            
            Message message = new MimeMessage(session);
        
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(reception));
            message.setSubject(subject);
            
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(messages, "text/html");
            
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            
            message.setContent(multipart);
            
            Transport.send(message);
        
        } catch (SendFailedException e) {
            Address[] addresses = e.getInvalidAddresses();
            if (addresses != null) {
                for (Address a : addresses) {
                    if (a.toString().equals(reception)) {
                        validAddress = false;
                        break;
                    }
                }
            }
            LOGGER.log(Level.SEVERE, null, e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            return false;
        }
        
        return true;
    }
    
    public boolean isValidAddress() {
        return validAddress;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public String getHost() {
        return host;
    }
    
    public String getUser() {
        return user;
    }
}
