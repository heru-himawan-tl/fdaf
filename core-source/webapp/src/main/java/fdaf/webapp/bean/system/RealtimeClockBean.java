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

import fdaf.webapp.websocket.RealtimeClockWS;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named
public class RealtimeClockBean implements Serializable {

    private static final List<RealtimeClockWS> websockets = new ArrayList<RealtimeClockWS>();
    private static final long serialVersionUID = 1L;
    private ExecutorService executorService;
    
    public RealtimeClockBean() {
        // NO-OP
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
                    if (!websockets.isEmpty()) {
                        for (RealtimeClockWS ws : websockets) {
                            if (ws.isOpen()) {
                                continueRun = true;
                                break;
                            }
                        }
                    }
                    if (!continueRun && executorService != null && !executorService.isShutdown()) {
                        websockets.clear();
                        executorService.shutdownNow();
                        return;
                    }
                    if (executorService.isShutdown()) {
                        return;
                    }
                    try {
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String date = LocalDateTime.now().format(format);
                        for (RealtimeClockWS ws : websockets) {
                            if (ws.isOpen()) {
                                ws.sendText(date);
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        });
    }
    
    public void addWebSocket(RealtimeClockWS websocket) {
        if (websocket != null && websocket.isOpen()) {
            websockets.add(websocket);
        }
    }
}
