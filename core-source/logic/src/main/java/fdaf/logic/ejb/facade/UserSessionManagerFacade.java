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

import fdaf.base.tools.CommonTool;
import fdaf.base.tools.PBKDF2Tool;
import fdaf.base.UserSessionManagerInterface;
import fdaf.base.UserType;
import fdaf.logic.base.Specification;
import fdaf.logic.ejb.repository.AuthorRepository;
import fdaf.logic.ejb.repository.EmployeeRepository;
import fdaf.logic.ejb.repository.ModifierRepository;
import fdaf.logic.ejb.repository.UserGroupMemberRepository;
import fdaf.logic.ejb.repository.UserLoginRepository;
import fdaf.logic.ejb.repository.UserRepository;
import fdaf.logic.entity.Author;
import fdaf.logic.entity.Employee;
import fdaf.logic.entity.Modifier;
import fdaf.logic.entity.User;
import fdaf.logic.entity.UserGroupMember;
import fdaf.logic.entity.UserLogin;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.concurrent.TimeUnit;
import javax.ejb.StatefulTimeout;

@StatefulTimeout(value = -1)
@Remote({UserSessionManagerInterface.class})
@Stateful
public class UserSessionManagerFacade implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @EJB
    private UserGroupMemberRepository userGroupMemberRepository;
    
    @EJB
    private UserLoginRepository userLoginRepository;
    
    @EJB
    private UserRepository userRepository;
    
    @EJB
    private AuthorRepository authorRepository;
    
    @EJB
    private ModifierRepository modifierRepository;
    
    @EJB
    private EmployeeRepository employeeRepository;
    
    private UserLogin userLoginEntity;
    private User userEntity;
    private boolean loggedOn;
    private Long authorId;
    private Long employeeId;
    private Long inEpochTimeStamp;
    private Long modifierId;
    private Long userGroupId;
    private Long userId;
    private String employeeUuid;
    private String realName;
    private String userSessionID;
    private String userName;
    private String userUuid;
    private UserType userType;

    public UserSessionManagerFacade() {
        // NO-OP
    }

    private void fetchAccountId(Long userId) throws Exception {
        Specification<UserGroupMember> userGroupMemberSpec = userGroupMemberRepository.presetSpecification();
        userGroupMemberSpec.setPredicate(userGroupMemberSpec.getBuilder().equal(userGroupMemberSpec.getRoot().get("userId"), userId));
        Specification<Modifier> modifierSpec = modifierRepository.presetSpecification();
        modifierSpec.setPredicate(modifierSpec.getBuilder().equal(modifierSpec.getRoot().get("userId"), userId));
        Specification<Author> authorSpec = authorRepository.presetSpecification();
        authorSpec.setPredicate(authorSpec.getBuilder().equal(authorSpec.getRoot().get("userId"), userId));
        UserGroupMember userGroupMemberEntity = userGroupMemberRepository.find(userGroupMemberSpec);
        Modifier modifierEntity = modifierRepository.find(modifierSpec);
        Employee employeeEntity = employeeRepository.find(userEntity.getEmployeeId());
        Author authorEntity = authorRepository.find(authorSpec);
        if ((authorEntity != null) && (modifierEntity != null) && (userGroupMemberEntity != null)) {
            employeeId = employeeEntity.getId();
            employeeUuid = employeeEntity.getUuid();
            this.userId = userEntity.getId();
            userUuid = userEntity.getUuid();
            userGroupId = userGroupMemberEntity.getUserGroupId();
            modifierId = modifierEntity.getId();
            authorId = authorEntity.getId();
            realName = employeeEntity.getFirstName() + " " + employeeEntity.getMiddleName() + " " + employeeEntity.getLastName();
            userName = userEntity.getUserName();
            userType = userEntity.getUserType();
        }
    }

    private boolean isUserLoginDataExists(String userSessionID, String userAgent) {
        Specification<UserLogin> userLoginSpec = userLoginRepository.presetSpecification();
        userLoginSpec.setPredicate(userLoginSpec.getBuilder().and(
            userLoginSpec.getBuilder().equal(userLoginSpec.getRoot().get("userSessionID"), userSessionID),
            userLoginSpec.getBuilder().equal(userLoginSpec.getRoot().get("userAgent"), userAgent)));
        userLoginEntity = userLoginRepository.find(userLoginSpec);
        return (userLoginEntity != null);
    }

    public boolean login(String userName, String password, String userAgent, boolean multipleLogins) throws Exception {
        Specification<User> userSpec = userRepository.presetSpecification();
        userSpec.setPredicate(userSpec.getBuilder().equal(userSpec.getRoot().get("userName"), userName));
        userEntity = userRepository.find(userSpec);
        if ((userEntity == null) || ((userEntity != null) && !PBKDF2Tool.validate(password, userEntity.getPassword()))) {
            return false;
        }
        if (!multipleLogins) {
            Specification<UserLogin> userLoginSpec = userLoginRepository.presetSpecification();
            userLoginSpec.setPredicate(userLoginSpec.getBuilder().and(
                userLoginSpec.getBuilder().equal(userLoginSpec.getRoot().get("logoutState"), false),
                userLoginSpec.getBuilder().equal(userLoginSpec.getRoot().get("userId"), userEntity.getId())));
            List<UserLogin> uls = userLoginRepository.findAll(userLoginSpec);
            if ((uls != null) && !uls.isEmpty()) {
                for (UserLogin ul : uls) {
                    ul.setLogoutState(true);
                    ul.setLogoutFlag(1);
                    userLoginRepository.update(ul);
                }
            }
        }
        String inTimeStamp = CommonTool.getTextualInstantDateTime();
        inEpochTimeStamp = CommonTool.getInstantEpochTimeStamp();
        userSessionID = PBKDF2Tool.encrypt(userEntity.getId() + userEntity.getUuid() + userAgent + inEpochTimeStamp + inTimeStamp);
        fetchAccountId(userEntity.getId());
        UserLogin userLoginEntity = new UserLogin();
        userLoginEntity.setInTimeStamp(inTimeStamp);
        userLoginEntity.setInEpochTimeStamp(inEpochTimeStamp);
        userLoginEntity.setUserId(userEntity.getId());
        userLoginEntity.setUserAgent(userAgent);
        userLoginEntity.setUserSessionID(userSessionID);
        userLoginEntity.setLogoutState(false);
        userLoginRepository.create(userLoginEntity);
        return true;
    }

    public void rollbackLogin(String userSessionID, String userAgent) {
        if (isUserLoginDataExists(userSessionID, userAgent)) {
            userLoginRepository.remove(userLoginEntity);
        }
    }

    public void watchSession(String userSessionID, String userAgent) throws Exception {
        if (!isUserLoginDataExists(userSessionID, userAgent)) {
            loggedOn = false;
            return;
        }
        userEntity = userRepository.find(userLoginEntity.getUserId());
        loggedOn = false;
        if ((userEntity != null) && PBKDF2Tool.validate(userEntity.getId()
            + userEntity.getUuid() + userLoginEntity.getUserAgent()
            + userLoginEntity.getInEpochTimeStamp()
            + userLoginEntity.getInTimeStamp(), userSessionID)) {
            fetchAccountId(userLoginEntity.getUserId());
            Long epochTimeStamp = CommonTool.getInstantEpochTimeStamp();
            inEpochTimeStamp = userLoginEntity.getInEpochTimeStamp();
            loggedOn = !userLoginEntity.getLogoutState();
            this.userSessionID = userSessionID;
        }
    }

    public boolean isLoggedOn() {
        return loggedOn;
    }

    public String getUserName() {
        return userName;
    }

    public String getRealName() {
        return realName;
    }

    public String getEmployeeUuid() {
        return employeeUuid;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Long getInEpochTimeStamp() {
        return inEpochTimeStamp;
    }

    public String getUserSessionID() {
        return userSessionID;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getUserGroupId() {
        return userGroupId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Long getModifierId() {
        return modifierId;
    }
    
    public UserType getUserType() {
        return userType;
    }

    public boolean logout(String userSessionID, String userAgent) {
        if (isUserLoginDataExists(userSessionID, userAgent)) {
            userLoginEntity.setOutEpochTimeStamp(CommonTool.getInstantEpochTimeStamp());
            userLoginEntity.setOutTimeStamp(CommonTool.getTextualInstantDateTime());
            userLoginEntity.setLogoutState(true);
            userLoginEntity.setLogoutFlag(0);
            userLoginRepository.update(userLoginEntity);
            return true;
        }
        return false;
    }
}
