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
package fdaf.webapp.base;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 * This Web Socket server model is implemented in FDAF in case the JSF v.2.x
 * Mojarra's Push still has serious bug. If use JSF v.2.x Mojarra's Push,
 * the web socket will be unexpectedly stopped upon the web application
 * reloaded. We also need the more customizable web socket server endpoint,
 * for instance to work with ExecutorService.
 */
public abstract class AbstractWebSocket implements WebSocketInterface {

    protected String uuid = UUID.randomUUID().toString();
    protected Session session;
    protected boolean open;
 
    @OnOpen
    public void onOpen(Session session) throws IOException {
        setSession(session);
        open = true;
        onOpenTask();
    }
    
    protected void onOpenTask() {
        // NO-OP
    }
    
    @OnMessage
    public void onBinMessage(Session session, ByteBuffer message) throws IOException {
        setSession(session);
        onBinMessageTask(message);
    }
 
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        setSession(session);
        onMessageTask(message);
    }
    
    protected void onBinMessageTask(ByteBuffer message) {
        // NO-OP
    }
    
    protected void onMessageTask(String message) {
        // NO-OP
    }
 
    @OnClose
    public void onClose(Session session) throws IOException {
        setSession(session);
        open = false;
        onCloseTask();
    }
    
    protected void onCloseTask() {
        // NO-OP
    }
 
    @OnError
    public void onError(Session session, Throwable throwable) {
        setSession(session);
        onErrorTask();
    }
    
    protected void onErrorTask() {
        // NO-OP
    }
    
    public void sendText(String text) {
        if (open) {
            try {
                session.getAsyncRemote().sendText(text);
            } catch (Exception e) {
            }
        }
    }
    
    protected void setSession(Session session) {
        this.session = session;
    }
    
    public boolean isOpen() {
        return open;
    }
    
    public String getUuid() {
        return uuid;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebSocketInterface)) {
            return false;
        }
        WebSocketInterface other = (WebSocketInterface) object;
        if (!this.uuid.equals(other.getUuid())) {
            return false;
        }
        return true;
    }
}
