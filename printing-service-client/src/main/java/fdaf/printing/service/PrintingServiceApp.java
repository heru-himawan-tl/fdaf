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
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class PrintingServiceApp {

    private static final Logger LOGGER = Logger.getLogger(PrintingServiceApp.class.getName());
    private static Object waitLock = new Object();
    public String webSocketServerURI;
    public String configPath;
    
    public void initConfig() {
    	boolean isUNIX = (!System.getProperty("os.name").matches(".*Windows.*"));
        String fsp = File.separator;
        String userHome = null;
        try {
            userHome = System.getProperty("user.home");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
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
                configPath = "C:\\fdaf\\printing-service-client.conf";
                System.out.println("[Client] Configuration file: " + configPath); 
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
        } else {
            try {
                configPath = userHome + fsp + ".fdaf" + fsp + "printing-service-client.conf";
                System.out.println("[Client] Configuration file: " + configPath); 
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
            try {
                configPath = "/usr/local/etc/fdaf/printing-service-client.conf";
                System.out.println("[Client] Configuration file: " + configPath); 
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
            try {
                configPath = "/etc/fdaf/printing-service-client.conf";
                System.out.println("[Client] Configuration file: " + configPath); 
                if (Files.exists(Paths.get(configPath))) {
                    loadConfig();
                    return;
                }
            } catch (Exception e) {
            }
	    }
    }
    
    public void run() {
        WebSocketContainer container = null;
        Session session = null;
        initConfig();
        try {
            container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(PrintingServiceWSC.class, URI.create(webSocketServerURI));
            waitTermSignal();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, null, e);
                }
            }
        }
    }
    
    public void loadConfig() {
        System.out.println("[Client] Loading configuration ...");
        try {
            InputStream input = new FileInputStream(configPath);
            Properties prop = new Properties();
            prop.load(input);
            webSocketServerURI = prop.getProperty("webSocketServerURI");
            System.out.println("[Client] Found configuration: webSocketServerURI = " + webSocketServerURI);
            input.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }
    }

    private static void waitTermSignal() {
        synchronized(waitLock) {
            try {
                waitLock.wait();
            } catch (Exception e) {
            }
        }
    }
    
    public static void main(String[] args) {
        PrintingServiceApp printingServiceApp = new PrintingServiceApp();
        printingServiceApp.run();
    }
}
