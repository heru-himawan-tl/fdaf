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

import fdaf.base.AddAdministratorInterface;
import fdaf.base.Permission;
import fdaf.base.tools.CommonTool;
import fdaf.base.tools.PBKDF2Tool;
import fdaf.base.UserType;
import fdaf.logic.base.AbstractFacade;
import fdaf.logic.base.Specification;
import fdaf.logic.ejb.repository.AuthorRepository;
import fdaf.logic.ejb.repository.EmployeeRepository;
import fdaf.logic.ejb.repository.ModifierRepository;
import fdaf.logic.ejb.repository.UserGroupMemberRepository;
import fdaf.logic.ejb.repository.UserGroupRepository;
import fdaf.logic.ejb.repository.UserRepository;
import fdaf.logic.entity.Author;
import fdaf.logic.entity.Employee;
import fdaf.logic.entity.Modifier;
import fdaf.logic.entity.User;
import fdaf.logic.entity.UserGroup;
import fdaf.logic.entity.UserGroupMember;
import fdaf.logic.tools.RemovalTool;
import java.io.Serializable;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

@Remote({AddAdministratorInterface.class})
@Stateful
public class AddAdministratorFacade extends AbstractFacade<UserRepository, User> implements Serializable {

    private static final long serialVersionUID = 1L;
    
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
    
    private Long userGroupMemberId;
    private Long userGroupId;
    private Long authorId;
    private Long modifierId;
    private Long employeeId;
    
    private String password;

