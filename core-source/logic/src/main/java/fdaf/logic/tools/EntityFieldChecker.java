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

import java.lang.reflect.Field;
import javax.persistence.Version;

public class EntityFieldChecker {
    public static final Class<?>[] PERSISTABLE_FIELD_TYPE_CLASSES = {
        boolean.class   , Boolean.class     , byte.class        ,
        Byte.class      , Character.class   , char.class        ,
        double.class    , Double.class      , float.class       ,
        Float.class     , int.class         , Integer.class     ,
        long.class      , Long.class        , short.class       ,
        Short.class     , String.class      ,
        java.math.BigDecimal.class          ,
        java.math.BigInteger.class          ,
        java.sql.Date.class                 ,
        java.sql.Time.class                 ,
        java.sql.Timestamp.class            ,
        java.util.Calendar.class            ,
        java.util.Date.class                ,
        fdaf.base.Permission.class
    };

    public static boolean isPersistableField(Field field) {
        for (Class<?> type : PERSISTABLE_FIELD_TYPE_CLASSES) {
            if (field.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotPersistableField(Field field) {
        return (!EntityFieldChecker.isPersistableField(field)
                || EntityFieldChecker.isVersionField(field)
                || EntityFieldChecker.isStaticField(field));
    }

    public static boolean isVersionField(Field field) {
        return field.isAnnotationPresent(Version.class);
    }

    public static boolean isStaticField(Field field) {
        return field.toString().matches(".*static final.*");
    }

    public static boolean isDataPropertyField(Field field) {
        String fieldName = field.getName();
        return (fieldName.equals("modificationDate")
                || fieldName.equals("createdDate")
                || fieldName.equals("modifierId")
                || fieldName.equals("userGroupId")
                || fieldName.equals("authorId")
                || fieldName.equals("permission"));
    }

    public static boolean isNotOrderParameterField(Field field) {
        String fieldName = field.getName();
        return (fieldName.equals("modifierId")
                || fieldName.equals("userGroupId")
                || fieldName.equals("authorId")
                || fieldName.equals("permission")
                || fieldName.equals("uuid"));
    }
}
