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
package modeler;

import fdaf.base.*;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.NotFound;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileWriter;
import javax.persistence.*;

public class Modeler {

    private LinkedHashMap<String, String> uiLabelFromEntity = new LinkedHashMap<String, String>();
    private LinkedList<String> modelAsEntityOnly = new LinkedList<String>();
    private String entityResourceBundleMessage = "";
    private boolean withEclipseLink;
    private String temporaryDirectory;
    private String currentWorkingDirectory;
    private String templatesDirectory;
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BOLD = "\u001B[1m";

    public Modeler(String[] args) throws Exception {
        currentWorkingDirectory = new File(".").getCanonicalPath();
        templatesDirectory = currentWorkingDirectory + "/templates";
        temporaryDirectory = System.getProperty("java.io.tmpdir") + "/io.sourceforge.fdaf";
    }

    public void setWithEclipseLink(boolean withEclipseLink) {
        this.withEclipseLink = withEclipseLink;
    }

    private void buildModelSource(String modelName, String artifactId) throws Exception {
        Class<?> clazz = Class.forName("fdaf.logic.entity." + modelName, false, getClass().getClassLoader());
        String templateFile = reformatPath(currentWorkingDirectory + "/" + artifactId + "/src/main/java/fdaf/logic/entity/" + modelName + ".java");
        String modelSourceContents = "";
        boolean enumeratedAnnotationClassImported = false;
        boolean joinColumnAnnotationClassImported = false;
        boolean oneToOneAnnotationClassImported = false;
        boolean columnAnnotationClassImported = false;
        boolean notFoundAnnotationClassImported = false;
        boolean withoutDataProperty = false;
        boolean withoutToString = false;
        boolean useSimpleToString = false;
        boolean withoutEquals = false;
        String toStringInfo = "";
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(templateFile));
            String l = null;
            int cmc = 0;
            boolean inComment = false;
            while ((l = r.readLine()) != null) {
                if (l.trim().matches("^// NO_WEB_APP_BEAN_GEN$")) {
                    modelAsEntityOnly.add(modelName);
                }
                if (l.trim().matches("^// WITHOUT_DATA_PROPERTY$")) {
                    withoutDataProperty = true;
                }
                if (l.trim().matches("^// WITHOUT_EQUALS")) {
                    withoutEquals = true;
                }
                if (l.trim().matches("^// USE_SIMPLE_TO_STRING$")) {
                    useSimpleToString = true;
                }
                if (l.trim().matches("^// WITHOUT_TO_STRING$")) {
                    withoutToString = true;
                }
            }
            r.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        LinkedList<String[]> constraintsMessage = new LinkedList<String[]>();
        String defaultNamedQuery = "";
        String defaultEntityInstanceName = "";
        String entityBody = "    public " + modelName + "() {\n    }\n\n"
                + "    public void setId(Long id) {\n"
                + "        this.id = id;\n"
                + "    }\n\n"
                + "    public Long getId() {\n"
                + "        return id;\n"
                + "    }\n\n";
        String constructorParameter = "";
        String constructorBody = "";
        String mrbk = toJavaFieldNaming(modelName);
        String mrbv = modelName.replaceAll("([a-z])([A-Z]|[0-9])", "$1 $2");
        if (!uiLabelFromEntity.containsKey(mrbk)) {
            uiLabelFromEntity.put(mrbk, mrbv);
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.toString().matches(".*static final.*")) {
                continue;
            }
            String fieldTypeName = field.getGenericType().getTypeName().replaceAll("[a-z]+\\.", "");
            String fieldName = field.getName();
            if (field.isAnnotationPresent(NotFound.class)) {
                notFoundAnnotationClassImported = true;
            }
            if (field.isAnnotationPresent(Enumerated.class)) {
                enumeratedAnnotationClassImported = true;
            }
            if (field.isAnnotationPresent(JoinColumn.class)) {
                joinColumnAnnotationClassImported = true;
            }
            if (field.isAnnotationPresent(OneToOne.class)) {
                oneToOneAnnotationClassImported = true;
            }
            if (field.isAnnotationPresent(Column.class)) {
                columnAnnotationClassImported = true;
            }
            String setterName = toSetterName(fieldName);
            String getterName = toGetterName(fieldName);
            if (!field.isAnnotationPresent(ManyToOne.class) && !field.isAnnotationPresent(OneToMany.class) &&
                !field.isAnnotationPresent(OneToOne.class) && !field.isAnnotationPresent(ManyToMany.class)) {
                constructorParameter += fieldTypeName + " " + fieldName + ",";
                constructorBody += "        this." + fieldName + " = " + fieldName + ";\n";
                String rbk = fieldName;
                toStringInfo += "            + \"[" + fieldName + "=\" + " + fieldName + " + \"]\\n\"\n";
                String rbv = toJavaClassNaming(fieldName.replaceAll("([a-z])([A-Z]|[0-9])", "$1 $2")
                    .toLowerCase())
                    .replace("Id", "ID").replace(" id", " ID").replace("Dob ", "D.O.B ")
                    .replace("IDle", "Idle").replace("Ipv 4", "IPv4 ")
                    .replace("Email", "E-mail").replace(" email", " e-mail")
                    .replace("uuid", "UUID").replace("Ssn", "SSN").replace("ssn", "SSN");
                if (!uiLabelFromEntity.containsKey(rbk)) {
                    uiLabelFromEntity.put(rbk, rbv);
                }
                for (Annotation a : field.getDeclaredAnnotations()) {
                    Class<? extends Annotation> at = a.annotationType();
                    for (Method am : at.getDeclaredMethods()) {
                        String aname = at.getName().replaceAll(".*\\.", "");
                        String amn = am.getName();
                        if (amn.equals("message")) {
                            String crbk = toJavaFieldNaming(modelName) + "." + fieldName + ".invalid." + aname;
                            String crbv = String.valueOf(am.invoke(a));
                            constraintsMessage.add(new String[]{crbk, crbv});
                            entityResourceBundleMessage += crbk + "=" + crbv + "\n";
                        }
                    }
                }
            } else {
                try {
                    Class efc = Class.forName(field.getGenericType().getTypeName(), false, getClass().getClassLoader());
                    for (Field efcf : efc.getDeclaredFields()) {
                        if (efcf.toString().matches(".*static final.*") || efcf.isAnnotationPresent(ManyToOne.class)
                                || efcf.isAnnotationPresent(OneToMany.class)
                                || efcf.isAnnotationPresent(OneToOne.class)
                                || efcf.isAnnotationPresent(ManyToMany.class)) {
                            continue;
                        }
                        String fn = efc.getName().replaceAll(".*\\.", "") + toJavaClassNaming(efcf.getName());
                        String rbk = toJavaFieldNaming(fn);
                        String rbv = toJavaClassNaming(fn.replaceAll("([a-z])([A-Z]|[0-9])", "$1 $2")
                            .toLowerCase()).replace("Id", "ID").replace(" id", " ID").replace("dob", "D.O.B ")
                            .replace("IDle", "Idle").replace("Ipv 4", "IPv4 ")
                            .replace("uuid", "UUID").replace("Ssn", "SSN").replace("ssn", "SSN")
                            .replaceAll("[ ]+", " ");
                        if (!uiLabelFromEntity.containsKey(rbk)) {
                            uiLabelFromEntity.put(rbk, rbv);
                        }
                    }
                } catch (Exception e) {
                }
            }
            entityBody += ""
                +   "    public void " + setterName + "(" + fieldTypeName + " " + fieldName + ") {\n"
                +   "        this." + fieldName + " = " + fieldName + ";\n"
                +   "    }\n\n"
                +   "    public " + fieldTypeName + " " + getterName + "() {\n"
                +   "        return " + fieldName + ";\n"
                +   "    }\n\n";
        }
        if (!withoutDataProperty) {
            entityBody += ""
                + "    public void setAuthorId(Long authorId) {\n"
                + "        this.authorId = authorId;\n"
                + "    }\n\n"
                + "    public Long getAuthorId() {\n"
                + "        return authorId;\n"
                + "    }\n\n"
                + "    public void setUserGroupId(Long userGroupId) {\n"
                + "        this.userGroupId = userGroupId;\n"
                + "    }\n\n"
                + "    public Long getUserGroupId() {\n"
                + "        return userGroupId;\n"
                + "    }\n\n"
                + "    public void setModifierId(Long modifierId) {\n"
                + "        this.modifierId = modifierId;\n"
                + "    }\n\n"
                + "    public Long getModifierId() {\n"
                + "        return modifierId;\n"
                + "    }\n\n"
                + "    public void setCreatedDate(String createdDate) {\n"
                + "        this.createdDate = createdDate;\n"
                + "    }\n\n"
                + "    public String getCreatedDate() {\n"
                + "        return createdDate;\n"
                + "    }\n\n"
                + "    public void setModificationDate(String modificationDate) {\n"
                + "        this.modificationDate = modificationDate;\n"
                + "    }\n\n"
                + "    public String getModificationDate() {\n"
                + "        return modificationDate;\n"
                + "    }\n\n"
                + "    public void setPermission(Permission permission) {\n"
                + "        this.permission = permission;\n"
                + "    }\n\n"
                + "    public Permission getPermission() {\n"
                + "        return permission;\n"
                + "    }\n\n";
            String[] fieldNames = {"authorId", "modifierId", "userGroupId", "createdDate", "modificationDate", "permission"};
            for (String fn : fieldNames) {
                toStringInfo += "            + \"[" + fn + "=\" + " + fn + " + \"]\\n\"\n";
            }
            String[][] dataPropertyLabels = {
                {"createdDate", "Created date"},
                {"modificationDate", "Modification date"},
                {"permission", "Access permission"},
            };
            for (String[] dataPropertyLabel : dataPropertyLabels) {
                if (!uiLabelFromEntity.containsKey(dataPropertyLabel[0])) {
                    uiLabelFromEntity.put(dataPropertyLabel[0], dataPropertyLabel[1]);
                }
            }
            constructorParameter += "Long authorId,Long modifierId,Long userGroupId,String createdDate,String modificationDate,Permission permission,";
        } else {
            entityBody += ""
                + "    public void setAuthorId(Long authorId) {\n"
                + "        this.authorId = authorId;\n"
                + "    }\n\n"
                + "    public Long getAuthorId() {\n"
                + "        return authorId;\n"
                + "    }\n\n"
                + "    public void setModifierId(Long modifierId) {\n"
                + "        this.modifierId = modifierId;\n"
                + "    }\n\n"
                + "    public Long getModifierId() {\n"
                + "        return modifierId;\n"
                + "    }\n\n"
                + "    public void setCreatedDate(String createdDate) {\n"
                + "        this.createdDate = createdDate;\n"
                + "    }\n\n"
                + "    public String getCreatedDate() {\n"
                + "        return createdDate;\n"
                + "    }\n\n"
                + "    public void setModificationDate(String modificationDate) {\n"
                + "        this.modificationDate = modificationDate;\n"
                + "    }\n\n"
                + "    public String getModificationDate() {\n"
                + "        return modificationDate;\n"
                + "    }\n\n";
            String[] fieldNames = {"authorId", "modifierId", "createdDate", "modificationDate"};
            for (String fn : fieldNames) {
                toStringInfo += "            + \"[" + fn + "=\" + " + fn + " + \"]\\n\"\n";
            }
            String[][] dataPropertyLabels = {
                {"createdDate", "Created date"},
                {"modificationDate", "Modification date"},
            };
            for (String[] dataPropertyLabel : dataPropertyLabels) {
                if (!uiLabelFromEntity.containsKey(dataPropertyLabel[0])) {
                    uiLabelFromEntity.put(dataPropertyLabel[0], dataPropertyLabel[1]);
                }
            }
            constructorParameter += "Long authorId,Long modifierId,String createdDate,String modificationDate,";
        }
        constructorParameter = constructorParameter.trim().replaceAll(",$", ",String uuid").replaceAll(",", ",\n            ").replace("\t", "    ");
        entityBody += String.format(""
            + "    public void setUuid(String uuid) {\n"
            + "        this.uuid = uuid;\n"
            + "    }\n\n"
            + "    public String getUuid() {\n"
            + "        return uuid;\n"
            + "    }\n\n"
            + "    @Override\n"
            + "    public int hashCode() {\n"
            + "        int hash = 0;\n"
            + "        hash += (id != null) ? id.hashCode() : 0;\n"
            + "        return hash;\n"
            + "    }\n\n"
            + ((withoutEquals)
            ? ""
            : "    @Override\n"
            + "    public boolean equals(Object object) {\n"
            + "        if (!(object instanceof %s)) {\n"
            + "            return false;\n"
            + "        }\n"
            + "        %s other = (%s) object;\n"
            + "        if (((this.getId() == null) && (other.getId() != null))\n"
            + "                || ((this.getId() != null) && !this.getId().equals(other.getId()))) {\n"
            + "            return false;\n"
            + "        }\n"
            + "        return true;\n"
            + "    }\n\n")
            + ((withoutToString)
            ? ""
            : ((useSimpleToString)
            ? "    @Override\n"
            + "    public String toString() {\n"
            + "        return getId().toString();\n"
            + "    }\n"
            : "    @Override\n"
            + "    public String toString() {\n"
            + "        return getClass().getName()\n"
            + "            + \"[id=\" + id + \"]\\n\"\n"
            + ((!toStringInfo.trim().isEmpty()) ? "            " + toStringInfo.trim() + "\n" : "")
            + "            + \"[uuid=\" + uuid + \"]\";\n"
            + "    }\n")), modelName, modelName, modelName);
        String currentModel = reformatPath(temporaryDirectory + "/modeling-output/generated/" + ((withEclipseLink) ? "with-eclipselink" : "with-hibernate") + "/logic/src/main/java/fdaf/logic/entity/" + modelName + ".java");
        String modelSourceDestContents = "";
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(templateFile));
            String l = null;
            int cmc = 0;
            while ((l = r.readLine()) != null) {
                if (l.trim().matches("^// WITHOUT_DATA_PROPERTY$") || l.trim().matches("^// NO_WEB_APP_BEAN_GEN$")
                    || l.trim().matches("^// WITHOUT_TO_STRING$") || l.trim().matches("^// WITHOUT_EQUALS$")
                    || l.trim().matches("^// USE_SIMPLE_TO_STRING$")) {
                    continue;
                }
                if (l.trim().matches("^import java.io.Serializable;$")) {
                    l = "import javax.persistence.GeneratedValue;\n"
                        + "import javax.persistence.GenerationType;\n"
                        + ((!withEclipseLink && !notFoundAnnotationClassImported)
                            ? "import org.hibernate.annotations.NotFoundAction;\n"
                            + "import org.hibernate.annotations.NotFound;\n" : "")
                        + ((!joinColumnAnnotationClassImported)
                            ? "import javax.persistence.JoinColumn;\n" : "")
                        + ((!oneToOneAnnotationClassImported)
                            ? "import javax.persistence.OneToOne;\n" : "")
                        + ((!columnAnnotationClassImported)
                            ? "import javax.persistence.Column;\n" : "")
                        + ((!enumeratedAnnotationClassImported)
                            ? "import javax.persistence.Enumerated;\n"
                            + "import javax.persistence.EnumType;\n" : "")
                        + "import javax.persistence.Id;\n"
                        + "import fdaf.base.Permission;\n\n"
                        + l;
                }
                if (l.trim().matches("^private static final long serialVersionUID.*")) {
                    l = l + "\n"
                        + "    @Id\n"
                        + "    @GeneratedValue(strategy = GenerationType.IDENTITY)\n"
                        + "    private Long id;";
                }
                if (l.matches(".*message([ \\t]+)?\\=([ \\t]+)?\".*")) {
                    String[] cms = constraintsMessage.get(cmc);
                    l = l.replace(cms[1], "{" + cms[0] + "}");
                    cmc++;
                }
                if (l.matches(".*\\/\\/ ADD_LABEL.*")) {
                    String vl = l.replaceAll("(.*ADD_LABEL\\:)([\t ]+)?(.*)", "$3").trim();
                    String kl = "";
                    for (String al : vl.split(" ")) {
                        if (!al.trim().isEmpty()) {
                            kl += toJavaClassNaming(al);
                        }
                    }
                    kl = toJavaFieldNaming(kl).replace("dOB", "DOB");
                    uiLabelFromEntity.put(kl, vl);
                    continue;
                }
                modelSourceContents += l.replace("\t", "    ") + "\n";
            }
            r.close();
        } catch (Exception ex) {
        }
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(currentModel));
            String l = null;
            while ((l = r.readLine()) != null) {
                modelSourceDestContents += l.replace("\t", "    ") + "\n";
            }
            r.close();
        } catch (Exception ex) {
        }
        uiLabelFromEntity.put("id", "ID");
        modelSourceContents = modelSourceContents.trim();

        if (modelSourceContents.isEmpty()) {
            return;
        }
        String[] modelSourceContentsTMP = modelSourceContents.split("\n");
        modelSourceContents = "";
        for (String l : modelSourceContentsTMP) {
            if (withEclipseLink && l.matches(".*NotFound.*")) {
                continue;
            }
            modelSourceContents += l.replace("\t", "    ") + "\n";
        }
        modelSourceContents = modelSourceContents.trim();
        String constructorTemplateFMT = "    private String uuid;\n";
        if (!withoutDataProperty) {
            constructorTemplateFMT += "    @Column(name = \"author_id\", nullable = true)\n"
                + "    private Long authorId;\n"
                + "    @Column(name = \"user_group_id\", nullable = true)\n"
                + "    private Long userGroupId;\n"
                + "    @Column(name = \"modifier_id\", nullable = true)\n"
                + "    private Long modifierId;\n"
                + "    @Column(name = \"created_date\", nullable = true)\n"
                + "    private String createdDate;\n"
                + "    @Column(name = \"modification_date\", nullable = true)\n"
                + "    private String modificationDate;\n"
                + "    @Enumerated(EnumType.STRING)\n"
                + "    private Permission permission;\n"
                + ((!withEclipseLink) ? "    @NotFound(action = NotFoundAction.IGNORE)\n" : "")
                + "    @OneToOne\n"
                + "    @JoinColumn(name = \"author_id\", insertable = false, updatable = false)\n"
                + "    private Author author;\n"
                + ((!withEclipseLink) ? "    @NotFound(action = NotFoundAction.IGNORE)\n" : "")
                + "    @OneToOne\n"
                + "    @JoinColumn(name = \"user_group_id\", insertable = false, updatable = false)\n"
                + "    private UserGroup userGroup;\n"
                + ((!withEclipseLink) ? "    @NotFound(action = NotFoundAction.IGNORE)\n" : "")
                + "    @OneToOne\n"
                + "    @JoinColumn(name = \"modifier_id\", insertable = false, updatable = false)\n"
                + "    private Modifier modifier;\n\n";
            constructorBody += "        this.authorId = authorId;\n"
                + "        this.userGroupId = userGroupId;\n"
                + "        this.modifierId = modifierId;\n"
                + "        this.createdDate = createdDate;\n"
                + "        this.modificationDate = modificationDate;\n"
                + "        this.permission = permission;\n";
        } else {
            constructorTemplateFMT += "    @Column(name = \"author_id\", nullable = true)\n"
                + "    private Long authorId;\n"
                + "    @Column(name = \"modifier_id\", nullable = true)\n"
                + "    private Long modifierId;\n"
                + "    @Column(name = \"created_date\", nullable = true)\n"
                + "    private String createdDate;\n"
                + "    @Column(name = \"modification_date\", nullable = true)\n"
                + "    private String modificationDate;\n"
                + "    @Enumerated(EnumType.STRING)\n"
                + ((!withEclipseLink) ? "    @NotFound(action = NotFoundAction.IGNORE)\n" : "")
                + "    @OneToOne\n"
                + "    @JoinColumn(name = \"author_id\", insertable = false, updatable = false)\n"
                + "    private Author author;\n"
                + ((!withEclipseLink) ? "    @NotFound(action = NotFoundAction.IGNORE)\n" : "")
                + "    @OneToOne\n"
                + "    @JoinColumn(name = \"modifier_id\", insertable = false, updatable = false)\n"
                + "    private Modifier modifier;\n\n";
            constructorBody += "        this.authorId = authorId;\n"
                + "        this.modifierId = modifierId;\n"
                + "        this.createdDate = createdDate;\n"
                + "        this.modificationDate = modificationDate;\n";
        }
        constructorTemplateFMT += "    public " + modelName + "(%s) {\n        %s\n    }";
        if (!constructorParameter.trim().isEmpty()) {
            entityBody = String.format(constructorTemplateFMT, constructorParameter, constructorBody.trim()) + "\n\n" + entityBody;
        } else {
            entityBody = "    private String uuid;" + "\n\n" + entityBody;
        }
        modelSourceContents = cleanTabs(modelSourceContents);
        entityBody = cleanTabs(entityBody);
        modelSourceContents = modelSourceContents.replace("    // ENTITY_BODY", entityBody).trim().replace("\n\n\n", "\n\n");
        modelSourceContents = cleanTabs(modelSourceContents).replace("    }\n\n}", "    }\n}").replaceAll("([\n]+)([ ]+)(private String uuid)", "$2\n    $3");
        if (withoutDataProperty) {
            modelSourceContents = modelSourceContents.replaceAll("(private String uuid;)", "$1\n");
        }
        if (modelSourceContents.equals(modelSourceDestContents.trim())) {
            return;
        }
        System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] " + ANSI_GREEN + "Updating entity source: " + ANSI_RESET + currentModel);
        try {
            FileWriter fw = new FileWriter(currentModel, false);
            fw.write(modelSourceContents);
            fw.close();
        } catch (Exception e) {
        }
    }

    private void buildWebAppBeanSource(String webAppBeanName) throws Exception {
        if (modelAsEntityOnly.contains(webAppBeanName)) {
            return;
        }
        String currentWebAppBeanPath = reformatPath(temporaryDirectory + "/modeling-output/generated/webapp/src/main/java/fdaf/webapp/bean/common/" + toJavaClassNaming(webAppBeanName) + "WebAppBean.java");
        String templatePath = reformatPath(templatesDirectory + "/WebAppBeanTemplate.java");
        String currentWebAppBeanSource = "";
        String templateSource = "";
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(currentWebAppBeanPath));
            String l = null;
            while ((l = r.readLine()) != null) {
                currentWebAppBeanSource += l.replace("\t", "    ") + "\n";
            }
            r.close();
        } catch (Exception ex) {
        }
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(templatePath));
            String l = null;
            while ((l = r.readLine()) != null) {
                templateSource += l.replaceAll("__NAME__", webAppBeanName).replace("\t", "    ") + "\n";
            }
            r.close();
        } catch (Exception ex) {
        }
        currentWebAppBeanSource = currentWebAppBeanSource.trim();
        templateSource = templateSource.trim();
        if (templateSource.equals(currentWebAppBeanSource)) {
            return;
        }
        System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] " + ANSI_GREEN + "Updating web application bean source: " + ANSI_RESET + currentWebAppBeanPath);
        try {
            FileWriter fw = new FileWriter(currentWebAppBeanPath, false);
            fw.write(templateSource);
            fw.close();
        } catch (Exception e) {
        }
    }

    private void buildFacadeSource(String facadeName, String baseDirectory, String artifactId) throws Exception {
        String templateFile = reformatPath(currentWorkingDirectory + "/" + artifactId + "/src/main/java/fdaf/logic/entity" + "/" + facadeName + ".java");
        String currentFacadePath = reformatPath(temporaryDirectory + "/modeling-output/generated/logic/src/main/java/fdaf/logic/ejb/facade/" + toJavaClassNaming(facadeName) + "Facade.java");
        String templatePath = reformatPath(templatesDirectory + "/FacadeTemplate.java");
        String updateCallbackPath = reformatPath(currentWorkingDirectory + "/" + baseDirectory + "/logic/src/main/java/fdaf/logic/ejb/callback/" + toJavaClassNaming(facadeName) + "UpdateCallback.java");
        String updateCallbackName = toJavaFieldNaming(facadeName) + "UpdateCallback";
        String currentFacadeSource = "";
        String templateSource = "";
        boolean withoutDataProperty = false;
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(templateFile));
            String l = null;
            int cmc = 0;
            boolean inComment = false;
            while ((l = r.readLine()) != null) {
                if (l.trim().matches("^// WITHOUT_DATA_PROPERTY$")) {
                    withoutDataProperty = true;
                }
            }
            r.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(currentFacadePath));
            String l = null;
            while ((l = r.readLine()) != null) {
                currentFacadeSource += l.replace("\t", "    ") + "\n";
            }
            r.close();
        } catch (Exception ex) {
        }
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(templatePath));
            String l = null;
            while ((l = r.readLine()) != null) {
                String cbm = "";
                if (l.trim().matches(".*protected __NAME__Repository getRepository.*")
                    && Files.exists(Paths.get(updateCallbackPath))) {
                    cbm = "    @EJB(lookup = \"java:global/__EJB_LOOKUP_DIR__/__NAME__UpdateCallback\")\n"
                        + "    fdaf.logic.base.UpdateCallbackInterface<__NAME__Repository, __NAME__> updateCallbackBean;\n\n"
                        + "    @Override\n"
                        + "    protected void presetUpdateCallback() {\n"
                        + "        updateCallback = updateCallbackBean;\n"
                        + "    }\n\n";
                }
                if (l.trim().matches(".*import fdaf.logic\\.base\\.Specification;.*")
                    && Files.exists(Paths.get(updateCallbackPath))) {
                    //l += "\nimport fdaf.logic.callback.__NAME__UpdateCallback;";
                }
                templateSource += (cbm + l).replaceAll("__NAME__", facadeName).replace("\t", "    ") + "\n";
            }
            String additionalCode = "";
            if (!withoutDataProperty) {
                additionalCode += "\n    @Override\n    protected void updateDataProperties(boolean updateMode) {"
                    + "\n        updateRecordDate();"
                    + "\n        entity.setModificationDate(recordDate);"
                    + "\n        if (!updateMode) {"
                    + "\n            entity.setAuthorId(authorId);"
                    + "\n            entity.setUserGroupId(userGroupId);"
                    + "\n            modifierId = authorId;"
                    + "\n            entity.setCreatedDate(recordDate);"
                    + "\n        }"
                    + "\n        entity.setModifierId(modifierId);"
                    + "\n    }\n}";
            } else {
                additionalCode += "\n    @Override\n    protected void updateDataProperties(boolean updateMode) {"
                    + "\n        updateRecordDate();"
                    + "\n        entity.setModificationDate(recordDate);"
                    + "\n        if (!updateMode) {"
                    + "\n            entity.setAuthorId(authorId);"
                    + "\n            modifierId = authorId;"
                    + "\n            entity.setCreatedDate(recordDate);"
                    + "\n        }"
                    + "\n        entity.setModifierId(modifierId);"
                    + "\n    }\n}";
            }
            templateSource = templateSource.trim().replaceAll("\\}$", additionalCode);
            r.close();
        } catch (Exception ex) {
        }
        currentFacadeSource = currentFacadeSource.trim();
        templateSource = templateSource.trim();
        if (templateSource.equals(currentFacadeSource)) {
            return;
        }
        System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] " + ANSI_GREEN + "Updating facade source: " + ANSI_RESET + currentFacadePath);
        try {
            FileWriter fw = new FileWriter(currentFacadePath, false);
            fw.write(templateSource);
            fw.close();
        } catch (Exception e) {
        }
    }

    private void buildRepositorySource(String repositoryName) throws Exception {
        String currentRepositoryPath = reformatPath(temporaryDirectory + "/modeling-output/generated/logic/src/main/java/fdaf/logic/ejb/repository/" + toJavaClassNaming(repositoryName) + "Repository.java");
        String templatePath = reformatPath(templatesDirectory + "/RepositoryTemplate.java");
        String currentRepositorySource = "";
        String templateSource = "";
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(currentRepositoryPath));
            String l = null;
            while ((l = r.readLine()) != null) {
                currentRepositorySource += l.replace("\t", "    ") + "\n";
            }
            r.close();
        } catch (Exception ex) {
        }
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(templatePath));
            String l = null;
            while ((l = r.readLine()) != null) {
                templateSource += l.replaceAll("__NAME__", repositoryName).replace("\t", "    ") + "\n";
            }
            r.close();
        } catch (Exception ex) {
        }
        currentRepositorySource = currentRepositorySource.trim();
        templateSource = templateSource.trim();
        if (templateSource.equals(currentRepositorySource)) {
            return;
        }
        System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] " + ANSI_GREEN + "Updating repository source: " + ANSI_RESET + currentRepositoryPath);
        try {
            FileWriter fw = new FileWriter(currentRepositoryPath, false);
            fw.write(templateSource);
            fw.close();
        } catch (Exception e) {
        }
    }

    private String toJavaFieldNaming(String source) {
        char[] sourceAsCharacters = source.toCharArray();
        sourceAsCharacters[0] = Character.toLowerCase(sourceAsCharacters[0]);
        return new String(sourceAsCharacters);
    }

    private String toJavaClassNaming(String source) {
        char[] sourceAsCharacters = source.toCharArray();
        sourceAsCharacters[0] = Character.toUpperCase(sourceAsCharacters[0]);
        return new String(sourceAsCharacters);
    }

    private String toSetterName(String source) {
        return "set" + toJavaClassNaming(source);
    }

    private String toGetterName(String source) {
        return "get" + toJavaClassNaming(source);
    }

    private String cleanTabs(String source) {
        String dest = source;
        while (dest.matches(".*\t.*")) {
            dest = dest.replace("\t", "    ");
        }
        return dest;
    }

    private void reformatSchemaSource(LinkedList<String> sqlScriptFileNames) {
        String sqlScriptFileFixDir = reformatPath(temporaryDirectory + "/modeling-output/generated/logic/src/main/resources/META-INF/sql/");
        String sqlScriptFileTemplateDir = reformatPath(templatesDirectory + "/logic-resource-template/src/main/resources/META-INF/sql/");
        String sqlScriptTemplateTail = "";
        String sqlScriptFixTail = "";
        for (String sqlScriptFileName : sqlScriptFileNames) {
            String sqlScriptTemplate = "";
            String sqlScriptFix = "";
            try {
                BufferedReader r = Files.newBufferedReader(Paths.get(sqlScriptFileFixDir + sqlScriptFileName));
                String l = null;
                while ((l = r.readLine()) != null) {
                    sqlScriptFix += l + "\n";
                }
                r.close();
            } catch (Exception e) {
            }
            try {
                BufferedReader r = Files.newBufferedReader(Paths.get(sqlScriptFileTemplateDir + sqlScriptFileName));
                String l = null;
                while ((l = r.readLine()) != null) {
                    sqlScriptTemplate += l.trim().replaceAll("[\t ]+", " ");
                    if (l.matches(".*\\;.*")) {
                        sqlScriptTemplate += "\n";
                    }
                }
                String[] sqlScriptTemplateTMP = sqlScriptTemplate.split("\n+");
                sqlScriptTemplate = "";
                for (String s : sqlScriptTemplateTMP) {
                    String tableName = s.replaceAll("^(CREATE[ ]+TABLE[ ]+IF[ ]+NOT[ ]+EXISTS[ `]+)([a-z0-9A-Z\\-_]+)([` ]+)(\\(.*$)", "$2");
                    if (!tableName.equals("user_group_member") && !tableName.equals("user_group_program_assoc")) {
                        s = s.replace(");", "PRIMARY KEY(id));").replace(tableName + "` (", tableName + "` (`id` BIGINT NOT NULL AUTO_INCREMENT,");
                        String tableDataPropertyDecl = "`uuid` LONGTEXT,`author_id` BIGINT,`user_group_id` BIGINT,`modifier_id` BIGINT,`created_date` LONGTEXT,`modification_date` LONGTEXT,`permission` LONGTEXT,";
                        sqlScriptTemplate += s.trim().replace("PRIMARY KEY(", tableDataPropertyDecl + "PRIMARY KEY(") + "\n";
                    } else {
                        s = s.replace(");", "PRIMARY KEY(id));").replace(tableName + "` (", tableName + "` (`id` BIGINT NOT NULL AUTO_INCREMENT,");
                        sqlScriptTemplate += s.trim().replace("PRIMARY KEY(", "`uuid` LONGTEXT,`author_id` BIGINT,`modifier_id` BIGINT,`created_date` LONGTEXT,`modification_date` LONGTEXT, PRIMARY KEY(") + "\n";
                    }
                }
                r.close();
            } catch (Exception e) {
            }
            sqlScriptTemplate = sqlScriptTemplate.trim().replaceAll(",", ", ");
            sqlScriptFix = sqlScriptFix.trim();
            sqlScriptTemplateTail += sqlScriptTemplate + "\n";
            sqlScriptFixTail += sqlScriptFix + "\n";
        }
        if (!sqlScriptTemplateTail.equals(sqlScriptFixTail)) {
            System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] " + ANSI_GREEN + "Updating SQL script: " + ANSI_RESET + sqlScriptFileFixDir + "/create-tables.sql");
            try {
                FileWriter fw = new FileWriter(sqlScriptFileFixDir + "/create-tables.sql", false);
                fw.write(sqlScriptTemplateTail);
                fw.close();
            } catch (Exception e) {
            }
        }
    }

    public void proceed() {
        LinkedList<String> sqlScriptFileNames = new LinkedList<String>();
        LinkedList<String> applicationModelNames = new LinkedList<String>();
        LinkedList<String> defaultModelNames = new LinkedList<String>();
        String persistenceXmlLoadScriptParamDef = "            <property name=\"javax.persistence.schema-generation.create-script-source\" value=\"META-INF/sql/create-tables.sql\" />\n";;
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(reformatPath(templatesDirectory + "/logic-resource-template/src/main/resources/META-INF/sql")));
            for (Path path : directoryStream) {
                sqlScriptFileNames.add(path.getFileName().toString());
            }
            directoryStream.close();
        } catch (Exception e) {
        }
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(reformatPath(temporaryDirectory + "/fdaf/application-primitive-entity-model/classes/fdaf/logic/entity")));
            for (Path path : directoryStream) {
                String modelName = path.getFileName().toString().replaceAll("\\.class$", "");
                applicationModelNames.add(modelName);
            }
            directoryStream.close();
        } catch (Exception e) {
        }
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(reformatPath(temporaryDirectory + "/fdaf/core-primitive-entity-model/classes/fdaf/logic/entity")));
            for (Path path : directoryStream) {
                String modelName = path.getFileName().toString().replaceAll("\\.class$", "");
                defaultModelNames.add(modelName);
            }
            directoryStream.close();
        } catch (Exception e) {
        }
        String newPersistenceXmlFilePath = reformatPath(temporaryDirectory + "/modeling-output/generated/" + ((withEclipseLink) ? "with-eclipselink" : "with-hibernate") + "/logic/src/main/resources/META-INF/persistence.xml");
        String persistenceXmlFileTemplatePath = reformatPath(templatesDirectory + "/logic-resource-template/src/main/resources/META-INF/persistence.xml");
        String persistenceXmlTemplate = "";
        String newPersistenceXml = "";
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(newPersistenceXmlFilePath));
            String l = null;
            while ((l = r.readLine()) != null) {
                newPersistenceXml += l.replace("\t", "    ") + "\n";
            }
            r.close();
        } catch (Exception e) {
        }
        try {
            BufferedReader r = Files.newBufferedReader(Paths.get(persistenceXmlFileTemplatePath));
            String l = null;
            while ((l = r.readLine()) != null) {
                persistenceXmlTemplate += l.replace("\t", "    ") + "\n";
                if (withEclipseLink && l.matches(".*\\<properties\\>.*")) {
                    persistenceXmlTemplate += "            <property name=\"eclipselink.weaving\" value=\"false\" />\n";
                }
            }
            r.close();
        } catch (Exception e) {
        }
        persistenceXmlTemplate = persistenceXmlTemplate.trim().replace("<properties>", "<properties>\n            " + persistenceXmlLoadScriptParamDef.trim());
        newPersistenceXml = newPersistenceXml.trim();
        if (!persistenceXmlTemplate.equals(newPersistenceXml)) {
            System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] " + ANSI_GREEN + "Updating persistence.xml: " + ANSI_RESET + newPersistenceXmlFilePath);
            try {
                FileWriter fw = new FileWriter(newPersistenceXmlFilePath, false);
                fw.write(persistenceXmlTemplate);
                fw.close();
            } catch (Exception e) {
            }
        }
        if (!defaultModelNames.isEmpty()) {
            for (String modelName : defaultModelNames) {
                try {
                    buildModelSource(modelName, "core-primitive-entity-model");
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            for (String modelName : defaultModelNames) {
                try {
                    buildWebAppBeanSource(modelName);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            for (String modelName : defaultModelNames) {
                try {
                    buildRepositorySource(modelName);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            for (String modelName : defaultModelNames) {
                try {
                    buildFacadeSource(modelName, "core-source", "core-primitive-entity-model");
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        }
        if (!applicationModelNames.isEmpty()) {
            for (String modelName : applicationModelNames) {
                try {
                    buildModelSource(modelName, "application-primitive-entity-model");
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            for (String modelName : applicationModelNames) {
                try {
                    buildWebAppBeanSource(modelName);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            for (String modelName : applicationModelNames) {
                try {
                    buildRepositorySource(modelName);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            for (String modelName : applicationModelNames) {
                try {
                    buildFacadeSource(modelName, "custom-core-source", "application-primitive-entity-model");
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        }
        entityResourceBundleMessage = entityResourceBundleMessage.trim();
        if (!entityResourceBundleMessage.isEmpty()) {
            String erbmp = reformatPath(temporaryDirectory + "/modeling-output/generated/webapp/src/main/resources/ValidationMessages.properties");
            String cerbm = "";
            try {
                BufferedReader r = Files.newBufferedReader(Paths.get(erbmp));
                String l = null;
                while ((l = r.readLine()) != null) {
                    cerbm += l + "\n";
                }
                r.close();
            } catch (Exception e) {
            }
            cerbm = cerbm.trim();
            if (!entityResourceBundleMessage.equals(cerbm)) {
                System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] " + ANSI_GREEN + "Updating validation message bundle source: " + ANSI_RESET + erbmp);
                try {
                    FileWriter fw = new FileWriter(erbmp, false);
                    fw.write(entityResourceBundleMessage);
                    fw.close();
                } catch (Exception e) {
                }
            }
        }
        if (!uiLabelFromEntity.isEmpty()) {
            String uiLabelFromEntityFilePath = reformatPath(temporaryDirectory + "/modeling-output/generated/erc/src/main/resources/fdaf/erc/label.properties");
            String currentUiLabelFromEntity = "";
            String newUiLabelFromEntity = "";
            for (String key : uiLabelFromEntity.keySet()) {
                newUiLabelFromEntity += key + "=" + uiLabelFromEntity.get(key) + "\n";
            }
            try {
                BufferedReader r = Files.newBufferedReader(Paths.get(uiLabelFromEntityFilePath));
                String l = null;
                while ((l = r.readLine()) != null) {
                    currentUiLabelFromEntity += l + "\n";
                }
                r.close();
            } catch (Exception e) {
            }
            currentUiLabelFromEntity = currentUiLabelFromEntity.trim();
            newUiLabelFromEntity = newUiLabelFromEntity.trim();
            if (!newUiLabelFromEntity.equals(currentUiLabelFromEntity)) {
                System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] " + ANSI_GREEN + "Updating message bundle source: " + ANSI_RESET + uiLabelFromEntityFilePath);
                try {
                    FileWriter fw = new FileWriter(uiLabelFromEntityFilePath, false);
                    fw.write(newUiLabelFromEntity);
                    fw.close();
                } catch (Exception e) {
                }
            }
        }
        if (!sqlScriptFileNames.isEmpty()) {
            reformatSchemaSource(sqlScriptFileNames);
        }
    }
    
    private String reformatPath(String path) {
        return path.replaceAll("[\\/]+", File.separator);
    }
    
    public boolean getWithEclipseLink() {
        return withEclipseLink;
    }

    public static void main(String[] args) throws Exception {
        Modeler modeler = new Modeler(args);
        if (args != null && args.length != 0) {
            for (String arg : args) {
                if (arg.trim().matches("^WithEclipseLink=true$")) {
                    modeler.setWithEclipseLink(true);
                }
            }
        }
        System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] ===============================================================================");
        System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] " + ANSI_BOLD + "Proceed modeling for/with " + ((modeler.getWithEclipseLink()) ? "EclipseLink" : "Hibernate (& for general compilation)") + " ..." + ANSI_RESET);
        System.out.println("[" + ANSI_BLUE + ANSI_BOLD + "FDAF INFO" + ANSI_RESET + "] ===============================================================================");
        modeler.proceed();
    }
}
