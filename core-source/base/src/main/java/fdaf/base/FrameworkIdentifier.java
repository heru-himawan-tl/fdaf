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
package fdaf.base;

public abstract class FrameworkIdentifier {

    protected FrameworkIdentifier() {
        // NO-OP
    }
    
    public String getFrameworkCodeName() {
        return "FDAF";
    }
    
    public String getFrameworkLongName() {
        return "FDAF Framework";
    }
    
    public String getFrameworkDescription() {
        return "FDAF (or F.D.A.F) - a free, open-source, MVC framework for creating Java web applications based JSF (JavaServer Face), and also mainly designed for creating enterprise application, implementing the standard Java EE. It favors both convention and configuration, makes implementation and configuration to be more simple, and lets the programmer write less of codes.";
    }
    
    public String getFrameworkDevCopyright() {
        return "Copyright (C) Heru Himawan Tejo Laksono";
    }
    
    public String getFrameworkDevSite() {
        return "https://github.com/heru-himawan-tl/fdaf";
    }
    
    public String getFrameworkVersion() {
        return "1.0";
    }
}
