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

import fdaf.webapp.websocket.EditIndexingWS;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named
public class EditIndexingBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final List<EditIndexingWS> webSockets = new ArrayList<EditIndexingWS>();    
    private ExecutorService executorService;
    private String serviceUUID = UUID.randomUUID().toString();
    
    public EditIndexingBean() {
        // NO-OP
    }
    
    public void addWebSocket(EditIndexingWS websocket) {
        if (websocket != null && websocket.isOpen()) {
            webSockets.add(websocket);
        }
    }
    
    public boolean isInEditing(String viewLayerName, Object dataID) {
        if (!webSockets.isEmpty()) {
            for (EditIndexingWS webSocket : webSockets) {
                if (webSocket.isOpen() && webSocket.getViewLayerName().equals(viewLayerName)
                    && webSocket.getDataID().equals(dataID)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getServiceUUID() {
        return serviceUUID;
    }
    
    public void removeWebSocket(EditIndexingWS editIndexingWS) {
        if (!webSockets.isEmpty()) {
            EditIndexingWS e = null;
            for (EditIndexingWS webSocket : webSockets) {
                if (webSocket.isOpen() && webSocket.getUuid().equals(editIndexingWS.getUuid())) {
                    e = webSocket;
                }
            }
            if (e != null) {
                webSockets.remove(e);
            }
        }
    }
   
    public void runService() {
        if (executorService != null && !executorService.isShutdown()) {
            return;
        }
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            public void run() {
                while (true) {
                    boolean continueRun = false;
                    if (!webSockets.isEmpty()) {
                        for (EditIndexingWS webSocket : webSockets) {
                            if (webSocket.isOpen()) {
                                continueRun = true;
                                break;
                            }
                        }
                    }
                    if (!continueRun && executorService != null && !executorService.isShutdown()) {
                        try {
                            webSockets.clear();
                            executorService.shutdownNow();
                            return;
                        } catch (Exception e) {
                        }
                    }
                    if (executorService.isShutdown()) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        });
    }
}
