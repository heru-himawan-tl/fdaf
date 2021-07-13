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

import fdaf.base.PasswordResetInterface;
import fdaf.base.tools.PBKDF2Tool;
import fdaf.logic.base.AbstractFacade;
import fdaf.logic.base.Specification;
import fdaf.logic.ejb.repository.UserRepository;
import fdaf.logic.entity.User;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.concurrent.TimeUnit;
import javax.ejb.StatefulTimeout;

@StatefulTimeout(value = -1)
@Remote({PasswordResetInterface.class})
@Stateful(passivationCapable = false)
public class PasswordResetFacade extends AbstractFacade<UserRepository, User> implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PasswordResetFacade.class.getName());
    
    private static final long serialVersionUID = 1L;

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new SecureRandom();
    private String encryptedPassword;
    private String password;
    
    @EJB
    private UserRepository repository;

    public PasswordResetFacade() {
        super(User.class);
    }
    
    protected void setUuid() {
        entity.setUuid(uuid);
    }

    protected UserRepository getRepository() {
        return repository;
    }
    
    public String getUserName() {
        return entity.getUserName();
    }
    
    public String getPassword() {
        return password;
    }

    public boolean findUser(String email) {
        Specification<User> userSpec = repository.presetSpecification();
        userSpec.setPredicate(userSpec.getBuilder().equal(userSpec.getRoot().get("email"), email));
        User user = repository.find(userSpec);
        if (user != null) {
            entity = user;
            return true;
        }
        return false;
    }
    
    public boolean preUpdateCheck() {
        try {
            StringBuilder p = new StringBuilder(12);
            for (int i = 0; i < 12; i++) {
                p.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
            }
            encryptedPassword = PBKDF2Tool.encrypt(new String(p));
            password = new String(p);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public void update() {
        updateRecordDate();
        entity.setModificationDate(recordDate);
        entity.setCreatedDate(recordDate);
        entity.setPassword(encryptedPassword);
        repository.update(entity);
    }

    protected User newEntity() {
        return new User();
    }

    @Override
    public boolean preCreateCheck() {
        return true;
    }

    @Override
    public void postCreateTask() {
        // NO-OP
    }

    @Override
    public void rollbackCreateTask() {
        // NO-OP
    }

    public Object getNewRecordId() {
        return (entity == null) ? 0 : entity.getId();
    }
}
