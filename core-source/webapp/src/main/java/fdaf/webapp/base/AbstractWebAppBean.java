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

import fdaf.base.FacadeInterface;
import fdaf.base.OrderingMode;
import fdaf.base.Permission;
import fdaf.base.UserType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;

public abstract class AbstractWebAppBean extends AbstractBaseWebAppBean {

    protected WebAppOpMode opMode = WebAppOpMode.LISTING;

    protected SelectItem[] orderParameters = new SelectItem[]{};
    protected String orderParameter;
    protected String orderParameterBackup;
    protected String orderParameterBackupPS;
    protected OrderingMode orderMode = OrderingMode.ASC;
    protected OrderingMode orderModeBackup = OrderingMode.ASC;
    protected OrderingMode orderModeBackupPS = OrderingMode.ASC;
    protected Integer limit = 25;
    protected Integer limitBackup = 1;
    protected Integer limitBackupPS = 1;
    protected Integer totalRowCount = 0;

    protected SelectItem[] pageNumbers = new SelectItem[]{};
    protected Integer pageCount = 1;
    protected boolean usingMainPagination;
    protected boolean usingMainPaginationBackup;
    protected boolean usingMainPaginationBackupPS;
    protected Integer currentPage = 1;
    protected Integer currentPageAlt = 1;
    protected Integer currentPageBackup = 1;
    protected Integer currentPageAltBackup = 1;
    protected Integer currentPageBackupPS = 1;
    protected Integer currentPageAltBackupPS = 1;

    protected boolean searchModeInitialized;
    protected boolean prematureSearchMode;
    protected boolean searchMode;
    protected String keyword;
    protected String[] keywords;

    protected ResultObject[] resultList = new ResultObject[]{};
    protected boolean dataListIsError;

    protected Object primaryKey;
    protected HashMap<Object, Object[]> primaryKeyMap = new HashMap<Object, Object[]>();
    protected Object[] dummyPrimaryKeys = new Object[]{};

    protected boolean selectAllFlag;
    protected boolean applyDeselectAll;
    protected boolean applySelectAll;
    protected boolean massiveRemovalReadyState;

    protected boolean disableValidation;
    protected boolean saveAndClose;
    protected boolean newRecordInserted;

    protected ResultObject resultObject;
    protected Object entity;

    protected SelectItem[] permissions = new SelectItem[]{};

    protected boolean prepareMassiveRemovalOp;
    protected boolean prepareRemoveOp;
    protected boolean takeoverOp;
    
    protected boolean defaultReadonlyForAll;
    
    protected boolean withoutDataProperties;
    protected boolean checkOrphanDataMode;
    
    protected boolean allowEdit = true;
    protected boolean allowAdd = true;
    
    protected boolean inlineUpdateMode;
    protected Object extEntity;
    
    protected String lastKnownNewRecordId;
    protected String infoMessage;

    protected AbstractWebAppBean() {
        // NO-OP
    }
    
    protected abstract FacadeInterface getFacade();

