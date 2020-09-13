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
package fdaf.base;

import java.util.List;

/**
 * General remote interface for general facade beans, provides most common
 * methods to handle CRUD operations, methods to invoke the persistence
 * entity, and methods to invoke the facade callbacks those declared within
 * {@link fdaf.logic.base.AbstractFacade}.
 *
 * @see         fdaf.logic.base.AbstractFacade
 *
 * @author      Heru Himawan Tejo Laksono
 */
public interface FacadeInterface {

    /**
     * Check whether the data of corresponding entity object with the
     * specified primary key exist.
     *
     * @param   primaryKey      a data type wrapper object represents the
     *                          primary key (generally, FDAF applies
     *                          {@link java.lang.Long})
     *
     * @return  {@code true} if data with specified primary key exists
     */
    public boolean isDataExists(Object primaryKey);
    
    /**
     * Check the specific conditions before persisting a new entity.
     *
     * @return  {@code true} if the conditions meet
     */
    public boolean preCreateCheck();
    
    /**
     * Check the specific conditions before updating current entity.
     *
     * @return  {@code true} if the conditions meet
     */
    public boolean preUpdateCheck();
    
    /**
     * Totally count the data rows.
     * 
     * @return  the number of available data rows
     */
    public int count();
    
    /**
     * Specifically count the data rows based the specified author ID, user
     * group ID and the specified keywords. This method is designed as a part
     * of search tool.
     * 
     * @return  the number of available data rows those meet the parameters
     */
    public int count(Long authorId, Long userGroupId, String[] keywords);
    
    /**
     * Specifically count the data rows based the specified author ID and
     * keywords. This method is designed as a part of search tool.
     * 
     * @return  the number of available data rows those meet the parameters
     */
    public int count(Long authorId, String[] keywords);
    
    /**
     * Specifically count the data rows based the specified keywords. This
     * method is designed as a part of search tool.
     * 
     * @return  the number of available data rows those meet the parameter
     */
    public int count(String[] keywords);
    
    /**
     * Roughly list all data.
     * 
     * @return  list of corresponding entity object
     */
    public List<Object> list();
    
