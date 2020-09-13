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
package fdaf.logic.entity;

import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.NotFound;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

// WITHOUT_DATA_PROPERTY
// NO_WEB_APP_BEAN_GEN

@Table(name = "user_login")
@Entity
public class UserLogin implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "user_id", nullable = true)
    private Long userId;
    @Column(name = "out_epoch_time_stamp")
    private Long outEpochTimeStamp;
    @Column(name = "out_time_stamp")
    private String outTimeStamp;
    @Column(name = "in_epoch_time_stamp")
    private Long inEpochTimeStamp;
    @Column(name = "in_time_stamp")
    private String inTimeStamp;
    @Column(name = "user_session_id", nullable = false)
    private String userSessionID;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "logout_state", nullable = false)
    private Boolean logoutState;
    @Column(name = "logout_flag")
    private Integer logoutFlag;
    @Column(name = "live_time")
    private Integer liveTime;
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // ENTITY_BODY
}
