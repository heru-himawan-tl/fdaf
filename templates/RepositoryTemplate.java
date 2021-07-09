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
package fdaf.logic.ejb.repository;

import fdaf.logic.base.AbstractRepository;
import fdaf.logic.entity.__NAME__;
import java.io.Serializable;
import javax.ejb.Stateful;
// --------------------------------------------------------------------------
// If you want to apply TransactionManagementType.BEAN
//import javax.ejb.TransactionManagement;
//import javax.ejb.TransactionManagementType;
// --------------------------------------------------------------------------
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

// --------------------------------------------------------------------------
// If you want to apply TransactionManagementType.BEAN
//@TransactionManagement(TransactionManagementType.BEAN)
// --------------------------------------------------------------------------
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateful
public class __NAME__Repository extends AbstractRepository<__NAME__> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "${fdaf.jpaUnitName}", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    public __NAME__Repository() {
        super(__NAME__.class);
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
