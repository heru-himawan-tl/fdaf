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
package fdaf.webapp.base;

import fdaf.base.ApplicationIdentifier;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

public abstract class AbstractWebAppCommon extends ApplicationIdentifier {

    protected ResourceBundle customCallbackMessageBundle;
    protected ResourceBundle customMessageBundle;
    protected ResourceBundle callbackMessageBundle;
    protected ResourceBundle messageBundle;
    protected ResourceBundle elabel;
    protected ResourceBundle label;
    protected ResourceBundle clabel;

    protected AbstractWebAppCommon() {
        // NO-OP
    }
    
    @PostConstruct
    public void postConstruct() {
        FacesContext context = FacesContext.getCurrentInstance();
        customCallbackMessageBundle = context.getApplication().getResourceBundle(context, "customCallbackMessage");
        customMessageBundle = context.getApplication().getResourceBundle(context, "customMessage");
        callbackMessageBundle = context.getApplication().getResourceBundle(context, "callbackMessage");
        messageBundle = context.getApplication().getResourceBundle(context, "defaultMessage");
        elabel = context.getApplication().getResourceBundle(context, "elabel");
        label = context.getApplication().getResourceBundle(context, "label");
        clabel = context.getApplication().getResourceBundle(context, "clabel");
    }
    
    public String getLocalDateTime() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(format);
    }
}
