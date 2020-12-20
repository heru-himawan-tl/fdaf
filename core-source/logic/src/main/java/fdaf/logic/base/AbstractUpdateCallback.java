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
package fdaf.logic.base;

/**
 * Prototype to simplify the extending of UPDATE callback.
 */
public abstract class AbstractUpdateCallback {

    protected String customMessage;
    protected String message;

    protected AbstractUpdateCallback() {
        // NO-OP
    }
    
    public void onPrepareUpdateTask() {
        // NO-OP
    }
    
    public void onSaveCreateTask() {
        // NO-OP
    }
    
    public void onSaveUpdateTask() {
        // NO-OP
    }

    public boolean preCreateCheck() {
        return true;
    }

    public void postCreateTask() {
        // NO-OP
    }

    public void rollbackCreateTask() {
        // NO-OP
    }

    public boolean preUpdateCheck() {
        return true;
    }

    public void postUpdateTask() {
        // NO-OP
    }
    
    public void onReloadEntityTask() {
        // NO-OP
    }
    
    public void onPreRemoveTask(Object primaryKey) {
        // NO-OP
    }
    
    public void setCustomMessage(String customMessage) {
        if (this.customMessage != null) {
            this.customMessage += "-" + customMessage;
        } else {
            this.customMessage = customMessage;
        }
    }

    public String getCustomMessage() {
        String backup = customMessage;
        customMessage = null;
        return backup;
    }

    public void setMessage(String message) {
        if (this.message != null) {
            this.message += "-" + message;
        } else {
            this.message = message;
        }
    }

    public String getMessage() {
        String backup = message;
        message = null;
        return backup;
    } 
}
