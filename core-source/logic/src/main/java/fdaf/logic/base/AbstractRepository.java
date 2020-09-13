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

import fdaf.base.OrderingMode;
import fdaf.base.Permission;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

public abstract class AbstractRepository<E> {

    protected Class<E> entityClass;

    protected AbstractRepository(final Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();
    
    public EntityManager getCurrentEntityManager() {
        return getEntityManager();
    }

    @Transactional
    public void create(E entity) {
        getEntityManager().persist(entity);
    }

    @Transactional
    public void update(E entity) {
        getEntityManager().merge(entity);
    }

    @Transactional
    public void remove(E entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    @Transactional
    public void refresh(E entity) {
        getEntityManager().refresh(getEntityManager().merge(entity));
    }

    public E find(Object primaryKey) {
        return getEntityManager().find(entityClass, primaryKey);
    }

    public Specification<E> presetSpecification() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        Specification<E> specification = new Specification<E>();
        specification.setBuilder(criteriaBuilder);
        specification.setQuery(criteriaQuery);
        specification.setRoot(root);
        return specification;
    }
    
    public List<E> findAll(Specification<E> specification, String orderParameter, OrderingMode orderMode) {
        CriteriaBuilder criteriaBuilder = specification.getBuilder();
        CriteriaQuery<E> criteriaQuery = specification.getQuery();
        Root<E> root = specification.getRoot();
        Path<E> path = root.get(orderParameter);
        Order order = (orderMode == OrderingMode.DESC) ? criteriaBuilder.desc(path) : criteriaBuilder.asc(path);
        Predicate predicate = specification.getPredicate();
        criteriaQuery.select(root).orderBy(order);
        if (predicate != null) {
            criteriaQuery.where(predicate);
        }
        TypedQuery<E> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }
    
    public List<E> findAll(Specification<E> specification) {
        CriteriaQuery<E> criteriaQuery = specification.getQuery();
        Root<E> root = specification.getRoot();
        Predicate predicate = specification.getPredicate();
        criteriaQuery.select(root);
        if (predicate != null) {
            criteriaQuery.where(predicate);
        }
        TypedQuery<E> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    public E find(Specification<E> specification) {
        CriteriaQuery<E> criteriaQuery = specification.getQuery();
        Root<E> root = specification.getRoot();
        Predicate predicate = specification.getPredicate();
        criteriaQuery.select(root);
        if (predicate != null) {
            criteriaQuery.where(predicate);
        }
        TypedQuery<E> query = getEntityManager().createQuery(criteriaQuery);
        query.setMaxResults(1);
        List<E> resultList = query.getResultList();
        return (resultList.isEmpty()) ? null : resultList.get(0);
    }

    public List<E> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        Path<E> path = root.get(orderParameter);
        Order order = (orderMode == OrderingMode.DESC) ? criteriaBuilder.desc(path) : criteriaBuilder.asc(path);
        TypedQuery<E> query = getEntityManager().createQuery(criteriaQuery.select(root).orderBy(order));
        return query.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    public List<E> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, String[] keywords) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        Path<E> path = root.get(orderParameter);
        Order order = (orderMode == OrderingMode.DESC) ? criteriaBuilder.desc(path) : criteriaBuilder.asc(path);
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (keywords != null && keywords.length != 0) {
            SearchPredicateFactory<E> predicateFactory = new SearchPredicateFactory<E>(entityClass);
            predicateList = predicateFactory.predictAll(criteriaBuilder, root, keywords);
            if (predicateList.isEmpty()) {
                return new ArrayList<E>();
            }
        }
        criteriaQuery.select(root);
        if (!predicateList.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.or(predicateList.toArray(new Predicate[]{})));
        }
        criteriaQuery.orderBy(order);
        TypedQuery<E> query = getEntityManager().createQuery(criteriaQuery);
        return query.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    public List<E> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, Long authorId, Long userGroupId, String[] keywords) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        Path<E> path = root.get(orderParameter);
        Order order = (orderMode == OrderingMode.DESC) ? criteriaBuilder.desc(path) : criteriaBuilder.asc(path);
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (keywords != null && keywords.length != 0) {
            SearchPredicateFactory<E> predicateFactory = new SearchPredicateFactory<E>(entityClass);
            predicateList = predicateFactory.predictAll(criteriaBuilder, root, keywords);
            if (predicateList.isEmpty()) {
                return new ArrayList<E>();
            }
        }
        PermissionPredicateFactory permissionPredicateFactory = new PermissionPredicateFactory<E>(criteriaBuilder, root, authorId, userGroupId);
        criteriaQuery.select(root);
        if (!predicateList.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(permissionPredicateFactory.isAccessible(), criteriaBuilder.or(predicateList.toArray(new Predicate[]{}))));
        } else {
            criteriaQuery.where(permissionPredicateFactory.isAccessible());
        }
        criteriaQuery.orderBy(order);
        TypedQuery<E> query = getEntityManager().createQuery(criteriaQuery);
        return query.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    public List<E> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, Long authorId, String[] keywords) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        Path<E> path = root.get(orderParameter);
        Order order = (orderMode == OrderingMode.DESC) ? criteriaBuilder.desc(path) : criteriaBuilder.asc(path);
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (keywords != null && keywords.length != 0) {
            SearchPredicateFactory<E> predicateFactory = new SearchPredicateFactory<E>(entityClass);
            predicateList = predicateFactory.predictAll(criteriaBuilder, root, keywords);
            if (predicateList.isEmpty()) {
                return new ArrayList<E>();
            }
        }
        PermissionPredicateFactory permissionPredicateFactory = new PermissionPredicateFactory<E>(criteriaBuilder, root, authorId, 0L);
        criteriaQuery.select(root);
        if (!predicateList.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(permissionPredicateFactory.isBelongTo(), criteriaBuilder.or(predicateList.toArray(new Predicate[]{}))));
        } else {
            criteriaQuery.where(permissionPredicateFactory.isBelongTo());
        }
        criteriaQuery.orderBy(order);
        TypedQuery<E> query = getEntityManager().createQuery(criteriaQuery);
        return query.setFirstResult(offset).setMaxResults(limit).getResultList();
    }
    
    public List<E> list(String orderParameter, OrderingMode orderMode) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        Path<E> path = root.get(orderParameter);
        Order order = (orderMode == OrderingMode.DESC) ? criteriaBuilder.desc(path) : criteriaBuilder.asc(path);
        criteriaQuery.select(root);
        criteriaQuery.orderBy(order);
        TypedQuery<E> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<E> list() {
        CriteriaQuery<E> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        return getEntityManager().createQuery(criteriaQuery.select(root)).getResultList();
    }

    public int count(Long authorId, Long userGroupId, String[] keywords) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<E> root = criteriaQuery.from(entityClass);
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (keywords != null && keywords.length != 0) {
            SearchPredicateFactory<E> predicateFactory = new SearchPredicateFactory<E>(entityClass);
            predicateList = predicateFactory.predictAll(criteriaBuilder, root, keywords);
            if (predicateList.isEmpty()) {
                return 0;
            }
        }
        PermissionPredicateFactory permissionPredicateFactory = new PermissionPredicateFactory<E>(criteriaBuilder, root, authorId, userGroupId);
        criteriaQuery.select(criteriaBuilder.count(root));
        if (!predicateList.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(permissionPredicateFactory.isAccessible(), criteriaBuilder.or(predicateList.toArray(new Predicate[]{}))));
        } else {
            criteriaQuery.where(permissionPredicateFactory.isAccessible());
        }
        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        return ((Long) query.getSingleResult()).intValue();
    }

    public int count(Long authorId, String[] keywords) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<E> root = criteriaQuery.from(entityClass);
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (keywords != null && keywords.length != 0) {
            SearchPredicateFactory<E> predicateFactory = new SearchPredicateFactory<E>(entityClass);
            predicateList = predicateFactory.predictAll(criteriaBuilder, root, keywords);
            if (predicateList.isEmpty()) {
                return 0;
            }
        }
        PermissionPredicateFactory permissionPredicateFactory = new PermissionPredicateFactory<E>(criteriaBuilder, root, authorId, 0L);
        criteriaQuery.select(criteriaBuilder.count(root));
        if (!predicateList.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(permissionPredicateFactory.isBelongTo(), criteriaBuilder.or(predicateList.toArray(new Predicate[]{}))));
        } else {
            criteriaQuery.where(permissionPredicateFactory.isBelongTo());
        }
        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        return ((Long) query.getSingleResult()).intValue();
    }

    public int count(String[] keywords) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<E> root = criteriaQuery.from(entityClass);
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (keywords != null && keywords.length != 0) {
            SearchPredicateFactory<E> predicateFactory = new SearchPredicateFactory<E>(entityClass);
            predicateList = predicateFactory.predictAll(criteriaBuilder, root, keywords);
            if (predicateList.isEmpty()) {
                return 0;
            }
        }
        criteriaQuery.select(criteriaBuilder.count(root));
        if (!predicateList.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.or(predicateList.toArray(new Predicate[]{})));
        }
        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        return ((Long) query.getSingleResult()).intValue();
    }

    public int count() {
        CriteriaQuery<Long> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
        Root<E> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(getEntityManager().getCriteriaBuilder().count(root));
        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        return ((Long) query.getSingleResult()).intValue();
    }
}
