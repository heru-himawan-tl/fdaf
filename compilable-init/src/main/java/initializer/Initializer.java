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
package initializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

public class Initializer {

    private final Map<String, String> propertyMap = new HashMap<String, String>();
    private final String temporaryCompilationDir;
    private final String applicationSourceDirectory;
    private final String ejbLookupDir;
    private final String applicationCodeName;
    
    public Initializer() throws Exception {
        Properties properties = new Properties();
        InputStream propertiesFileStream = new FileInputStream("develop.properties");
        properties.load(propertiesFileStream);
        Enumeration e = properties.propertyNames();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        propertyMap.put("fdaf.applicationCompiledDate", formatter.format(date));
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = properties.getProperty(key);
            propertyMap.put(key, value);
        }
        applicationCodeName = propertyMap.get("fdaf.applicationCodeName");
        ejbLookupDir = applicationCodeName + "/" + applicationCodeName + "-logic";
        applicationSourceDirectory = new File(propertyMap.get("fdaf.applicationSourceDirectory")).getCanonicalPath();
        temporaryCompilationDir = System.getProperty("java.io.tmpdir") + File.separator + propertyMap.get("fdaf.applicationGroupId") + File.separator + "raw-compilation";
    }
    
    private void recursiveReadDir(String dirname, LinkedList<String> nodeList, boolean toFormat) {
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dirname));
            for (Path path : directoryStream) {
                String fileName = path.getFileName().toString();
                if (toFormat) {
                    if (fileName.equals("pom.xml") || fileName.equals("ApplicationIdentifier.java")
                            || fileName.equals("persistence.xml") || dirname.matches(".*webapp\\/bean.*")
                            || dirname.matches(".*logic\\/ejb\\/callback.*")
                            || dirname.matches(".*logic\\/ejb\\/facade.*")
                            || fileName.matches(".*\\.(yaml|yml|properties)$")
                            || fileName.equals("invoker-pom.xml")
                            || fileName.equals("web.xml")
                            || fileName.matches(".*Repository\\.java$")) {
                        nodeList.add(dirname + File.separator + fileName);
                    }
                } else {
                    nodeList.add(dirname + File.separator + fileName);
                }
                if (Files.isDirectory(path)) {
                    recursiveReadDir(dirname + File.separator + fileName, nodeList, toFormat);
                }
            }
            directoryStream.close();
        } catch (Exception e) {
        }
    }
    
    private void format(String nodeAddr) {
        String originalSource = "";
        String source = "";
        String dest = "";
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(nodeAddr));
            String l = null;
            while ((l = r.readLine()) != null) {
                String s = l;
                originalSource += l + "\n";
                for (String k : propertyMap.keySet()) {
                    s = s.replaceAll("\\$\\{" + k + "\\}", propertyMap.get(k));
                }
                if (nodeAddr.matches(".*persistence.xml") && nodeAddr.matches(".*thorntail.*")
                        && s.matches(".*\\<jta\\-data\\-source\\>.*")
                        && !s.matches(".*\\<jta\\-data\\-source\\>java\\:jboss\\/datasources\\/.*")) {
                    s = s.replace("<jta-data-source>", "<jta-data-source>java:jboss/datasources/");
                }
                if (nodeAddr.matches(".*(webapp\\/bean|logic\\/ejb\\/callback|logic\\/ejb\\/facade).*") && nodeAddr.matches(".*thorntail.*")) {
                    s = s.replace("__EJB_LOOKUP_DIR__", applicationCodeName);
                }
                if (nodeAddr.matches(".*(webapp\\/bean|logic\\/ejb\\/callback|logic\\/ejb\\/facade).*") && !nodeAddr.matches(".*thorntail.*")) {
                    s = s.replace("__EJB_LOOKUP_DIR__", ejbLookupDir);
                }
                if (nodeAddr.matches(".*with\\-eclipselink.*")) {
                    s = s.replaceAll("__JPA_PROVIDER_NAME__", "EclipseLink").replaceAll("__JPA_PROVIDER__", "eclipselink");
                }
                if (nodeAddr.matches(".*with\\-hibernate.*")) {
                    s = s.replaceAll("__JPA_PROVIDER_NAME__", "Hibernate").replaceAll("__JPA_PROVIDER__", "hibernate");
                }
                source += s + "\n";
            }
            r.close();
        } catch (Exception e) {
        }
        if (!originalSource.equals(source)) {
            try {
                FileWriter fw = new FileWriter(nodeAddr.replace(applicationSourceDirectory, temporaryCompilationDir), false);
                fw.write(source);
                fw.close();
                System.out.println("Formatted temporary compilation source: " + nodeAddr);
            } catch (Exception e) {
            }
        }
        if (nodeAddr.matches(".*resources.*\\.properties$")) {
            String newRCAddr = nodeAddr.replace(".properties", "_en_US.properties");
            try {
                Files.copy(Paths.get(nodeAddr), Paths.get(newRCAddr));
            } catch (Exception e) {
            }
        }
    }
    
    private byte[] readFile(String fileAddress) {
        try {
            byte[] contents = Files.readAllBytes(Paths.get(fileAddress));
            int i = contents.length;
            while (i-- > 0) {
                if (contents[i] != 10) {
                    break;
                }
            }
            byte[] output = new byte[i + 1];
            System.arraycopy(contents, 0, output, 0, i + 1);
            return output;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            byte[] dummy = {};
            return dummy;
        }
    }
    
    private void writeFile(String fileAddress, byte[] contents) {
        try {
            Files.write(Paths.get(fileAddress), contents);
            System.out.println("Written file: " + fileAddress);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    public void proceed() throws Exception {
        LinkedList<String> preformattedNodeList = new LinkedList<String>();
        LinkedList<String> applicationNodeList = new LinkedList<String>();
        LinkedList<String> toFormatNodeList = new LinkedList<String>();
        String finalThorntailJar = applicationCodeName + "-thorntail.jar";
        String finalWAR = applicationCodeName + ".war";
        String finalEAR = applicationCodeName + ".ear";
        String buildDirectory = "";
        recursiveReadDir(temporaryCompilationDir, toFormatNodeList, true);
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("Formatting specific compilable application source files ...");
        System.out.println("-------------------------------------------------------------------------------");
        for (String nodeAddr : toFormatNodeList) {
            format(nodeAddr);
        }
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("Organizing compilable application source files ...");
        System.out.println("-------------------------------------------------------------------------------");
        recursiveReadDir(temporaryCompilationDir, preformattedNodeList, false);
        Collections.sort(preformattedNodeList);
        for (String nodeAddr : preformattedNodeList) {
            String currentAppSourceNodeAddr = nodeAddr.replace(temporaryCompilationDir, applicationSourceDirectory);
            if (Files.isDirectory(Paths.get(nodeAddr))) {
                if (!Files.exists(Paths.get(currentAppSourceNodeAddr))) {
                    try {
                        Files.createDirectory(Paths.get(currentAppSourceNodeAddr));
                        System.out.println("Created directory: " + currentAppSourceNodeAddr);
                    } catch (java.io.IOException e) {
                        e.printStackTrace(System.out);
                    }
                }
            }
            if (Files.isRegularFile(Paths.get(nodeAddr))) {
                if (!Files.exists(Paths.get(currentAppSourceNodeAddr))) {
                    writeFile(currentAppSourceNodeAddr, readFile(nodeAddr));
                } else {
                    byte[] c0 = readFile(currentAppSourceNodeAddr);
                    byte[] c1 = readFile(nodeAddr);
                    boolean isEqual = true;
                    if (c0.length != c1.length) {
                        isEqual = false;
                    } else {
                        for (int i = 0; i < c1.length; i++) {
                            if (c1[i] != c0[i]) {
                                isEqual = false;
                                break;
                            }
                        }
                    }
                    if (!isEqual) {
                        System.out.println("Updating file: " + currentAppSourceNodeAddr);
                        writeFile(currentAppSourceNodeAddr, c1);
                    }
                }
            }
        }
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("Checking for unused compilable application source files or directories ...");
        System.out.println("-------------------------------------------------------------------------------");
        recursiveReadDir(applicationSourceDirectory, applicationNodeList, false);
        Collections.sort(applicationNodeList);
        for (String nodeAddr : applicationNodeList) {
            String dirname = new File(nodeAddr).getParent();
            String basename = nodeAddr.replace(dirname, "").replace(File.separator, "");
            if (basename.equals(finalThorntailJar) || basename.equals(finalWAR) || basename.equals(finalEAR)) {
                buildDirectory += dirname.replaceAll("(\\p{Punct})", "\\\\$1") + "|";
            }
        }
        if (!buildDirectory.isEmpty()) {
            buildDirectory = "(" + buildDirectory.trim().replaceAll("\\|$", "") + ")";
        }
        for (String nodeAddr : applicationNodeList) {
            String temporaryNodeAddr = nodeAddr.replace(applicationSourceDirectory, temporaryCompilationDir);
            if (Files.isRegularFile(Paths.get(nodeAddr)) && !Files.exists(Paths.get(temporaryNodeAddr))) {
                if (!buildDirectory.isEmpty() && nodeAddr.matches("^" + buildDirectory + ".*")) {
                    continue;
                }
                System.out.println("Found unused node: " + nodeAddr);
                try {
                    Files.delete(Paths.get(nodeAddr));
                    System.out.println("Removed unused node: " + nodeAddr);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        }
        for (String nodeAddr : applicationNodeList) {
            String temporaryNodeAddr = nodeAddr.replace(applicationSourceDirectory, temporaryCompilationDir);
            if (Files.isDirectory(Paths.get(nodeAddr)) && !Files.exists(Paths.get(temporaryNodeAddr))) {
                if (!buildDirectory.isEmpty() && nodeAddr.matches("^" + buildDirectory + ".*")) {
                    continue;
                }
                System.out.println("Found unused node: " + nodeAddr);
                try {
                    Files.delete(Paths.get(nodeAddr));
                    System.out.println("Removed unused node: " + nodeAddr);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println("===============================================================================");
        System.out.println("Initializing compilable application source ...");
        System.out.println("===============================================================================");
        Initializer initializer = new Initializer();
        initializer.proceed();
    }
}