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
import fdaf.webapp.bean.system.EditIndexingBean;
import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/edit-indexing")
public class EditIndexingWS extends AbstractWebSocket {

    @Inject
    private EditIndexingBean editIndexing;
    private String viewLayerName;
    private Object dataID;
    
    public String getViewLayerName() {
        return viewLayerName;
    }
    
    public Object getDataID() {
        return dataID;
    }
 
    @Override
    protected void onOpenTask() {
        editIndexing.addWebSocket(this);
        editIndexing.runService();
    }
    
    @Override
    protected void onMessageTask(String message) {
        if (message != null && !message.trim().isEmpty()) {
            String serviceUUID = message.replaceAll("^([a-zA-Z0-9\\-]+)([ ]+)([a-zA-Z]+)([ ]+)([0-9]+)$", "$1");
            if (serviceUUID.equals(editIndexing.getServiceUUID())) {
                viewLayerName = message.replaceAll("^([a-zA-Z0-9\\-]+)([ ]+)([a-zA-Z]+)([ ]+)([0-9]+)$", "$3");
                dataID = (Object) Long.parseLong(message.replaceAll("^([a-zA-Z0-9\\-]+)([ ]+)([a-zA-Z]+)([ ]+)([0-9]+)$", "$5"));
            }
        }
    }
    
    @Override
    protected void onCloseTask() {
    }
}
