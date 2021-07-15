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
package fdaf.logic.ejb.utility;

import fdaf.base.FileManagerInterface;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

// UNDER DEVELOPMENT !
@Remote({FileManagerInterface.class})
@Stateful(passivationCapable = false)
public class FileManagerUtil {
        
    private static final long serialVersionUID = 1L;
    
    private Map<String, String> directoriesMap = new HashMap<String, String>();
    private Map<String, String> filesMap = new HashMap<String, String>();
    private LinkedList<String> nodeList = new LinkedList<String>();
    private String baseDirectory;
    private boolean error;

    public FileManagerUtil() {
        // NO-OP
    }

    private void recursiveReadDir(String dirname, LinkedList<String> nodeList) {
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dirname));
            for (Path path : directoryStream) {
                String fileName = path.getFileName().toString();
                nodeList.add(dirname + File.separator + fileName);
                if (Files.isDirectory(path)) {
                    recursiveReadDir(dirname + File.separator + fileName, nodeList);
                }
            }
            directoryStream.close();
        } catch (Exception e) {
        }
    }

    public void populateNodes() {

        try {
            if (baseDirectory == null) {
                baseDirectory = System.getProperty("user.home");
            }
        } catch (Exception e) {
            error = true;
            return;
        }
        
        Map<String, String> localDirectoriesShortByName = new HashMap<String, String>();
        Map<String, String> localFilesShortByName = new HashMap<String, String>();
        Map<String, String> localDirectoriesShortByDate = new HashMap<String, String>();
        Map<String, String> localFilesShortByDate = new HashMap<String, String>();
        
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(baseDirectory));
            for (Path path : directoryStream) {
                String name = path.getFileName().toString();
                if (Files.isDirectory(path)) {
                    localDirectoriesShortByName.put(name, baseDirectory + File.separator + name);
                } else {
                    localFilesShortByName.put(name, baseDirectory + File.separator + name);
                }
            }
            directoryStream.close();
        } catch (Exception e) {
        }
        
        directoriesMap = new TreeMap<String, String>(localDirectoriesShortByName);
        filesMap = new TreeMap<String, String>(localFilesShortByName);
        
        if (!directoriesMap.isEmpty()) {
            System.out.println("Directories: ");
            for (String key : directoriesMap.keySet()) {
                System.out.println(directoriesMap.get(key));
            }
        }
        
        if (!filesMap.isEmpty()) {
            System.out.println("Files: ");
            for (String key : filesMap.keySet()) {
                System.out.println(filesMap.get(key));
            }
        }
    }
    
    public Map<String, String> getNodeMap() {
        return null;
    }
    
    public void search(String keywords) {
    }
    
    public List<String> getSearchResultList() {
        return null;
    }

    public void changeDirectory(String directoryAddress) {
    }
    
    public void upload(List<InputStream> fileStreamList) {
    }
    
    public void move(List<String> fileAddressList, String sourceDirectory, String destinationDirectory) {
    }
    
    public void remove(List<String> fileAddressList) {
    }
}
