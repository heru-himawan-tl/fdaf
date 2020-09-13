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
package fdaf.webapp.base;

import fdaf.base.Permission;
import fdaf.base.UserType;
import java.lang.reflect.Field;

public class DataPropertiesReflect {
    private Long authorId;
    private Long modifierId;
    private Long userGroupId;
    private Permission permission = Permission.PRIVATE;
    private String authorName;
    private String createdDate;
    private String modificationDate;
    private String modifierName;
    private String userGroupName;
    private UserType userType = null;

    public DataPropertiesReflect(Object data) {
        try {
            Field authorField = getField(data, "author");
            if (authorField != null) {
                Object authorInstance = getValue(data, authorField);
                if (authorInstance != null) {
                    Field userField = getField(authorInstance, "user");
                    if (userField != null) {
                        Object userInstance = getValue(authorInstance, userField);
                        if (userInstance != null) {
                            Field userTypeField = getField(userInstance, "userType");
                            if (userTypeField != null) {
                                Object userTypeObject = getValue(userInstance, userTypeField);
                                if (userTypeObject != null) {
                                    userType = (UserType) userTypeObject;
                                }
                            }
                            Field userNameField = getField(userInstance, "userName");
                            if (userNameField != null) {
                                Object userNameObject = getValue(userInstance, userNameField);
                                if (userNameObject != null) {
                                    authorName = String.valueOf(userNameObject);
                                }
                            }
                        }
                    }
                }
            }
            Field modifierField = getField(data, "modifier");
            if (modifierField != null) {
                Object modifierInstance = getValue(data, modifierField);
                if (modifierInstance != null) {
                    Field userField = getField(modifierInstance, "user");
                    if (userField != null) {
                        Object userInstance = getValue(modifierInstance, userField);
                        if (userInstance != null) {
                            Field userNameField = getField(userInstance, "userName");
                            if (userNameField != null) {
                                Object userNameObject = getValue(userInstance, userNameField);
                                if (userNameObject != null) {
                                    modifierName = String.valueOf(userNameObject);
                                }
                            }
                        }
                    }
                }
            }
            Field userGroupField = getField(data, "userGroup");
            if (userGroupField != null) {
                Object userGroupInstance = getValue(data, userGroupField);
                Field nameField = getField(userGroupInstance, "name");
                if (nameField != null) {
                    Object nameObject = getValue(userGroupInstance, nameField);
                    if (nameObject != null) {
                        userGroupName = String.valueOf(nameObject);
                    }
                }
            }
            Field permissionField = getField(data, "permission");
            if (permissionField != null) {
                Object permissionObject = getValue(data, permissionField);
                if (permissionObject != null) {
                    permission = (Permission) permissionObject;
                }
            }
            Field authorIdField = getField(data, "authorId");
            if (authorIdField != null) {
                Object authorIdObject = getValue(data, authorIdField);
                if (authorIdObject != null) {
                    authorId = Long.parseLong(String.valueOf(authorIdObject));
                }
            }
            Field userGroupIdField = getField(data, "userGroupId");
            if (userGroupIdField != null) {
                Object userGroupIdObject = getValue(data, userGroupIdField);
                if (userGroupIdObject != null) {
                    userGroupId = Long.parseLong(String.valueOf(userGroupIdObject));
                }
            }
            Field modifierIdField = getField(data, "modifierId");
            if (modifierIdField != null) {
                Object modifierIdObject = getValue(data, modifierIdField);
                if (modifierIdObject != null) {
                    modifierId = Long.parseLong(String.valueOf(modifierIdObject));
                }
            }
            Field createdDateField = getField(data, "createdDate");
            if (createdDateField != null) {
                Object createdDateObject = getValue(data, createdDateField);
                if (createdDateObject != null) {
                    createdDate = String.valueOf(createdDateObject);
                }
            }
            Field modificationDateField = getField(data, "modificationDate");
            if (modificationDateField != null) {
                Object modificationDateObject = getValue(data, modificationDateField);
                if (modificationDateObject != null) {
                    modificationDate = String.valueOf(modificationDateObject);
                }
            }
        } catch (Exception e) {
        }
    }

    private Field getField(Object instance, String name) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            try {
                if (field.getName().equals(name)) {
                    return field;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    private Object getValue(Object instance, Field field) {
        try {
            field .setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
        }
        return null;
    }

    public Permission getPermission() {
        return permission;
    }
    
    public UserType getUserType() {
        return userType;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public String getModifierName() {
        return modifierName;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Long getUserGroupId() {
        return userGroupId;
    }

    public Long getModifierId() {
        return modifierId;
    }
    
    public String getCreatedDate() {
        return createdDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    @Override
    public String toString() {
        return getClass().getName() + "\n"
            + "[authorId="          + authorId          + "]\n"
            + "[userType="          + userType          + "]\n"
            + "[modifierId="        + modifierId        + "]\n"
            + "[userGroupId="       + userGroupId       + "]\n"
            + "[permission="        + permission.name() + "]\n"
            + "[authorName="        + authorName        + "]\n"
            + "[modifierName="      + modifierName      + "]\n"
            + "[userGroupName="     + userGroupName     + "]\n"
            + "[createdDate="       + createdDate       + "]\n"
            + "[modificationDate="  + modificationDate  + "]\n";
    }
}
