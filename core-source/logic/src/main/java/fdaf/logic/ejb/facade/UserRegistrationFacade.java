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

import fdaf.base.Permission;
import fdaf.base.tools.CommonTool;
import fdaf.base.tools.PBKDF2Tool;
import fdaf.base.UserRegistrationInterface;
import fdaf.base.UserType;
import fdaf.logic.base.AbstractFacade;
import fdaf.logic.base.Specification;
import fdaf.logic.ejb.repository.AuthorRepository;
import fdaf.logic.ejb.repository.EmployeeRepository;
import fdaf.logic.ejb.repository.ModifierRepository;
import fdaf.logic.ejb.repository.StaffInvitationRepository;
import fdaf.logic.ejb.repository.UserGroupMemberRepository;
import fdaf.logic.ejb.repository.UserGroupRepository;
import fdaf.logic.ejb.repository.UserRepository;
import fdaf.logic.entity.Author;
import fdaf.logic.entity.Employee;
import fdaf.logic.entity.Modifier;
import fdaf.logic.entity.StaffInvitation;
import fdaf.logic.entity.User;
import fdaf.logic.entity.UserGroup;
import fdaf.logic.entity.UserGroupMember;
import fdaf.logic.tools.RemovalTool;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

@Remote({UserRegistrationInterface.class})
@Stateful
public class UserRegistrationFacade extends AbstractFacade<UserRepository, User> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(UserRegistrationFacade.class.getName());
    
    @EJB
    private StaffInvitationRepository invitationRepository;
    
    @EJB
    private UserRepository repository;
    
    @EJB
    private UserGroupMemberRepository userGroupMemberRepository;
    
    @EJB
    private UserGroupRepository userGroupRepository;
    
    @EJB
    private AuthorRepository authorRepository;
    
    @EJB
    private ModifierRepository modifierRepository;
    
    @EJB
    private EmployeeRepository employeeRepository;
    
    private Long authorId;
    private Long employeeId;
    private Long modifierId;
    private Long userGroupId;
    private Long userGroupMemberId;
    private Long userId;

    public UserRegistrationFacade() {
        super(User.class);
    }

    protected UserRepository getRepository() {
        return repository;
    }

    protected User newEntity() {
        return new User();
    }

    protected void setUuid() {
        entity.setUuid(uuid);
    }

    public Object getNewRecordId() {
        return entity.getId();
    }
    
    public boolean register(UserType userType, String userName, String password, String email) {
        String encryptedPassword = null;
        updateRecordDate();
        try {
            encryptedPassword = PBKDF2Tool.encrypt(password);
        } catch (Exception e) {
            indicateServiceError(e);
            setMessage("passwordEncryptError");
            return false;
        }
        Modifier modifierEntity = null;
        Author authorEntity = null;
        try {
            Specification<UserGroup> userGroupSpec = userGroupRepository.presetSpecification();
            userGroupSpec.setPredicate(userGroupSpec.getBuilder().equal(userGroupSpec.getRoot().get("name"), userType.name()));
            UserGroup userGroupEntity = userGroupRepository.find(userGroupSpec);
            if (userGroupEntity == null) {
                userGroupEntity = new UserGroup();
                userGroupEntity.setName(userType.name());
                if (userType == UserType.CLIENT) {
                    userGroupEntity.setDescription("Group for all client accounts.");
                }
                if (userType == UserType.STAFF) {
                    userGroupEntity.setDescription("Group for all staff accounts.");
                }
                String userGroupEntityUUID = UUID.randomUUID().toString();
                userGroupEntity.setUuid(userGroupEntityUUID);
                userGroupEntity.setCreatedDate(recordDate);
                userGroupEntity.setModificationDate(recordDate);
                userGroupRepository.create(userGroupEntity);
                userGroupSpec = userGroupRepository.presetSpecification();
                userGroupSpec.setPredicate(userGroupSpec.getBuilder().equal(userGroupSpec.getRoot().get("uuid"), userGroupEntityUUID));
                userGroupEntity = userGroupRepository.find(userGroupSpec);
            }
            userGroupId = userGroupEntity.getId();
            prepareCreate();
            entity.setPassword(encryptedPassword);
            entity.setUserName(userName);
            entity.setEmail(email);
            entity.setUserType(userType);
            entity.setEnabled(true);
            repository.create(entity);
            reloadNewEntity();
            userId = entity.getId();
        } catch (Exception e) {
            indicateServiceError(e);
            return false;
        }
        try {
            Specification<Author> authorSpec = authorRepository.presetSpecification();
            authorSpec.setPredicate(authorSpec.getBuilder().equal(authorSpec.getRoot().get("userId"), userId));
            authorEntity = authorRepository.find(authorSpec);
            if (authorEntity == null) {
                authorEntity = new Author();
                authorEntity.setUserId(userId);
                authorEntity.setUuid(UUID.randomUUID().toString());
                authorEntity.setCreatedDate(recordDate);
                authorEntity.setModificationDate(recordDate);
                authorRepository.create(authorEntity);
                authorEntity = authorRepository.find(authorSpec);
            }
            authorId = authorEntity.getId();
        } catch (Exception e) {
            indicateServiceError(e);
            return false;
        }
        try {
            Specification<Modifier> modifierSpec = modifierRepository.presetSpecification();
            modifierSpec.setPredicate(modifierSpec.getBuilder().equal(modifierSpec.getRoot().get("userId"), userId));
            modifierEntity = modifierRepository.find(modifierSpec);
            if (modifierEntity == null) {
                modifierEntity = new Modifier();
                modifierEntity.setUserId(userId);
                modifierEntity.setUuid(UUID.randomUUID().toString());
                modifierEntity.setCreatedDate(recordDate);
                modifierEntity.setModificationDate(recordDate);
                modifierRepository.create(modifierEntity);
                modifierEntity = modifierRepository.find(modifierSpec);
            }
            modifierId = modifierEntity.getId();
        } catch (Exception e) {
            indicateServiceError(e);
            return false;
        }
        try {
            String userGroupMemberEntityUUID = UUID.randomUUID().toString();
            UserGroupMember userGroupMemberEntity = new UserGroupMember();
            userGroupMemberEntity.setUserId(userId);
            userGroupMemberEntity.setUserGroupId(userGroupId);
            userGroupMemberEntity.setUuid(userGroupMemberEntityUUID);
            userGroupMemberEntity.setCreatedDate(recordDate);
            userGroupMemberEntity.setModificationDate(recordDate);
            userGroupMemberRepository.create(userGroupMemberEntity);
            Specification<UserGroupMember> userGroupMemberSpec = userGroupMemberRepository.presetSpecification();
            userGroupMemberSpec.setPredicate(userGroupMemberSpec.getBuilder().equal(userGroupMemberSpec.getRoot().get("uuid"), userGroupMemberEntityUUID));
            userGroupMemberEntity = userGroupMemberRepository.find(userGroupMemberSpec);
            userGroupMemberId = userGroupMemberEntity.getId();
        } catch (Exception e) {
            indicateServiceError(e);
            return false;
        }
        try {
            String employeeEntityUUID = UUID.randomUUID().toString();
            Employee employeeEntity = new Employee();
            employeeEntity.setAuthorId(authorId);
            employeeEntity.setCreatedDate(recordDate);
            employeeEntity.setFirstName("Unknown " + CommonTool.generateRandomName());
            employeeEntity.setLastName("Unknown " + CommonTool.generateRandomName());
            employeeEntity.setMiddleName("Unknown " + CommonTool.generateRandomName());
            employeeEntity.setModificationDate(recordDate);
            employeeEntity.setModifierId(modifierId);
            employeeEntity.setPermission(Permission.READ_ONLY_FOR_ALL);
            employeeEntity.setUserGroupId(userGroupId);
            employeeEntity.setUuid(employeeEntityUUID);
            employeeRepository.create(employeeEntity);
            Specification<Employee> employeeSpec = employeeRepository.presetSpecification();
            employeeSpec.setPredicate(employeeSpec.getBuilder().equal(employeeSpec.getRoot().get("uuid"), employeeEntityUUID));
            employeeEntity = employeeRepository.find(employeeSpec);
            employeeId = employeeEntity.getId();
        } catch (Exception e) {
            indicateServiceError(e);
            return false;
        }
        try {
            entity.setEmployeeId(employeeId);
            entity.setAuthorId(authorId);
            entity.setModifierId(modifierId);
            entity.setPermission(Permission.READ_ONLY_FOR_ALL);
            repository.update(entity);
        } catch (Exception e) {
            indicateServiceError(e);
            return false;
        }
        try {
            modifierEntity.setAuthorId(authorId);
            modifierEntity.setModifierId(modifierId);
            modifierRepository.update(modifierEntity);
        } catch (Exception e) {
            indicateServiceError(e);
            return false;
        }
        try {
            authorEntity.setAuthorId(authorId);
            authorEntity.setModifierId(modifierId);
            authorRepository.update(authorEntity);
        } catch (Exception e) {
            indicateServiceError(e);
            return false;
        }
        return true;
    }
    
    private void indicateServiceError(Exception e) {
        rollback();
        LOGGER.log(Level.SEVERE, "Account Registration Service Failure", e);
        setMessage("registerAccountServiceFailure");
    }
    
    @Override
    public void rollbackCreateTask() {
        rollback();
    }
    
    private void rollback() {
        new RemovalTool<UserRepository, User>().remove(repository, getNewRecordId());
        new RemovalTool<UserGroupMemberRepository, UserGroupMember>().remove(userGroupMemberRepository, userGroupMemberId);
        //new RemovalTool<UserGroupRepository, UserGroup>().remove(userGroupRepository, userGroupId);
        new RemovalTool<AuthorRepository, Author>().remove(authorRepository, authorId);
        new RemovalTool<ModifierRepository, Modifier>().remove(modifierRepository, modifierId);
        new RemovalTool<EmployeeRepository, Employee>().remove(employeeRepository, employeeId);
    }
    
    public boolean validateInvitation(String email, String signature) {
        Specification<StaffInvitation> spec = invitationRepository.presetSpecification();
        spec.setPredicate(spec.getBuilder().and(
            spec.getBuilder().equal(spec.getRoot().get("signature"), signature),
            spec.getBuilder().equal(spec.getRoot().get("email"), email)));
        return (invitationRepository.find(spec) != null);
    }
    
    public boolean findUserByName(String userName) {
        Specification<User> spec = repository.presetSpecification();
        spec.setPredicate(spec.getBuilder().equal(spec.getRoot().get("userName"), userName));
        return (repository.find(spec) != null);
    }
    
    public boolean findUserByEmail(String email) {
        Specification<User> spec = repository.presetSpecification();
        spec.setPredicate(spec.getBuilder().equal(spec.getRoot().get("email"), email));
        return (repository.find(spec) != null);
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
