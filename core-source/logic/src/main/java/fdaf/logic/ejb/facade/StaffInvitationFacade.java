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
package fdaf.logic.ejb.facade;

import fdaf.base.StaffInvitationInterface;
import fdaf.logic.base.AbstractFacade;
import fdaf.logic.base.Specification;
import fdaf.logic.base.UpdateCallbackInterface;
import fdaf.logic.ejb.repository.StaffInvitationRepository;
import fdaf.logic.entity.StaffInvitation;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.concurrent.TimeUnit;
import javax.ejb.StatefulTimeout;

@StatefulTimeout(value = -1)
@Remote({StaffInvitationInterface.class})
@Stateful
public class StaffInvitationFacade extends AbstractFacade<StaffInvitationRepository, StaffInvitation> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(StaffInvitationFacade.class.getName());
    
    @EJB
    private StaffInvitationRepository repository;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/StaffInvitationUpdateCallback")
    private UpdateCallbackInterface<StaffInvitationRepository, StaffInvitation> updateCallbackBean;
    
    private StaffInvitation backupEntity;
    
    private boolean rolledBack;

    public StaffInvitationFacade() {
        super(StaffInvitation.class);
    }

    @Override
    protected void presetUpdateCallback() {
        updateCallback = updateCallbackBean;
    }

    protected StaffInvitationRepository getRepository() {
        return repository;
    }

    protected StaffInvitation newEntity() {
        return new StaffInvitation();
    }

    protected void setUuid() {
        entity.setUuid(uuid);
    }

    public Object getNewRecordId() {
        return entity.getId();
    }
    
    public String getEmail() {
        return entity.getEmail();
    }
    
    public String getInvitaionSignature() {
        return entity.getSignature();
    }
    
    public String getInvitaionSubject() {
        return entity.getSubject();
    }
    
    public String getInvitaionMessage() {
        return entity.getMessage();
    }
    
    @Override
    public void prepareCreate() {
        super.prepareCreate();
        if (rolledBack && backupEntity != null) {
            entity.setSignature(backupEntity.getSignature());
            entity.setEmail(backupEntity.getEmail());
        }
        backupEntity = null;
        rolledBack = false;
    }
    
    @Override
    public void rollbackCreateTask() {
        try {
            if (entity == null) {
                return;
            }
            Long primaryKey = entity.getId();
            if (primaryKey == null) {
                return;
            }
            entity = getRepository().find(primaryKey);
            if (entity != null) {
                backupEntity = entity;
                getRepository().remove(entity);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        rolledBack = true;
    }
    
    @Override
    protected void updateDataProperties(boolean updateMode) {
        updateRecordDate();
        entity.setModificationDate(recordDate);
        if (!updateMode) {
            entity.setAuthorId(authorId);
            entity.setUserGroupId(userGroupId);
            modifierId = authorId;
            entity.setCreatedDate(recordDate);
        }
        entity.setModifierId(modifierId);
    }
}