    public AddAdministratorFacade() {
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

    @Override
    public boolean preCreateCheck() {
        Specification<User> spec = repository.presetSpecification();
        spec.setPredicate(spec.getBuilder().equal(spec.getRoot().get("userName"), entity.getUserName()));
        if (repository.find(spec) != null) {
            setMessage("newUserDuplicated");
            return false;
        }
        if (!entity.getPassword().equals(entity.getPasswordConfirm())) {
            setMessage("passwordNotConfirmend");
            return false;
        }
        spec = repository.presetSpecification();
        spec.setPredicate(spec.getBuilder().equal(spec.getRoot().get("email"), entity.getEmail()));
        if (repository.find(spec) != null) {
            setMessage("emailAlreadyUsed");
            return false;
        }
        return true;
    }

    @Override
    public void postCreateTask() {
        Long userId = (Long) getNewRecordId();
        Specification<UserGroup> userGroupSpec = userGroupRepository.presetSpecification();
        userGroupSpec.setPredicate(userGroupSpec.getBuilder().equal(userGroupSpec.getRoot().get("name"), "ADMINISTRATOR"));
        UserGroup userGroupEntity = userGroupRepository.find(userGroupSpec);
        Specification<UserGroupMember> userGroupMemberSpec = userGroupMemberRepository.presetSpecification();
        userGroupMemberSpec.setPredicate(userGroupMemberSpec.getBuilder().equal(userGroupMemberSpec.getRoot().get("userId"), userId));
        UserGroupMember userGroupMemberEntity = userGroupMemberRepository.find(userGroupMemberSpec);
        Specification<Author> authorSpec = authorRepository.presetSpecification();
        authorSpec.setPredicate(authorSpec.getBuilder().equal(authorSpec.getRoot().get("userId"), userId));
        Author authorEntity = authorRepository.find(authorSpec);
        Specification<Modifier> modifierSpec = modifierRepository.presetSpecification();
        modifierSpec.setPredicate(modifierSpec.getBuilder().equal(modifierSpec.getRoot().get("userId"), userId));
        Modifier modifierEntity = modifierRepository.find(modifierSpec);
        Specification<Employee> employeeSpec = employeeRepository.presetSpecification();
        String employeeEntityUUID = UUID.randomUUID().toString();
        employeeSpec.setPredicate(employeeSpec.getBuilder().equal(employeeSpec.getRoot().get("uuid"), employeeEntityUUID));
        updateRecordDate();
        if (userGroupEntity == null) {
            userGroupEntity = new UserGroup();
            userGroupEntity.setName("ADMINISTRATOR");
            userGroupEntity.setDescription("Group for all administrator accounts.");
            String userGroupEntityUUID = UUID.randomUUID().toString();
            userGroupEntity.setUuid(userGroupEntityUUID);
            userGroupRepository.create(userGroupEntity);
            userGroupSpec = userGroupRepository.presetSpecification();
            userGroupSpec.setPredicate(userGroupSpec.getBuilder().equal(userGroupSpec.getRoot().get("uuid"), userGroupEntityUUID));
            userGroupEntity = userGroupRepository.find(userGroupSpec);   
        }
        userGroupId = userGroupEntity.getId();
        if (userGroupMemberEntity == null) {
            userGroupMemberEntity = new UserGroupMember();
            userGroupMemberEntity.setUserId(userId);
            userGroupMemberEntity.setUserGroupId(userGroupEntity.getId());
            userGroupMemberEntity.setUuid(UUID.randomUUID().toString());
            userGroupMemberRepository.create(userGroupMemberEntity);
            userGroupMemberEntity = userGroupMemberRepository.find(userGroupMemberSpec);
        }
        userGroupMemberId = userGroupMemberEntity.getId();
        if (authorEntity == null) {
            authorEntity = new Author();
            authorEntity.setUserId(userId);
            authorEntity.setUuid(UUID.randomUUID().toString());
            authorRepository.create(authorEntity);
            authorEntity = authorRepository.find(authorSpec);
        }
        authorId = authorEntity.getId();
        if (modifierEntity == null) {
            modifierEntity = new Modifier();
            modifierEntity.setUserId(userId);
            modifierEntity.setUuid(UUID.randomUUID().toString());
            modifierRepository.create(modifierEntity);
            modifierEntity = modifierRepository.find(modifierSpec);
        }
        modifierId = modifierEntity.getId();
        if (modifierId != null && authorId != null && userGroupId != null) {
            userGroupEntity.setAuthorId(authorId);
            userGroupEntity.setCreatedDate(recordDate);
            userGroupEntity.setModificationDate(recordDate);
            userGroupEntity.setModifierId(modifierId);
            userGroupRepository.update(userGroupEntity);
            userGroupMemberEntity.setAuthorId(authorId);
            userGroupMemberEntity.setCreatedDate(recordDate);
            userGroupMemberEntity.setModificationDate(recordDate);
            userGroupMemberEntity.setModifierId(modifierId);
            userGroupMemberRepository.update(userGroupMemberEntity);
            authorEntity.setAuthorId(authorId);
            authorEntity.setCreatedDate(recordDate);
            authorEntity.setModificationDate(recordDate);
            authorEntity.setModifierId(modifierId);
            authorRepository.update(authorEntity);
            modifierEntity.setAuthorId(authorId);
            modifierEntity.setCreatedDate(recordDate);
            modifierEntity.setModificationDate(recordDate);
            modifierEntity.setModifierId(modifierId);
            modifierRepository.update(modifierEntity);
        }
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
        employeeEntity = employeeRepository.find(employeeSpec);
        employeeId = employeeEntity.getId();
        reloadNewEntity();
        try {
            password = entity.getPassword();
            String encryptedPassword = PBKDF2Tool.encrypt(password);
            entity.setPassword(encryptedPassword);
        } catch (Exception e) {
        }
        entity.setEmployeeId(employeeId);
        entity.setUserType(UserType.ADMINISTRATOR);
        entity.setAuthorId(authorId);
        entity.setCreatedDate(recordDate);
        entity.setModificationDate(recordDate);
        entity.setModifierId(modifierId);
        entity.setPermission(Permission.READ_ONLY_FOR_ALL);
        entity.setUserGroupId(userGroupId);
        entity.setUuid(uuid);
        entity.setEnabled(true);
        repository.update(entity);
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getUserName() {
        return entity.getUserName();
    }
    
    public String getEmail() {
        return entity.getEmail();
    }

    @Override
    public void rollbackCreateTask() {
        new RemovalTool<UserRepository, User>().remove(repository, getNewRecordId());
        new RemovalTool<UserGroupMemberRepository, UserGroupMember>().remove(userGroupMemberRepository, userGroupMemberId);
        // User group may not be removed.
        //new RemovalTool<UserGroupRepository, UserGroup>().remove(userGroupRepository, userGroupId);
        new RemovalTool<AuthorRepository, Author>().remove(authorRepository, authorId);
        new RemovalTool<ModifierRepository, Modifier>().remove(modifierRepository, modifierId);
        new RemovalTool<EmployeeRepository, Employee>().remove(employeeRepository, employeeId);
    }

    public Object getNewRecordId() {
        return (entity == null) ? 0 : entity.getId();
    }
}
