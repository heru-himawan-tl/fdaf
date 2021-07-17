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
package fdaf.webapp.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.File;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
@WebServlet(urlPatterns = {"/file-manager-tool"})
public class FileManagerTool extends HttpServlet {
 
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String view = request.getParameter("view");
        boolean createView = (view != null && !view.trim().isEmpty() && !view.trim().toLowerCase().matches("^(0|no|false)$")
                                && view.trim().toLowerCase().matches("^(1|yes|true)$"));
        boolean invalid = (view == null || (view != null && !view.trim().toLowerCase().matches("^(0|no|false|1|yes|true)$")));
        String address = request.getParameter("address");
        String mimeType = "";
        Path path = null;
        
        if (address == null || (address != null && address.trim().isEmpty()) || invalid) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        path = Paths.get(address);
        
        if (!Files.exists(path)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        if (!Files.isDirectory(path)) {
            mimeType = new String(Files.probeContentType(path) + "").toLowerCase();
            if (!mimeType.matches(".*(png|jpg|jpeg|bmp|gif|jfif|tiff|wmf|ppm|pgm|pbm|pnm|webp|heif).*") && createView) {
                address = getServletContext().getRealPath("/web-resources/icons/file.png");
            } 
        } else {
            address = getServletContext().getRealPath("/web-resources/icons/folder.png");
        }
        
        mimeType = Files.probeContentType(path);
        path = Paths.get(address);
        
        if (!Files.exists(path)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + address.replaceAll(".*/", "") + "\"");
        OutputStream output = response.getOutputStream();
        
        try {
            for (byte b : Files.readAllBytes(path)) {
                output.write(b);
            }
        } catch (Exception e) {
        }
        
        output.flush();
        output.close();
    }
 
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
