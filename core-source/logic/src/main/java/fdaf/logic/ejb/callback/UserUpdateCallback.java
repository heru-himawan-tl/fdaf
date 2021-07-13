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
package fdaf.logic.ejb.callback;

import fdaf.base.tools.PBKDF2Tool;
import fdaf.base.UserType;
import fdaf.logic.base.AbstractUpdateCallback;
import fdaf.logic.base.Specification;
import fdaf.logic.base.UpdateCallbackInterface;
import fdaf.logic.ejb.repository.UserRepository;
import fdaf.logic.entity.User;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.concurrent.TimeUnit;
import javax.ejb.StatefulTimeout;

@StatefulTimeout(value = -1)
@Remote({UpdateCallbackInterface.class})
@Stateful(passivationCapable = false)
public class UserUpdateCallback extends AbstractUpdateCallback
        implements UpdateCallbackInterface<UserRepository, User>, Serializable {
        
    private static final long serialVersionUID = 1L;
    private UserRepository repository;
    private User entity;
    private String passwordBackup;

    public UserUpdateCallback() {
        // NO-OP
    }

    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }

    public void setEntity(User entity) {
        this.entity = entity;
    }
    
    public User getEntity() {
        return entity;
    }
    
    public void onPrepareUpdateTask() {
        passwordBackup = entity.getPassword();
    }

    public boolean preCreateCheck() {
        Specification<User> spec1 = repository.presetSpecification();
        spec1.setPredicate(spec1.getBuilder().equal(spec1.getRoot().get("userName"), entity.getUserName()));
        Specification<User> spec2 = repository.presetSpecification();
        spec2.setPredicate(spec2.getBuilder().equal(spec2.getRoot().get("email"), entity.getEmail()));
        if ((repository.find(spec1) == null) && (repository.find(spec2) == null)) {
            String password = entity.getPassword().trim();
            boolean checked = checkPassword(password);
            boolean encrypted = encryptPassword(checked);
            passwordBackup = (encrypted && checked) ? entity.getPassword() : passwordBackup;
            return checked && encrypted;
        }
        if (repository.find(spec1) != null) {
            setMessage("newUserDuplicated");
        }
        if (repository.find(spec2) != null) {
            setMessage("emailAlreadyUsed");
        }
        return false;
    }

    public boolean preUpdateCheck() {
        Specification<User> spec1 = repository.presetSpecification();
        spec1.setPredicate(spec1.getBuilder().and(
            spec1.getBuilder().notEqual(spec1.getRoot().get("uuid"),  entity.getUuid()),
            spec1.getBuilder().equal(spec1.getRoot().get("userName"), entity.getUserName())));
        Specification<User> spec2 = repository.presetSpecification();
        spec2.setPredicate(spec2.getBuilder().and(
            spec2.getBuilder().notEqual(spec2.getRoot().get("uuid"),  entity.getUuid()),
            spec2.getBuilder().equal(spec2.getRoot().get("email"), entity.getEmail())));
        if ((repository.find(spec1) == null) && (repository.find(spec2) == null)) {
            boolean encrypted = true;
            boolean checked = true;
            String password = entity.getPassword().trim();
            if (password.trim().isEmpty()) {
                setMessage("reservedUserPassword");
                entity.setPassword(passwordBackup);
            } else {
                checked = checkPassword(password);
                encrypted = encryptPassword(checked);
                passwordBackup = (encrypted && checked) ? entity.getPassword() : passwordBackup;
            }
            return checked && encrypted;
        }
        if (repository.find(spec1) != null) {
            setMessage("updateUserDuplicated");
        }
        if (repository.find(spec2) != null) {
            setMessage("emailAlreadyUsed");
        }
        return false;
    }
    
    private boolean checkPassword(String password) {
        boolean checked = true;
        if ((password.length() < 8) || (password.length() > 512)) {
            setMessage("passwordOutOfRange");
            checked = false;
        }
        if (!password.matches(".*([a-zA-Z].*[0-9]|[0-9].*[a-zA-Z]|[a-zA-Z][0-9]|[0-9][a-zA-Z]).*")) {
            setMessage("insecurePassword");
            checked = false;
        }
        return checked;
    }
    
    private boolean encryptPassword(boolean checked) {
        if (checked) {
            try {
                String password = PBKDF2Tool.encrypt(entity.getPassword());
                entity.setPassword(password);
            } catch (Exception e) {
                setMessage("passwordEncryptionFailed");
                return false;
            }
        }
        return true;
    }
}
