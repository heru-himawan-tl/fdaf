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
package fdaf.printing.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class PrintingServiceWSC {

    private static final Logger LOGGER = Logger.getLogger(PrintingServiceWSC.class.getName());
    private String webSocketClientSecureKey;
    private Session session;
    private String configPath;
    private boolean open;
    private boolean unlocked;
    
    public void initConfig() {
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
                configDirPath = Paths.get(userHome + fsp + ".fdaf");
            } else {
                configDirPath = Paths.get("C:\\fdaf");
            }
            if (!Files.exists(configDirPath)) {
                Files.createDirectory(configDirPath);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        
        if (!isUNIX) {
            try {
                configPath = "C:\\fdaf\\websocket-client.conf";
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
        } else {
            try {
                configPath = userHome + fsp + ".fdaf" + fsp + "websocket-client.conf";
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
            try {
                configPath = "/usr/local/etc/fdaf/websocket-client.conf";
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
            try {
                configPath = "/etc/fdaf/websocket-client.conf";
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
	    }
    }
    
    @OnOpen
    public void onOpen(Session session) throws IOException {
        setSession(session);
        initConfig();
        open = true;
        if (webSocketClientSecureKey != null && !webSocketClientSecureKey.isEmpty()) {
            sendText(webSocketClientSecureKey);
        } else {
            sendText("");
        }
    }
    
    public void loadConfig() {
        try {
            InputStream input = new FileInputStream(configPath);
            Properties prop = new Properties();
            prop.load(input);
            webSocketClientSecureKey = prop.getProperty("webSocketClientSecureKey");
            input.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        setSession(session);
        if (message != null && !message.isEmpty()) {
            if (!unlocked && message.trim().equals(webSocketClientSecureKey)) {
                System.out.println("[Client] Connection successfully unlocked ...");
                unlocked = true;
            }
            if (!message.trim().equals(webSocketClientSecureKey)) {
                System.out.println("[Server] " + message);
            }
        }
    }
    
    @OnMessage
    public void onMessage(Session session, ByteBuffer message) throws IOException {
        setSession(session);
        // TODO: Add code to handle printing
    }
    
    protected void setSession(Session session) {
        this.session = session;
    }
    
    @OnClose
    public void onClose(Session session) throws IOException {
        setSession(session);
        open = false;
    }
    
    public void sendText(String text) {
        if (open) {
            try {
                session.getAsyncRemote().sendText(text);
            } catch (Exception e) {
            }
        }
    }
}
