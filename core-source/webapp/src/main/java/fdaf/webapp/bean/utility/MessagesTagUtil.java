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
package fdaf.webapp.bean.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@SessionScoped
@Named
public class MessagesTagUtil implements Serializable {
    private static final long serialVersionUID = 1L;
    private String severity = "info";

    public MessagesTagUtil() {
    }

    public String getSeverity() {
        List<FacesMessage> messageList = FacesContext.getCurrentInstance().getMessageList();
        if (!messageList.isEmpty()) {
            for (Object ko : FacesMessage.VALUES_MAP.keySet()) {
                FacesMessage.Severity fs = (FacesMessage.Severity) FacesMessage.VALUES_MAP.get(ko);
                if (fs.equals(messageList.get(0).getSeverity())) {
                    return String.valueOf(ko).toLowerCase();
                }
            }
        }
        return severity;
    }

    public boolean getRendered() {
        for (FacesMessage m : FacesContext.getCurrentInstance().getMessageList()) {
            if (!m.isRendered()) {
                return true;
            }
        }
        return false;
    }

    public List<String> getMessages() {
        Iterator iterator = FacesContext.getCurrentInstance().getMessages();
        List<String> messages = new ArrayList<String>();
        String detailTrace = "";
        while (iterator.hasNext()) {
            FacesMessage message = (FacesMessage) iterator.next();
            if (!message.isRendered()) {
                if (!detailTrace.equals(message.getDetail())) {
                    messages.add(message.getDetail());
                }
                detailTrace = message.getDetail();
                message.rendered();
            }
        }
        return messages;
    }
}
