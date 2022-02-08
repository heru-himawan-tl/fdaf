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
package fdaf.logic.base;

import fdaf.logic.tools.EntityFieldChecker;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.Transient;

public class SearchPredicateFactory<E> {

    private Class<E> entityClass;

    public SearchPredicateFactory(final Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public List<Predicate> predictDecimal(CriteriaBuilder criteriaBuilder, Root<E> root, String keyword, Field field) {
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (!field.isAnnotationPresent(Transient.class)) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            if (keyword.matches("^([0-9]+)?\\.([0-9]+)$")) {
                if (fieldType.equals(BigDecimal.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), BigDecimal.valueOf(Double.parseDouble(keyword)));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(Double.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), Double.parseDouble(keyword));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(double.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), (double) Double.parseDouble(keyword));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(Float.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), Float.parseFloat(keyword));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(float.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), (float) Float.parseFloat(keyword));
                    predicateList.add(predicate);
                }
            }
        }
        return predicateList;
    }

    public List<Predicate> predictInteger(CriteriaBuilder criteriaBuilder, Root<E> root, String keyword, Field field) {
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (!field.isAnnotationPresent(Transient.class)) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            if (keyword.matches("^([0-9]+)$")) {
                if (fieldType.equals(BigDecimal.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), BigDecimal.valueOf(Long.parseLong(keyword)));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(BigInteger.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), BigInteger.valueOf(Long.parseLong(keyword)));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(int.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), (int) Integer.parseInt(keyword));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(Integer.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), Integer.parseInt(keyword));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(long.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), (long) Long.parseLong(keyword));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(Long.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), Long.parseLong(keyword));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(short.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), (short) Short.parseShort(keyword));
                    predicateList.add(predicate);
                }
                if (fieldType.equals(Short.class)) {
                    Predicate predicate = criteriaBuilder.equal(root.get(fieldName), Short.parseShort(keyword));
                    predicateList.add(predicate);
                }
            }
        }
        return predicateList;
    }

    public List<Predicate> predictDate(CriteriaBuilder criteriaBuilder, Root<E> root, String keyword, Field field) {
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (!field.isAnnotationPresent(Transient.class)) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            if (keyword.matches("^([0-9]+)(\\-|\\/)([0-9]+)(\\-|\\/)([0-9]+)$")) {
                String part1 = keyword.replaceAll("(^[0-9]+)(\\-|\\/)([0-9]+)(\\-|\\/)([0-9]+$)", "$1");
                String part2 = keyword.replaceAll("(^[0-9]+)(\\-|\\/)([0-9]+)(\\-|\\/)([0-9]+$)", "$3");
                String part3 = keyword.replaceAll("(^[0-9]+)(\\-|\\/)([0-9]+)(\\-|\\/)([0-9]+$)", "$5");
                String separator = keyword.replaceAll("(^[0-9]+)(\\-|\\/)(.*$)", "$2").trim();
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd" + separator + "MM" + separator + "yyyy");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy" + separator + "MM" + separator + "dd");
                try {
                    if (fieldType.equals(Calendar.class)) {
                        java.util.Date date1 = dateFormat1.parse(part1 + separator + part2 + separator + part3);
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(date1);
                        Predicate predicate1 = criteriaBuilder.equal(root.get(fieldName), cal1);
                        predicateList.add(predicate1);
                        java.util.Date date2 = dateFormat1.parse(part2 + separator + part2 + separator + part3);
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(date2);
                        Predicate predicate2 = criteriaBuilder.equal(root.get(fieldName), cal2);
                        predicateList.add(predicate2);
                    }
                    if (fieldType.equals(java.util.Date.class)) {
                        java.util.Date date1 = dateFormat1.parse(part1 + separator + part2 + separator + part3);
                        Predicate predicate1 = criteriaBuilder.equal(root.get(fieldName), date1);
                        predicateList.add(predicate1);
                        java.util.Date date2 = dateFormat1.parse(part2 + separator + part2 + separator + part3);
                        Predicate predicate2 = criteriaBuilder.equal(root.get(fieldName), date2);
                        predicateList.add(predicate2);
                    }
                    if (fieldType.equals(java.sql.Date.class)) {
                        java.sql.Date date1 = (java.sql.Date) dateFormat1.parse(part1 + separator + part2 + separator + part3);
                        Predicate predicate1 = criteriaBuilder.equal(root.get(fieldName), date1);
                        predicateList.add(predicate1);
                        java.sql.Date date2 = (java.sql.Date) dateFormat1.parse(part2 + separator + part2 + separator + part3);
                        Predicate predicate2 = criteriaBuilder.equal(root.get(fieldName), date2);
                        predicateList.add(predicate2);
                    }
                } catch (ParseException e) {
                }
            }
        }
        return predicateList;
    }

    public List<Predicate> predictTimestamp(CriteriaBuilder criteriaBuilder, Root<E> root, String keyword, Field field) {
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (!field.isAnnotationPresent(Transient.class)) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            if (keyword.matches("^[0-9]+\\-[0-9]+\\-[0-9]+ [0-9]+\\:[0-9]+\\:[0-9]+(\\.[0-9]+)?$")
                && fieldType.equals(java.sql.Timestamp.class)) {
                Predicate predicate = criteriaBuilder.equal(root.get(fieldName), java.sql.Timestamp.valueOf(keyword));
                predicateList.add(predicate);
            }
        }
        return predicateList;
    }

    public List<Predicate> predictString(CriteriaBuilder criteriaBuilder, Root<E> root, String keyword, Field field) {
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (!field.isAnnotationPresent(Transient.class)) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getName();
            if (fieldType.equals(String.class)) {
                Predicate predicate0 = criteriaBuilder.like(root.get(fieldName).as(String.class), "%" + keyword.toUpperCase() + "%");
                predicateList.add(predicate0);
                Predicate predicate1 = criteriaBuilder.like(root.get(fieldName).as(String.class), "%" + keyword.toLowerCase() + "%");
                predicateList.add(predicate1);
                Predicate predicate2 = criteriaBuilder.like(root.get(fieldName).as(String.class), "%" + keyword + "%");
                predicateList.add(predicate2);
            }
        }
        return predicateList;
    }

    public List<Predicate> predictAll(CriteriaBuilder criteriaBuilder, Root<E> root, String keyword, Field field) {
        List<Predicate> predicateList0 = new ArrayList<Predicate>();
        if (!field.isAnnotationPresent(Transient.class)) {
            List<Predicate> predicateList1 = predictDecimal(criteriaBuilder, root, keyword, field);
            List<Predicate> predicateList2 = predictInteger(criteriaBuilder, root, keyword, field);
            List<Predicate> predicateList3 = predictDate(criteriaBuilder, root, keyword, field);
            List<Predicate> predicateList4 = predictTimestamp(criteriaBuilder, root, keyword, field);
            List<Predicate> predicateList5 = predictString(criteriaBuilder, root, keyword, field);
            if (!predicateList1.isEmpty()) {
                predicateList0.addAll(predicateList1);
            }
            if (!predicateList2.isEmpty()) {
                predicateList0.addAll(predicateList2);
            }
            if (!predicateList3.isEmpty()) {
                predicateList0.addAll(predicateList3);
            }
            if (!predicateList4.isEmpty()) {
                predicateList0.addAll(predicateList4);
            }
            if (!predicateList5.isEmpty()) {
                predicateList0.addAll(predicateList5);
            }
        }
        return predicateList0;
    }

    public List<Predicate> predictAll(CriteriaBuilder criteriaBuilder, Root<E> root, String[] keywords) {
        List<Predicate> predicateList0 = new ArrayList<Predicate>();
        for (String keyword : keywords) {
            if (!keyword.trim().isEmpty()) {
                for (Field field : entityClass.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(Transient.class)) {
                        if (EntityFieldChecker.isPersistableField(field) && !EntityFieldChecker.isVersionField(field)
                            && !EntityFieldChecker.isStaticField(field)) {
                            List<Predicate> predicateList1 = predictAll(criteriaBuilder, root, keyword.trim(), field);
                            if (!predicateList1.isEmpty()) {
                                predicateList0.addAll(predicateList1);
                            }
                        }
                    }
                }
            }
        }
        return predicateList0;
    }
}