    @SuppressWarnings("unchecked")
    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();
        List<SelectItem> orderParametersTemp = new ArrayList<SelectItem>();
        List<SelectItem> permissionsTemp = new ArrayList<SelectItem>();
        if (getFacade() != null) {
            for (String fieldName : getFacade().getOrderParameters()) {
                SelectItem selectItem = new SelectItem();
                selectItem.setLabel(fieldName.replaceAll("([a-z])([A-Z])", "$1 $2").toUpperCase());
                selectItem.setValue(fieldName);
                orderParametersTemp.add(selectItem);
                orderParameter = (orderParameter == null) ? fieldName : orderParameter;
            }
            if (!orderParametersTemp.isEmpty()) {
                orderParameters = orderParametersTemp.toArray(new SelectItem[]{});
            }
        }
        for (Permission p : Permission.values()) {
            SelectItem selectItem = new SelectItem();
            selectItem.setLabel(p.toString().replaceAll("[_]+", " "));
            selectItem.setValue(p.toString());
            permissionsTemp.add(selectItem);
        }
        permissions = permissionsTemp.toArray(new SelectItem[]{});
        postConstructTask();
    }

    protected void postConstructTask() {
        // NO-OP
    }
    
    public void unsetCheckOrphanDataMode(AjaxBehaviorEvent event) throws AbortProcessingException {
        checkOrphanDataMode = false;
    }
    
    public void setCheckOrphanDataMode(AjaxBehaviorEvent event) throws AbortProcessingException {
        checkOrphanDataMode = true;
    }
    
    public boolean getInCheckOrphanDataMode() {
        return checkOrphanDataMode;
    }
    
    public void configDefaultReadonlyForAll(ComponentSystemEvent event) throws AbortProcessingException {
        defaultReadonlyForAll = true;
    }
    
    public void configWithoutDataProperties(ComponentSystemEvent event) throws AbortProcessingException {
        withoutDataProperties = true;
    }
    
    public boolean getNoDataProperties() {
        return withoutDataProperties;
    }
    
    public void disallowEdit(ComponentSystemEvent event) throws AbortProcessingException {
        allowEdit = false;
    }
    
    public boolean getAllowEdit() {
        return allowEdit;
    }
    
    public void disallowAdd(ComponentSystemEvent event) throws AbortProcessingException {
        allowAdd = false;
    }
    
    public boolean getAllowAdd() {
        return allowAdd;
    }
    
    
    public boolean getDefaultReadonlyForAll() {
        return defaultReadonlyForAll;
    }

    public void setOpMode(WebAppOpMode opMode) {
        this.opMode = opMode;
    }

    public WebAppOpMode getOpMode() {
        return opMode;
    }

    protected boolean isUpdateMode() {
        return (opMode == WebAppOpMode.UPDATE);
    }

    public void setDisableValidation(boolean disableValidation) {
        this.disableValidation = disableValidation;
    }

    public boolean getDisableValidation() {
        return disableValidation;
    }

    public void setPrimaryKey(Object primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Object getPrimaryKey() {
        return primaryKey;
    }

    public SelectItem[] getPermissions() {
        return permissions;
    }

    public void prepareCreate(AjaxBehaviorEvent event) throws AbortProcessingException {
        opMode = WebAppOpMode.CREATE;
        disableValidation = false;
        exitSearch(null);
        getFacade().prepareCreate();
        presetEntity();
    }

    protected void presetEntity() {
        entity = getFacade().getEntity();
    }

    public Object getEntity() {
        return entity;
    }

    public void executeCreateAlt(AjaxBehaviorEvent event) throws AbortProcessingException {
        saveAndClose = true;
        executeCreate(null);
    }

    protected void feedBackEntity() {
        getFacade().setEntity(entity);
    }

    public void executeCreate(AjaxBehaviorEvent event) throws AbortProcessingException {
        try {
            feedBackEntity();
            if (getFacade().preCreateCheck()) {
                getFacade().setUserGroupId(userGroupId);
                getFacade().setModifierId(modifierId);
                getFacade().setAuthorId(authorId);
                onCreateSubmitTask();
                getFacade().create();
                newRecordInserted = true;
                getFacade().reloadNewEntity();
                presetEntity();
                feedBackEntity();
                getFacade().postCreateTask();
                addMessage(SV_INFO, "createRecordSuccess", String.valueOf(getFacade().getNewRecordId()));
                lastKnownNewRecordId = String.valueOf(getFacade().getNewRecordId());
                infoMessage = "createRecordSuccess";
                if (saveAndClose) {
                    opMode = WebAppOpMode.LISTING;
                    saveAndClose = false;
                    onCreateFinishTask();
                    disposeEntity();
                    return;
                }
                getFacade().reloadNewEntity();
                presetEntity();
                opMode = WebAppOpMode.UPDATE;
                onCreateFinishTask();
            } else {
                addPreUpdateCheckWarnCustomMessage();
                addPreUpdateCheckWarnMessage();
            }
        } catch (Exception e) {
            indicateServiceError(e);
            rollbackCreateTask();
        }
    }
    
    protected void addPreUpdateCheckWarnCustomMessage() {
        String message = getFacade().getCustomMessage();
        if (message != null) {
            if (message.matches(".*\\-.*")) {
                for (String m : message.split("\\-")) {
                    addCustomCallbackMessage(SV_WARN, m);
                }
            } else {
                addCustomCallbackMessage(SV_WARN, message);
            }
        }
    }
    
    protected void addPreUpdateCheckWarnMessage() {
        String message = getFacade().getMessage();
        if (message != null) {
            if (message.matches(".*\\-.*")) {
                for (String m : message.split("\\-")) {
                    addCallbackMessage(SV_WARN, m);
                }
            } else {
                addCallbackMessage(SV_WARN, message);
            }
        }
    }
    
    protected void onCreateSubmitTask() {
        // NO-OP
    }
    
    protected void onUpdateSubmitTask() {
        // NO-OP
    }
    
    protected void onCreateFinishTask() {
        // NO-OP
    }
    
    protected void onUpdateFinishTask() {
        // NO-OP
    }

    protected void rollbackCreateTask() {
        try {
            getFacade().rollbackCreateTask();
        } catch (Exception e) {
        }
    }

    protected void disposeEntity() {
        getFacade().disposeEntity();
        entity = null;
    }

    public SelectItem[] getOrderParameters() {
        return orderParameters;
    }

    public void setOrderParameter(String orderParameter) {
        this.orderParameter = orderParameter;
    }

    public String getOrderParameter() {
        return orderParameter;
    }

    public void setOrderMode(OrderingMode orderMode) {
        this.orderMode = orderMode;
    }

    public OrderingMode getOrderMode() {
        return orderMode;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getTotalRowCount() {
        return totalRowCount;
    }

    public void populateCountOfRows(ComponentSystemEvent event) throws AbortProcessingException {
        if (prematureSearchMode) {
            return;
        }
        try {
            if (loggedOn && !brokenUserSession) {
                if (!withoutDataProperties && !checkOrphanDataMode) {
                    totalRowCount = getFacade().count(authorId, userGroupId, keywords);
                } else {
                    totalRowCount = getFacade().count(keywords);
                }
            } else {
                totalRowCount = 0;
            }
        } catch (Exception e) {
            indicateServiceError(e);
            totalRowCount = 0;
        }
    }

    public void populatePageNumbers(ComponentSystemEvent event) throws AbortProcessingException {
        if (prematureSearchMode) {
            return;
        }
        pageCount = (totalRowCount != null && limit != null && totalRowCount > 0 && totalRowCount > limit)
            ? (int) Math.ceil((double) totalRowCount / (double) limit) : 1;
        List<SelectItem> numberList = new ArrayList<SelectItem>();
        if (totalRowCount > 0) {
            for (Integer count = 1; count <= pageCount; count++) {
                SelectItem selectItem = new SelectItem();
                selectItem.setLabel(String.valueOf(count));
                selectItem.setValue(count);
                numberList.add(selectItem);
            }
        }
        if (newRecordInserted) {
            currentPageAlt = pageCount;
            currentPage = pageCount;
        }
        newRecordInserted = false;
        saveAndClose = false;
        if (!numberList.isEmpty()) {
            pageNumbers = numberList.toArray(new SelectItem[]{});
        }
    }

    public SelectItem[] getPageNumbers() {
        return pageNumbers;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void changePageByMainPagination(AjaxBehaviorEvent event) throws AbortProcessingException {
        usingMainPagination = true;
    }

    public void changePage(AjaxBehaviorEvent event) throws AbortProcessingException {
        usingMainPagination = false;
    }

    public void toNextPage(AjaxBehaviorEvent event) throws AbortProcessingException {
        usingMainPagination = true;
        if (currentPage < pageCount) {
            currentPage += 1;
        }
    }

    public void toPrevPage(AjaxBehaviorEvent event) throws AbortProcessingException {
        usingMainPagination = true;
        if (currentPage > 1) {
            currentPage -= 1;
        }
    }

    public void setCurrentPageAlt(Integer currentPageAlt) {
        this.currentPageAlt = currentPageAlt;
    }

    public Integer getCurrentPageAlt() {
        return currentPageAlt;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getCurrentPage() {
        return (usingMainPagination) ? currentPage : currentPageAlt;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void configureSearch(AjaxBehaviorEvent event) throws AbortProcessingException {
        searchModeInitialized = true;
        if (keyword != null && !keyword.trim().isEmpty()) {
            keywords = (keyword.matches(".*[;,\\:\\|\\!\\#].*")) ? keyword.split("[;,\\:\\|\\!\\#]+") : new String[]{keyword.trim()};
            primaryKey = null;
            getFacade().disposeEntity();
            if (!searchMode) {
                usingMainPaginationBackup = usingMainPagination;
                currentPageAltBackup = currentPageAlt;
                currentPageBackup = currentPage;
                orderModeBackup = orderMode;
                limitBackup = limit;
                orderParameterBackup = orderParameter;
            }
            searchMode = true;
            return;
        }
        usingMainPaginationBackupPS = usingMainPagination;
        currentPageAltBackupPS = currentPageAlt;
        currentPageBackupPS = currentPage;
        limitBackupPS = limit;
        orderModeBackupPS = orderMode;
        orderParameterBackupPS = orderParameter;
        prematureSearchMode = true;
        searchMode = false;
        keywords = null;
        keyword = null;
    }

    public boolean getSearchMode() {
        return searchMode;
    }

    public void exitSearch(AjaxBehaviorEvent event) throws AbortProcessingException {
        if (searchMode) {
            usingMainPagination = usingMainPaginationBackup;
            currentPageAlt = currentPageAltBackup;
            currentPage = currentPageBackup;
            orderMode = orderModeBackup;
            limit = limitBackup;
            orderParameter = orderParameterBackup;
        }
        searchMode = false;
        keywords = null;
        keyword = null;
    }
    
    protected ResultObject presetResultObject(Object data) {
        DataPropertiesReflect dataProperties = new DataPropertiesReflect(data);
        ResultObject resultObject = new ResultObject();
        Long currentUserGroupId = dataProperties.getUserGroupId();
        Long currentAuthorId = dataProperties.getAuthorId();
        Permission permission = dataProperties.getPermission();
        UserType authorUserType = dataProperties.getUserType();
        String authorName = dataProperties.getAuthorName();
        resultObject.setData(data);
        resultObject.setRemovable(true);
        resultObject.setEditable(true);
        if (!withoutDataProperties && !checkOrphanDataMode && (userType == UserType.STAFF || userType == UserType.ADMINISTRATOR) && currentAuthorId != null && currentUserGroupId != null) {
            PermissionCheck permissionCheck = new PermissionCheck(currentAuthorId, authorId, currentUserGroupId, userGroupId, permission);
            resultObject.setRemovable((userType == UserType.ADMINISTRATOR && authorUserType != UserType.STAFF) ? true : permissionCheck.isRemovable());
            resultObject.setEditable((userType == UserType.ADMINISTRATOR && authorUserType != UserType.STAFF) ? true : permissionCheck.isEditable());
        }
        if (userType == UserType.ADMINISTRATOR) {
            resultObject.setRemovable(true);
            resultObject.setEditable(true);
        }
        resultObject.setTakeoverable((userType == UserType.ADMINISTRATOR && authorUserType != UserType.ADMINISTRATOR && currentAuthorId != authorId)
            || ((authorUserType == null || authorName == null) && checkOrphanDataMode && userType == UserType.ADMINISTRATOR));
        resultObject.setUserGroupName(dataProperties.getUserGroupName());
        resultObject.setAuthorId(dataProperties.getAuthorId());
        resultObject.setUserGroupId(dataProperties.getUserGroupId());
        resultObject.setAuthorName(dataProperties.getAuthorName());
        resultObject.setModifierId(dataProperties.getModifierId());
        resultObject.setModifierName(dataProperties.getModifierName());
        return resultObject;
    }
    
    public void view(Object primaryKey) {
        try {
            getFacade().prepareUpdate(primaryKey);
            presetEntity();
            DataPropertiesReflect dataProperties = new DataPropertiesReflect(entity);
            resultObject = presetResultObject(entity);
            resultObject.setUserGroupName(dataProperties.getUserGroupName());
            resultObject.setAuthorId(dataProperties.getAuthorId());
            resultObject.setUserGroupId(dataProperties.getUserGroupId());
            resultObject.setAuthorName(dataProperties.getAuthorName());
            resultObject.setModifierId(dataProperties.getModifierId());
            resultObject.setModifierName(dataProperties.getModifierName());
            resultObject.setCreatedDate(dataProperties.getCreatedDate());
            resultObject.setModificationDate(dataProperties.getModificationDate());
            opMode = WebAppOpMode.VIEW;
        } catch (Exception e) {
            indicateServiceError(e);
        }
    }
    
    public ResultObject getResultObject() {
        return resultObject;
    }
    
    public void exitView() {
        opMode = WebAppOpMode.LISTING;
        resultObject = null;
        disposeEntity();
    }
    
    public void recoverFromPrematureSearchMode(ComponentSystemEvent event) throws AbortProcessingException {
        if ( (prematureSearchMode && searchModeInitialized && currentPage <= 1) 
            || (prematureSearchMode && searchModeInitialized && pageCount <= 1)
            || (prematureSearchMode && !searchModeInitialized)) {
            addMessage(SV_WARN, "emptySearchKeywordWarning");
        }
        if (prematureSearchMode && !searchModeInitialized) {
            usingMainPagination = usingMainPaginationBackupPS;
            currentPageAlt = currentPageAltBackupPS;
            currentPage = currentPageBackupPS;
            limit = limitBackupPS;
            orderMode = orderModeBackupPS;
            orderParameter = orderParameterBackupPS;
            prematureSearchMode = false;
        }
    }
    
    @Override
    public void deinit(ComponentSystemEvent event) throws AbortProcessingException {
        if ( (prematureSearchMode && searchModeInitialized && currentPage <= 1)
            || (prematureSearchMode && searchModeInitialized && pageCount <= 1)) {
            prematureSearchMode = false;
        }
        serviceIsError = false;
        searchModeInitialized = false;
    }

    @SuppressWarnings("unchecked")
    public void populateResultList(ComponentSystemEvent event) throws AbortProcessingException {
        List<Object> resultListTemp = new ArrayList<Object>();
        currentPageAlt = (newRecordInserted) ? pageCount : ((usingMainPagination) ? currentPage : currentPageAlt);
        currentPage = (newRecordInserted) ? pageCount : ((usingMainPagination) ? currentPage : currentPageAlt);
        currentPageAlt = (currentPageAlt == null) ? 1 : currentPageAlt;
        currentPage = (currentPage == null) ? 1 : currentPage;
        Integer offset = (totalRowCount > 0 && currentPage > 1) ? (currentPage - 1) * limit : 0;
        if (prematureSearchMode) {
            return;
        }
        try {
            if (loggedOn && !brokenUserSession) {
                if (!withoutDataProperties && !checkOrphanDataMode) {
                    resultListTemp = getFacade().list(orderParameter, orderMode, offset, limit, authorId, userGroupId, keywords);
                } else {
                    resultListTemp = getFacade().list(orderParameter, orderMode, offset, limit, keywords);
                }
                if (resultListTemp.isEmpty() && totalRowCount > 0) {
                    if (!withoutDataProperties && !checkOrphanDataMode) {
                        resultListTemp = getFacade().list(orderParameter, orderMode,  0, limit, authorId, userGroupId, keywords);
                    } else {
                        resultListTemp = getFacade().list(orderParameter, orderMode,  0, limit, keywords);
                    }
                }
            }
            dataListIsError = false;
        } catch (Exception e) {
            indicateServiceError(e);
            resultList = new ResultObject[]{};
            dataListIsError = true;
        }
        usingMainPagination = false;
        newRecordInserted = false;
        saveAndClose = false;
        if (!resultListTemp.isEmpty()) {
            List<ResultObject> resultObjectTemp = new ArrayList<ResultObject>();
            for (Object data : resultListTemp) {
                resultObjectTemp.add(presetResultObject(data));
            }
            resultList = resultObjectTemp.toArray(new ResultObject[]{});
        } else {
            resultList = new ResultObject[]{};
        }
    }

    public boolean getDataListIsInError() {
        return dataListIsError;
    }

    public Object[] getResultList() {
        return resultList;
    }

    public void prepareUpdate(Object primaryKey) {
        try {
            getFacade().prepareUpdate(primaryKey);
            presetEntity();
            opMode = WebAppOpMode.UPDATE;
            disableValidation = false;
            this.primaryKey = primaryKey;
        } catch (Exception e) {
            indicateServiceError(e);
        }
    }

    public void executeUpdateAlt(AjaxBehaviorEvent event) throws AbortProcessingException {
        saveAndClose = true;
        executeUpdate(null);
    }

    public void executeUpdate(AjaxBehaviorEvent event) throws AbortProcessingException {
        try {
            feedBackEntity();
            if (getFacade().preUpdateCheck()) {
                getFacade().setUserGroupId(userGroupId);
                getFacade().setModifierId(modifierId);
                getFacade().setAuthorId(authorId);
                onUpdateSubmitTask();
                getFacade().update();
                getFacade().reloadEntity();
                presetEntity();
                feedBackEntity();
                getFacade().postUpdateTask();
                addMessage(SV_INFO, "updateRecordSuccess");
                infoMessage = "updateRecordSuccess";
                if (saveAndClose) {
                    opMode = WebAppOpMode.LISTING;
                    saveAndClose = false;
                    onUpdateFinishTask();
                    disposeEntity();
                    return;
                }
                getFacade().reloadEntity();
                presetEntity();
                onUpdateFinishTask();
            } else {
                addPreUpdateCheckWarnCustomMessage();
                addPreUpdateCheckWarnMessage();
            }
        } catch (Exception e) {
            indicateServiceError(e);
        }
    }
    
    public void executeInlineUpdate(AjaxBehaviorEvent event) throws AbortProcessingException {
        try {
            if (primaryKey != null && !String.valueOf(primaryKey).equals("0")) {
                if (getFacade().isDataExists(primaryKey)) {
                    getFacade().inlineUpdate(primaryKey, extEntity);
                    addMessage(SV_INFO, "updateRecordSuccess");
                    infoMessage = "updateRecordSuccess";
                } else {
                    addMessage(SV_WARN, "dataNotFound");
                }
            }
        } catch (Exception e) {
            indicateServiceError(e);
        }
        inlineUpdateMode = false;
        extEntity = null;
    }
    
    public void prepareInlineUpdate(Object extEntity) {
        inlineUpdateMode = true;
        this.extEntity = extEntity;
    }
    
    public void cancelInlineUpdate() {
        inlineUpdateMode = false;
        primaryKey = null;
        extEntity = null;
    }

    public void prepareTakeover(Object primaryKey) {
        try {
            getFacade().prepareUpdate(primaryKey);
            presetEntity();
            this.primaryKey = primaryKey;
            takeoverOp = true;
        } catch (Exception e) {
            indicateServiceError(e);
        }
    }

    public boolean getTakeoverOp() {
        return takeoverOp;
    }

    public void executeTakeover(AjaxBehaviorEvent event) throws AbortProcessingException {
        try {
            if (!loggedOn || brokenUserSession || userType != UserType.ADMINISTRATOR) {
                saveAndClose = false;
                takeoverOp = false;
                disposeEntity();
                return;
            }
            feedBackEntity();
            getFacade().setUserGroupId(userGroupId);
            getFacade().setModifierId(modifierId);
            getFacade().setAuthorId(authorId);
            getFacade().takeover();
            addMessage(SV_INFO, "TakeoverSuccess");
            infoMessage = "TakeoverSuccess";
            saveAndClose = false;
            takeoverOp = false;
            disposeEntity();
        } catch (Exception e) {
            indicateServiceError(e);
        }
    }

    public void cancelTakeover() {
        disposeEntity();
        takeoverOp = false;
        primaryKey = null;
    }

    public void setPrepareRemoveOp(boolean prepareRemoveOp) {
        this.prepareRemoveOp = prepareRemoveOp;
    }

    public boolean getPrepareRemoveOp() {
        return prepareRemoveOp;
    }
    
    protected void onRemoveFinishTask() {
        // NO-OP
    }

    public void executeRemove(AjaxBehaviorEvent event) throws AbortProcessingException {
        try {
            if (primaryKey != null && !String.valueOf(primaryKey).equals("0")) {
                if (getFacade().isDataExists(primaryKey)) {
                    getFacade().remove(primaryKey);
                    onRemoveFinishTask();
                } else {
                    addMessage(SV_WARN, "dataNotFound");
                }
            }
        } catch (Exception e) {
            indicateServiceError(e);
        }
        prepareRemoveOp = false;
    }

    public void resetMassiveSelection(ComponentSystemEvent event) throws AbortProcessingException {
        applyDeselectAll = false;
        applySelectAll = false;
        selectAllFlag = false;
    }

    protected Object getSelectItemValue() {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        List<UIComponent> children = component.getChildren();
        if (children != null && !children.isEmpty()) {
            return ((UISelectItem) children.get(0)).getItemValue();
        }
        return null;
    }

    public void selectAll(AjaxBehaviorEvent event) throws AbortProcessingException {
        applyDeselectAll = (!selectAllFlag);
        applySelectAll = selectAllFlag;
    }

    protected void putPrimaryKeyByMassSelection(Object primaryKeyId) {
        if (applySelectAll) {
            primaryKeyMap.put(primaryKeyId, new Object[]{primaryKeyId});
        }
        if (applyDeselectAll) {
            resetPrimaryKeyMapPair(primaryKeyId);
        }
    }

    protected void resetPrimaryKeyMapPair(Object primaryKeyId) {
        primaryKeyMap.put(primaryKeyId, new Object[]{});
    }

    public void setPrimaryKeys(Object[] primaryKeys) {
        Object primaryKeyId = getSelectItemValue();
        if (primaryKeyId != null) {
            if (applySelectAll) {
                primaryKeys = new Object[]{primaryKeyId};
            }
            if (applyDeselectAll) {
                primaryKeys = new Object[]{};
            }
            primaryKeyMap.put(primaryKeyId, primaryKeys);
        }
    }

    public Object[] getPrimaryKeys() {
        Object primaryKeyId = getSelectItemValue();
        if (primaryKeyId != null) {
            if (!primaryKeyMap.containsKey(primaryKeyId)) {
                resetPrimaryKeyMapPair(primaryKeyId);
                putPrimaryKeyByMassSelection(primaryKeyId);
            } else {
                putPrimaryKeyByMassSelection(primaryKeyId);
                return primaryKeyMap.get(primaryKeyId);
            }
        }
        return dummyPrimaryKeys;
    }

    public void setSelectAllFlag(boolean selectAllFlag) {
        this.selectAllFlag = selectAllFlag;
    }

    public boolean getSelectAllFlag() {
        return selectAllFlag;
    }

    public void presetMassiveRemovalReadyState() {
        prepareMassiveRemovalOp = true;
        for (Object primaryKeyId : primaryKeyMap.keySet()) {
            Object[] primaryKeys = primaryKeyMap.get(primaryKeyId);
            if (primaryKeys != null && primaryKeys.length != 0) {
                massiveRemovalReadyState = true;
                return;
            }
        }
    }

    public boolean getMassiveRemovalReadyState() {
        return massiveRemovalReadyState;
    }

    public void clearMassiveRemovalReadyState() {
        massiveRemovalReadyState = false;
        prepareMassiveRemovalOp = false;
    }

    public boolean getPrepareMassiveRemovalOp() {
        return prepareMassiveRemovalOp;
    }

    public void executeMassiveRemoval(AjaxBehaviorEvent event) throws AbortProcessingException {
        if (!primaryKeyMap.isEmpty()) {
            List<Object> primaryKeyIdList = new ArrayList<Object>();
            boolean partiallyRemoved = false;
            boolean partiallyLocated = false;
            boolean getRemoved = false;
            for (Object primaryKeyId : primaryKeyMap.keySet()) {
                Object[] primaryKeys = primaryKeyMap.get(primaryKeyId);
                if (primaryKeys != null && primaryKeys.length != 0) {
                    try {
                        if (!getFacade().isDataExists(primaryKeyId)) {
                            partiallyLocated = true;
                            continue;
                        }
                        getFacade().remove(primaryKeyId);
                        primaryKeyIdList.add(primaryKeyId);
                        getRemoved = true;
                    } catch (Exception e) {
                        indicateServiceError(e);
                        partiallyRemoved = true;
                        break;
                    }
                }
            }
            if (!primaryKeyIdList.isEmpty()) {
                for (Object primaryKeyId : primaryKeyIdList) {
                    primaryKeyMap.remove(primaryKeyId);
                }
            }
            if (!partiallyLocated && !partiallyRemoved && getRemoved) {
                addMessage(SV_INFO, "massiveRemovalInfo");
                infoMessage = "massiveRemovalInfo";
            }
            if (partiallyLocated && getRemoved) {
                addMessage(SV_WARN, "massiveRemovalPartialLocatedWarn");
            }
            if (partiallyRemoved && getRemoved) {
                addMessage(SV_WARN, "massiveRemovalPartialRemovedWarn");
            }
            if (!getRemoved) {
                addMessage(SV_ERROR, "massiveRemovalError");
            }
            onRemoveFinishTask();
        }
        clearMassiveRemovalReadyState();
        prepareRemoveOp = false;
    }

    public void cancelRemoval() {
        clearMassiveRemovalReadyState();
        primaryKeyMap.clear();
        primaryKey = null;
        prepareRemoveOp = false;
    }
}