    /**
     * List the corresponding entity object based the specified order
     * parameter, ordering mode, offset, and limit.
     *
     * @param   orderParameter  name of specific field of the corresponding
     *                          entity as an <code>ORDER BY</code> parameter
     * @param   orderMode       ordering mode, representation of
     *                          {@link OrderingMode} to determine whether
     *                          ascending or descending
     * @pparam  offset          offset of the first row to be listed
     * @param   limit           limit of maximum number of rows, started
     *                          from the specified offset
     *
     * @return  list of corresponding entity object
     */
    public List<Object> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit);
    
    /**
     * List the corresponding entity object based the specified order
     * parameter, ordering mode, offset, limit, author ID, user group ID,
     * and keywords.
     *
     * @param   orderParameter  name of specific field of the corresponding
     *                          entity as an <code>ORDER BY</code> parameter
     * @param   orderMode       ordering mode, representation of
     *                          {@link OrderingMode} to determine whether
     *                          ascending or descending
     * @pparam  offset          offset of the first row to be listed
     * @param   limit           limit of maximum number of rows, started
     *                          from the specified offset
     * @param   authorId        the ID number of the specific data author
     * @param   userGroupId     the ID number of user group of the
     *                          corresponding data author
     * @param   keywords        string array represent the search-keywords
     *
     * @return  list of corresponding entity object
     */
    public List<Object> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, Long authorId, Long userGroupId, String[] keywords);
    
    /**
     * List the corresponding entity object based the specified order
     * parameter, ordering mode, offset, limit, author ID, and keywords.
     *
     * @param   orderParameter  name of specific field of the corresponding
     *                          entity as an <code>ORDER BY</code> parameter
     * @param   orderMode       ordering mode, representation of
     *                          {@link OrderingMode} to determine whether
     *                          ascending or descending
     * @pparam  offset          offset of the first row to be listed
     * @param   limit           limit of maximum number of rows, started
     *                          from the specified offset
     * @param   authorId        the ID number of the specific data author
     * @param   keywords        string array represent the search-keywords
     *
     * @return  list of corresponding entity object
     */
    public List<Object> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, Long authorId, String[] keywords);
    
    /**
     * List the corresponding entity object based the specified order
     * parameter, ordering mode, offset, limit, and keywords.
     *
     * @param   orderParameter  name of specific field of the corresponding
     *                          entity as an <code>ORDER BY</code> parameter
     * @param   orderMode       ordering mode, representation of
     *                          {@link OrderingMode} to determine whether
     *                          ascending or descending
     * @pparam  offset          offset of the first row to be listed
     * @param   limit           limit of maximum number of rows, started
     *                          from the specified offset
     * @param   keywords        string array represent the search-keywords
     *
     * @return  list of corresponding entity object
     */
    public List<Object> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, String[] keywords);
    
    /**
     * List the corresponding entity object based the specified order
     * parameter and ordering mode.
     *
     * @param   orderParameter  name of specific field of the corresponding
     *                          entity as an <code>ORDER BY</code> parameter
     * @param   orderMode       ordering mode, representation of
     *                          {@link OrderingMode} to determine whether
     *                          ascending or descending
     *
     * @return  list of corresponding entity object
     */
    public List<Object> list(String orderParameter, OrderingMode orderMode);
    
    /**
     * Get the order parameters based the persistable field names of the
     * corresponding entity class.
     *
     * @return  list of persistable field names of the corresponding entity
     */
    public List<String> getOrderParameters();
    
    /**
     * Get the instance of corresponding entity.
     *
     * @return  instance of corresponding entity 
     */
    public Object getEntity();
    
    /**
     * Return the record ID of the newly persisted entity.
     *
     * @return  object represent the new record ID
     */
    public Object getNewRecordId();
    
    /**
     * Return the custom message that raised by an update-callback method.
     *
     * @return  string representation of update-callback custom message
     */
    public String getCustomMessage();
    
    /**
     * Return the general message that raised by an update-callback method.
     *
     * @return  string representation of update-callback general message
     */
    public String getMessage();
    
    /**
     * Update an entity in inline mode (purposed for editable row of data
     * listing).
     *
     * @param   primaryKey      primaryKey of corresponding entity object of
     *                          the edited target
     * @param   entity          entity object to update
     */
    public void inlineUpdate(Object primaryKey, Object entity);
    
    /**
     * Execute the general 'create' operation to persist a new entity.
     */
    public void create();
    
    /**
     * Execute a custom facade task. This method is purposed for a developer
     * to custom code within an overriding declaration of this method.
     *
     * @param   parameter       free object represent the parameter for this
     *                          method.
     */
    public void customFacadeTask(Object parameter);
    
    /**
     * Dispose of an entity object that recently used within a 'create' or
     * an 'update' operation.
     */
    public void disposeEntity();
    
    /**
     * Execute a task upon the new entity successfully persisted.
     */
    public void postCreateTask();
    
    /**
     * Execute a task upon an 'update' operation success.
     */
    public void postUpdateTask();
    
    /**
     * Prepare a 'create' operation.
     */
    public void prepareCreate();
    
    /**
     * Prepare an 'update' operation to the corresponding selected entity
     * with the specified primary key.
     *
     * @param   primaryKey      primary key to select a corresponding entity
     */
    public void prepareUpdate(Object primaryKey);
    
    /**
     * Reload a recently updated entity to get last known data within.
     */
    public void reloadEntity();
    
    /**
     * Reload a newly persisted entity to get current data within.
     */
    public void reloadNewEntity();
    
    /**
     * Remove an instance of the corresponding entity based on the specified
     * primary key.
     *
     * @param   primaryKey      primary key to select a corresponding entity
     */
    public void remove(Object primaryKey);
    
    /**
     * Execute the rollback upon a 'create' operation encountered serious
     * problem.
     */
    public void rollbackCreateTask();
    
    /**
     * Set the author ID for the newly persisted or the newly updated entity.
     *
     * @param   authorId        the author ID
     */
    public void setAuthorId(Long authorId);
    
    /**
     * Set entity object into current facade, causes the corresponding entity
     * in a 'create' or 'update' operation refreshed with the last updated
     * data.
     *
     * @param   entity          an instance of corresponding entity
     */
    public void setEntity(Object entity);
    
    /**
     * Set the modifier ID for the newly persisted or the newly updated
     * entity.
     *
     * @param   modifierId      the modifier ID
     */
    public void setModifierId(Long modifierId);
    
    /**
     * Set the user group ID for the newly persisted or the newly updated
     * entity.
     *
     * @param   userGroupId     the user group ID
     */
    public void setUserGroupId(Long userGroupId);
    
    /**
     * Takeover an orphan entity into current administrator user ownership.
     */
    public void takeover();
    
    /**
     * Execute an 'update' operation.
     */
    public void update();
}
