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
package fdaf.logic.tools;

import fdaf.logic.base.SourcedDataCheckerInterface;
import java.util.HashMap;
import java.util.Map;

public class SourcedDataChecker {

    protected final Map<String, SourcedDataCheckerInterface> sourceDataCheckerMap = new HashMap<String, SourcedDataCheckerInterface>();
    protected String callbackPrefixName;

    public SourcedDataChecker() {
        // NO-OP
    }

    public void configure(Object callback) {
        Class<?> enclosingClass = callback.getClass().getEnclosingClass();
        if (enclosingClass == null) {
            callbackPrefixName = callback.getClass().getName();
        } else {
            callbackPrefixName = enclosingClass.getName();
        }
        callbackPrefixName = callbackPrefixName.replaceAll("^.*\\.|UpdateCallback$", "");
    }

    protected void mapSourcedDataCheckers() {
        // NO-OP
    }

    public boolean isSourced(Object primaryKey) {
        if (!sourceDataCheckerMap.isEmpty()) {
            for (String key: sourceDataCheckerMap.keySet()) {
                if (key.matches("^" + callbackPrefixName + "On.*")) {
                    SourcedDataCheckerInterface sdc = sourceDataCheckerMap.get(key);
                    if (sdc.isSourced(primaryKey)) {
                        return true;
                    }
                }
            }
        }
        return false;
    } 
}