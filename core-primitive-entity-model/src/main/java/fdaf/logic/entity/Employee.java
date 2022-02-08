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
// ADD_LABEL: Full name
// ADD_LABEL: Personal information
// ADD_LABEL: Contact information
// ADD_LABEL: Employment information
// ADD_LABEL: DOB
package fdaf.logic.entity;

import fdaf.base.Gender;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.NotFound;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.io.Serializable;

@Table(name = "employee")
@Entity
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Invalid format first name.")
    @Size(min = 2, max = 24, message = "First name length out of range (min = 2, max = 24).")
    @NotBlank(message = "First name not specified.")
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "middle_name")
    private String middleName;
    
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Invalid format family name.")
    @Size(min = 2, max = 24, message = "Last name length out of range (min = 2, max = 24).")
    @NotBlank(message = "Last name not specified.")
    @Column(name = "last_name")
    private String lastName;
    
    @Max(value = 31, message = "D.O.B day must be smaller than 32.")
    @Min(value = 1, message = "D.O.B day must be greater than 0.")
    @Positive(message = "D.O.B day must be positive integer.")
    @Column(name = "dob_day")
    private Integer dobDay;
    
    @Column(name = "dob_month")
    private Integer dobMonth;
    
    @Positive(message = "D.O.B year must be positive integer.")
    @Column(name = "dob_year")
    private Integer dobYear;
    
    @Column(name = "employment_id")
    private String employmentId;
    
    private String ssn;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "department_id", nullable = true)
    private Long departmentId;
    
    @Column(name = "role_id", nullable = true)
    private Long roleId;
    
    private String address1;
    private String address2;
    
    @Pattern(regexp = "^(\\+[0-9\\-]+|[0-9\\-]+||[ ]+)$", message = "Invalid phone number format.")
    private String phone1;
    
    @Pattern(regexp = "^(\\+[0-9\\-]+|[0-9\\-]+||[ ]+)$", message = "Invalid phone number format.")
    private String phone2;
    
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private Department department;
    
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    // ENTITY_BODY
}
