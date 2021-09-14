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

import fdaf.webapp.bean.system.FileManagerSettingsBean;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
@WebServlet(urlPatterns = {"/file-preview-tool"})
public class FilePreviewTool extends HttpServlet {

    @Inject
    private FileManagerSettingsBean settings;
 
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
                String iconName = mimeType.replaceAll("\\/", "-");
                iconName = (iconName.equals("null")) ? "gnome-fs-regular" : iconName;
                address = getServletContext().getRealPath("/web-resources/icons/mimetypes/" + iconName + ".svg");
            } else {
                isImage = mimeType.matches(".*image\\/.*");
                isVideo = mimeType.matches(".*video\\/.*");
                isAudio = mimeType.matches(".*audio\\/.*");
            }
        } else {
            address = getServletContext().getRealPath("/web-resources/icons/folder.svg");
            isDirectory = true;
        }
        
        if (createPrev && !isDirectory) {
            response.setContentType("text/html");
            String contextPath = request.getContextPath();
            PrintWriter out = response.getWriter();
            String urlAddress = address;
            try {
                urlAddress = URLEncoder.encode(address, "UTF-8");
            } catch (Exception e) {
            }
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
            String fsp = File.separator;
            out.println("<div class=\"field_note_box\">" + address.replace(settings.getBaseDirectory(), fsp).replace(fsp + fsp, fsp) + "</div>");
            out.println("<div class=\"file_previewer_internal_box\">");
            if (isImage) {
                out.println("<img src=\"file-preview-tool?view=1&amp;prev=0&amp;address=" 
                    + urlAddress + "\" width=\"100%\" alt=\"" + urlAddress + "\" id=\"FilePreviewToolImage\" />");
            }
            if (isVideo) {
                out.println("<video width=\"100%\" controls autoplay playsinline id=\"FilePreviewToolVideo\">");
                out.println("<source src=\"file-preview-tool?view=1&amp;prev=0&amp;address=" + urlAddress + "\" type=\"" + mimeType + "\">");
                out.println("</video>");
            }
            if (isAudio) {
                out.println("<audio width=\"100%\" controls autoplay playsinline id=\"FilePreviewToolAudio\">");
                out.println("<source src=\"file-preview-tool?view=1&amp;prev=0&amp;address=" + urlAddress + "\" type=\"" + mimeType + "\">");
                out.println("</audio>");
            }
            if (!isAudio && !isVideo && !isImage && !mimeType.equals("application/pdf")) {
                out.println("<pre>");
                byte[] bc = Files.readAllBytes(Paths.get(address));
                String cnt = new String(bc);
                out.println(cnt.replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;"));
                out.println("</pre>");
            }
            if (!isAudio && !isVideo && !isImage && mimeType.equals("application/pdf")) {
                //out.println("<object class=\"file_previewer_object\" height=\"100%\" data=\"file-preview-tool?view=1&amp;prev=0&amp;address=" + urlAddress + "\" type=\"application/pdf\">");
                out.println("<embed class=\"file_previewer_object\" height=\"100%\" src=\"file-preview-tool?view=1&amp;prev=0&amp;address=" + urlAddress + "\" type=\"application/pdf\" />");
                //out.println("</object>");
            }
            out.println("</div>");
            out.println("<script>resizeIframe();</script>");
            out.println("</body>");
            out.println("</html>");
            return;
        }
        
        if (mimeType.matches(".*(video|audio)\\/.*") && !createView && !isDirectory && !createPrev) {
            String iconName = mimeType.replaceAll("\\/", "-");
            address = getServletContext().getRealPath("/web-resources/icons/mimetypes/" + iconName + ".svg");
        }
        
        path = Paths.get(address);
        
        if (!Files.exists(path)) {
            address = getServletContext().getRealPath("/web-resources/icons/mimetypes/gnome-fs-regular.svg");
            path = Paths.get(address);
        }
        
        OutputStream output = response.getOutputStream();
        mimeType = Files.probeContentType(path);
        
        if (request.getHeader("range") != null) {
            response.setStatus(206);
            String rangeValue = request.getHeader("range").trim().substring("bytes=".length());
            File sourceFile = new File(address);
            long fileLength = sourceFile.length();
            long start, end;
            if (rangeValue.startsWith("-")) {
                end = fileLength - 1;
                start = fileLength - 1 - Long.parseLong(rangeValue.substring("-".length()));
            } else {
                String[] range = rangeValue.split("-");
                start = Long.parseLong(range[0]);
                end = range.length > 1 ? Long.parseLong(range[1]) : fileLength - 1;
            }
            if (end > fileLength - 1) {
                end = fileLength - 1;
            }
            if (start <= end) {
                long contentLength = end - start + 1;
                response.setHeader("Content-Length", contentLength + "");
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
                response.setHeader("Content-Type", mimeType);
                response.setHeader("Content-Disposition", "inline; filename=\"" + address.replaceAll(".*/", "") + "\"");
                RandomAccessFile raf = new RandomAccessFile(sourceFile, "r");
                raf.seek(start);
                byte[] buffer = new byte[500000];
                int bytesRead = 0;
                int totalRead = 0;
                while (totalRead < contentLength) {
                    bytesRead = raf.read(buffer);
                    totalRead += bytesRead;
                    output.write(buffer, 0, bytesRead);
                }
            }
        } else {
            try {
                response.setContentType(mimeType);
                response.setHeader("Content-Disposition", "inline; filename=\"" + address.replaceAll(".*/", "") + "\"");
                response.setStatus(200);
                byte[] contents = Files.readAllBytes(path);
                response.setContentLength(contents.length);
                output.write(contents);
                output.flush();
                output.close();
            } catch (Exception e) {
            }
        }
    }
 
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
