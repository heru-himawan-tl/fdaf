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
import java.io.PrintWriter;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
@WebServlet(urlPatterns = {"/file-preview-tool"})
public class FilePreviewTool extends HttpServlet {
 
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
        String address = request.getParameter("address");
        String view = request.getParameter("view");
        String prev = request.getParameter("prev");
        
        boolean invalidViewOpt = (view == null || (view != null && !view.trim().toLowerCase().matches("^(0|no|false|1|yes|true)$")));
        boolean invalidPrevOpt = (prev == null || (prev != null && !prev.trim().toLowerCase().matches("^(0|no|false|1|yes|true)$")));
        
        boolean createView = (view != null && !view.trim().isEmpty() && !view.trim().toLowerCase().matches("^(0|no|false)$")
                                && view.trim().toLowerCase().matches("^(1|yes|true)$"));
        boolean createPrev = (prev != null && !prev.trim().isEmpty() && !prev.trim().toLowerCase().matches("^(0|no|false)$")
                                && prev.trim().toLowerCase().matches("^(1|yes|true)$"));
        
        boolean isImage = false;
        boolean isVideo = false;
        boolean isAudio = false;
        
        boolean isDirectory = false;
        
        String mimeType = "";
        Path path = null;
        
        if (address == null || (address != null && address.trim().isEmpty()) || invalidViewOpt || invalidPrevOpt) {
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
            if (!mimeType.matches(".*(image|video|audio)\\/.*") && !createView && !createPrev) {
                address = getServletContext().getRealPath("/web-resources/icons/file.png");
            } else {
                isImage = mimeType.matches(".*image\\/.*");
                isVideo = mimeType.matches(".*video\\/.*");
                isAudio = mimeType.matches(".*audio\\/.*");
            }
        } else {
            address = getServletContext().getRealPath("/web-resources/icons/folder.png");
            isDirectory = true;
        }
        
        if (createPrev && !isDirectory) {
            response.setContentType("text/html");
            String contextPath = request.getContextPath();
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            out.println("<head>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
            out.println("<title>" + address + "</title>");
            out.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"" + contextPath + "/javax.faces.resource/style.css.jsf?ln=css\" />");
            out.println("<script type=\"text/javascript\" src=\"" + contextPath + "/javax.faces.resource/file-previewer.js.jsf?ln=js\"></script>");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=0\" />");
            out.println("</head>");
            if (!isAudio && !isVideo && !isImage) {
                out.println("<body style=\"margin: 0px;\">");
            } else {
                out.println("<body style=\"margin: 0px; text-align:center !important;\">");
            }
            out.println("<b>" + address + "</b>\n<hr />");
            if (isImage) {
                out.println("<img src=\"file-preview-tool?view=1&amp;prev=0&amp;address=" 
                    + address + "\" width=\"100%\" alt=\"" + address + "\" id=\"FilePreviewToolImage\" />");
            }
            if (isVideo) {
                out.println("<video width=\"100%\" controls autoplay id=\"FilePreviewToolVideo\">");
                out.println("<source src=\"file-preview-tool?view=1&amp;prev=0&amp;address=" + address + "\" type=\"" + mimeType + "\">");
                out.println("</video>");
            }
            if (isAudio) {
                out.println("<audio width=\"100%\" controls autoplay id=\"FilePreviewToolAudio\">");
                out.println("<source src=\"file-preview-tool?view=1&amp;prev=0&amp;address=" + address + "\" type=\"" + mimeType + "\">");
                out.println("</audio>");
            }
            if (!isAudio && !isVideo && !isImage) {
                out.println("<pre>");
                byte[] bc = Files.readAllBytes(Paths.get(address));
                out.println(new String(bc));
                out.println("</pre>");
            }
            out.println("<script>resizeIframe();</script>");
            out.println("</body>");
            out.println("</html>");
            return;
        }
        
        if (mimeType.matches(".*video\\/.*") && !createView && !isDirectory && !createPrev) {
            address = getServletContext().getRealPath("/web-resources/icons/video.png");
        }
        
        if (mimeType.matches(".*audio\\/.*") && !createView && !isDirectory && !createPrev) {
            address = getServletContext().getRealPath("/web-resources/icons/audio.png");
        }
        
        System.out.println(">>>>>>> " + mimeType);
        
        path = Paths.get(address);
        
        mimeType = Files.probeContentType(path);
        
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + address.replaceAll(".*/", "") + "\"");
        OutputStream output = response.getOutputStream();
        byte[] contents = Files.readAllBytes(path);
        response.setContentLength(contents.length);
        output.write(contents);
        output.flush();
        output.close();
    }
 
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
