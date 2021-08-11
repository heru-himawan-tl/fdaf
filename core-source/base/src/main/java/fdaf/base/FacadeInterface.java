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

public interface FacadeInterface {
    public boolean isDataExists(Object primaryKey);
    public boolean preCreateCheck();
    public boolean preUpdateCheck();
    public int count();
    public int count(Long authorId, Long userGroupId, String[] keywords);
    public int count(Long authorId, String[] keywords);
    public int count(String[] keywords);
    public List<Object> list();
    public List<Object> list(String orderParameter, OrderingMode orderMode);
    public List<Object> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit);
    public List<Object> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, Long authorId, Long userGroupId, String[] keywords);
    public List<Object> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, Long authorId, String[] keywords);
    public List<Object> list(String orderParameter, OrderingMode orderMode, Integer offset, Integer limit, String[] keywords);
    public List<String> getOrderParameters();
    public Object getEntity();
    public Object getNewRecordId();
    public String getCustomMessage();
    public String getMessage();
    public void create();
    public void customFacadeTask(Object parameter);
    public void disposeEntity();
    public void inlineUpdate(Object primaryKey, Object entity);
    public void postCreateTask();
    public void postUpdateTask();
    public void prepareCreate();
    public void prepareUpdate(Object primaryKey);
    public void reloadEntity();
    public void reloadNewEntity();
    public void remove(Object primaryKey);
    public void rollbackCreateTask();
    public void setAuthorId(Long authorId);
    public void setEntity(Object entity);
    public void setModifierId(Long modifierId);
    public void setUserGroupId(Long userGroupId);
    public void takeover();
    public void update();
}
