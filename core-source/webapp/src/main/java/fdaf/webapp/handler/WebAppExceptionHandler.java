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
package fdaf.webapp.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.faces.FacesException;

public class WebAppExceptionHandler extends ExceptionHandlerWrapper {
    private static final Logger LOGGER = Logger.getLogger(WebAppExceptionHandler.class.getCanonicalName());
    private ExceptionHandler wrapped;

    WebAppExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        final Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents().iterator();
        while (iterator.hasNext()) {
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) iterator.next().getSource();
            final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Throwable throwable = context.getException();
            final Map<String, Object> requestMap = externalContext.getRequestMap();
            try {
                try {
                    if (throwable.getClass().getSimpleName().equals("ViewExpiredException")) {
                        //externalContext.redirect("expire.jsf");
                        FacesContext fcontext = FacesContext.getCurrentInstance();
                        ViewHandler viewHandler = fcontext.getApplication().getViewHandler();
                        fcontext.setViewRoot(viewHandler.createView(fcontext, "/expire.xhtml"));
                        fcontext.getPartialViewContext().setRenderAll(true);
                        fcontext.renderResponse();
                    } else {
                        LOGGER.log(Level.SEVERE, null, throwable);
                        requestMap.put("fdaf.error.referer", externalContext.getRequestServletPath().replace("/", ""));
                        StringWriter stringWriter = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(stringWriter);
                        throwable.printStackTrace(printWriter);
                        requestMap.put("fdaf.error.stackTraces", stringWriter.toString().trim());
                        FacesContext fcontext = FacesContext.getCurrentInstance();
                        ViewHandler viewHandler = fcontext.getApplication().getViewHandler();
                        fcontext.setViewRoot(viewHandler.createView(fcontext, "/error.xhtml"));
                        fcontext.getPartialViewContext().setRenderAll(true);
                        fcontext.renderResponse();
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Critical Exception!", e);
                    return;
                }
            } finally {
                iterator.remove();
            }
        }
        getWrapped().handle();
    }
}
