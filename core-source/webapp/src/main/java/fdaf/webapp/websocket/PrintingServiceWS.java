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
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PAICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TO (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fdaf.webapp.websocket;

import fdaf.webapp.base.AbstractWebSocket;
import fdaf.webapp.bean.system.PrintingServiceBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/printing-service")
public class PrintingServiceWS extends AbstractWebSocket {

    @Inject
    private PrintingServiceBean printingService;

    private String webSocketClientSecureKey;
    private ExecutorService executorService;
    private boolean locked;
    private String address;
    
    @Override
    protected void onOpenTask() {
        webSocketClientSecureKey = printingService.getWebSocketClientSecureKey();
        printingService.addWebSocket(this);
        printingService.runService();
        // TODO: add code to configure IPv4 address
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                }
                if (executorService != null && !executorService.isShutdown()) {
                    try {
                        executorService.shutdownNow();
                        return;
                    } catch (Exception e) {
                    }
                }
                try {
                    if (session != null && session.isOpen()) {
                        session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Secure key veryfication timeout."));
                    }
                    open = false;
                } catch (Exception e) {
                }
            }
        });
    }
    
    public String getAddress() {
        return address;
    }

    @Override
    protected void onMessageTask(String message) {
        if (locked == false && !message.equals(webSocketClientSecureKey)) {
            try {
                if (session != null && session.isOpen()) {
                    session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Wrong secure key."));
                }
                open = false;
            } catch (Exception e) {
            }
        }
        locked = true;
    }
}
