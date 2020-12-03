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
import fdaf.logic.base.AbstractRepository;
import fdaf.logic.tools.EntityFieldChecker;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;

public abstract class AbstractFacade<R extends AbstractRepository<E>, E extends Object> {

    protected List<String> orderParameters = new ArrayList<String>();
    protected UpdateCallbackInterface<R, E> updateCallback;
    protected Class<E> entityClass;
    protected E entity;
    protected Object primaryKey;
    protected Long authorId;
    protected Long userGroupId;
    protected Long modifierId;
    protected String recordDate = "";
    protected String uuid;

    protected AbstractFacade(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract R getRepository();

    @PostConstruct
    public void postConstruct() {
        int dataPropertyCount = 0;
        for (Field field : entityClass.getDeclaredFields()) {
            if (EntityFieldChecker.isNotPersistableField(field)) {
                continue;
            }
            if (EntityFieldChecker.isDataPropertyField(field)) {
                dataPropertyCount++;
            }
            if (EntityFieldChecker.isNotOrderParameterField(field)) {
                continue;
            }
            orderParameters.add(field.getName());
        }
        presetUpdateCallback();
        updateCallback.setRepository(getRepository());
    }

    protected void presetUpdateCallback() {
        updateCallback = new UpdateCallbackInterface<R, E>() {
            private String customMessage;
            private String message;
            private E entity;
            public void setRepository(R repository) {
            }
            public void setEntity(E entity) {
                this.entity = entity;
            }
            public E getEntity() {
                return entity;
            }
            public void onPrepareUpdateTask() {
                // NO-OP
            }
            public void onSaveCreateTask() {
                // NO-OP
            }
            public void onSaveUpdateTask() {
                // NO-OP
            }
            public boolean preCreateCheck() {
                return true;
            }
            public void rollbackCreateTask() {
                // NO-OP
            }
            public void postCreateTask() {
                // NO-OP
            }
            public boolean preUpdateCheck() {
                return true;
            }
            public void postUpdateTask() {
                // NO-OP
            }
            public void setMessage(String message) {
                this.message = message;
            }
            public String getMessage() {
                return message;
            }
            public void setCustomMessage(String customMessage) {
                this.customMessage = customMessage;
            }
            public String getCustomMessage() {
                return customMessage;
            }
        };
    }

    public List<String> getOrderParameters() {
        return orderParameters;
    }

    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public void setUserGroupId(Long userGroupId) {
        this.userGroupId = userGroupId;
    }

    protected void updateDataProperties(boolean updateMode) {
        // NO-OP
    }

    protected void updateRecordDate() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        recordDate = LocalDateTime.now().format(format);
    }

    protected abstract  E newEntity();

    protected abstract void setUuid();

    public void prepareCreate() {
        uuid = UUID.randomUUID().toString();
        entity = newEntity();
        updateDataProperties(false);
        setUuid();
    }

    public boolean preCreateCheck() {
        updateCallback.setEntity(entity);
        return updateCallback.preCreateCheck();
    }

    public void create() {
        updateDataProperties(false);
        getRepository().create(entity);
        updateCallback.setEntity(entity);
        updateCallback.onSaveCreateTask();
    }

    public void postCreateTask() {
        updateCallback.setEntity(entity);
        updateCallback.postCreateTask();
    }

    public void rollbackCreateTask() {
        updateCallback.setEntity(entity);
        updateCallback.rollbackCreateTask();
    }
    
    public void inlineUpdate(Object primaryKey, E entity) {
        if (isDataExists(primaryKey)) {
            getRepository().update(entity);
        }
    }

    public void reloadNewEntity() {
        Specification<E> spec = getRepository().presetSpecification();
        spec.setPredicate(spec.getBuilder().equal(spec.getRoot().get("uuid"), uuid));
        entity = getRepository().find(spec);
    }

    public abstract Object getNewRecordId();

    public List<E> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit) {
        return getRepository().list(orderParameter, orderMode, offset, limit);
    }

    public List<E> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, String[] keywords) {
        return getRepository().list(orderParameter, orderMode, offset, limit, keywords);
    }

    public List<E> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, Long authorId, Long userGroupId, String[] keywords) {
        return getRepository().list(orderParameter, orderMode, offset, limit, authorId, userGroupId, keywords);
    }

    public List<E> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, Long authorId, String[] keywords) {
        return getRepository().list(orderParameter, orderMode, offset, limit, authorId, keywords);
    }
    
    public List<E> list(String orderParameter, OrderingMode orderMode) {
        return getRepository().list(orderParameter, orderMode);
    }

    public List<E> list() {
        return getRepository().list();
    }

    public boolean isDataExists(Object primaryKey) {
        return (getRepository().find(primaryKey) != null);
    }

    public void setEntity(E entity) {
        this.entity = entity;
    }

    public E getEntity() {
        return entity;
    }

    public int count(Long authorId, Long userGroupId, String[] keywords) {
        return getRepository().count(authorId, userGroupId, keywords);
    }

    public int count(Long authorId, String[] keywords) {
        return getRepository().count(authorId, keywords);
    }

    public int count(String[] keywords) {
        return getRepository().count(keywords);
    }

    public int count() {
        return getRepository().count();
    }

    public void prepareUpdate(Object primaryKey) {
        this.primaryKey = primaryKey;
        entity = getRepository().find(primaryKey);
        updateCallback.setEntity(entity);
        updateCallback.onPrepareUpdateTask();
        entity = updateCallback.getEntity();
    }

    public boolean preUpdateCheck() {
        updateCallback.setEntity(entity);
        boolean ret = updateCallback.preUpdateCheck();
        entity = updateCallback.getEntity();
        return ret;
    }

    public void update() {
        updateDataProperties(true);
        getRepository().update(entity);
        updateCallback.setEntity(entity);
        updateCallback.onSaveUpdateTask();
        entity = updateCallback.getEntity();
    }

    public void postUpdateTask() {
        updateCallback.setEntity(entity);
        updateCallback.postUpdateTask();
        entity = updateCallback.getEntity();
    }

    public void takeover() {
        updateDataProperties(false);
        getRepository().update(entity);
    }

    public void reloadEntity() {
        Specification<E> spec = getRepository().presetSpecification();
        spec.setPredicate(spec.getBuilder().equal(spec.getRoot().get("id"), primaryKey));
        entity = getRepository().find(spec);
    }

    public void remove(Object primaryKey) {
        E entity = getRepository().find(primaryKey);
        if (entity != null) {
            getRepository().remove(entity);
        }
    }

    public void disposeEntity() {
        entity = null;
    }
    
    protected void setCustomMessage(String customMessage) {
        updateCallback.setCustomMessage(customMessage);
    }

    public String getCustomMessage() {
        return updateCallback.getCustomMessage();
    }

    protected void setMessage(String message) {
        updateCallback.setMessage(message);
    }

    public String getMessage() {
        return updateCallback.getMessage();
    }
    
    public void customFacadeTask(Object parameter) {
        // NO-OP
    }
}
