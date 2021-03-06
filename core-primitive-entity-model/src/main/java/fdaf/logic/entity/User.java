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
// ADD_LABEL: Real name
package fdaf.logic.entity;

import fdaf.base.UserType;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.NotFound;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.io.Serializable;

@Table(name = "user")
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Invalid format for user name (allowed: a-z, A-Z, 0-9 and spaces).")
    @Size(min = 4, max = 128, message = "User name length out of range (min = 4, max = 128).")
    @NotBlank(message = "User name not specified.")
    @Column(name = "user_name")
    private String userName;
    
    @Transient
    private String currentPassword;
    
    @Transient
    private String newPassword;
    
    @Transient
    private String passwordConfirm;
    
    private String password;
    
    private String email;
    
    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType;
    
    @Column(name = "time_stamp")
    private Long timeStamp;
    
    @Column(name = "employee_id", nullable = true)
    private Long employeeId;
    
    private Boolean enabled;
    
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    // ENTITY_BODY
}
