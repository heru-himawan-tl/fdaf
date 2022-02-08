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
package fdaf.logic.base;

import fdaf.base.Permission;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class PermissionPredicateFactory<E> {

    private final CriteriaBuilder criteriaBuilder;
    private final Long authorId;
    private final Long userGroupId;
    private final Root<E> root;

    public PermissionPredicateFactory(CriteriaBuilder criteriaBuilder, Root<E> root, Long authorId, Long userGroupId) {
        this.authorId = authorId;
        this.criteriaBuilder = criteriaBuilder;
        this.root = root;
        this.userGroupId = userGroupId;
    }

    private Predicate isEqualPermission(Permission permission) {
        return criteriaBuilder.equal(root.get("permission"), permission);
    }

    public Predicate isPrivate() {
        return isEqualPermission(Permission.PRIVATE);
    }

    public Predicate isReadOnlyForAll() {
        return isEqualPermission(Permission.READ_ONLY_FOR_ALL);
    }

    public Predicate isReadOnlyForGroupNoneForOthers() {
        return isEqualPermission(Permission.READ_ONLY_FOR_GROUP_NONE_FOR_OTHERS);
    }

    public Predicate isReadWriteDeleteForAll() {
        return isEqualPermission(Permission.READ_WRITE_DELETE_FOR_ALL);
    }

    public Predicate isReadWriteDeleteForGroupAndReadOnlyForOthers() {
        return isEqualPermission(Permission.READ_WRITE_DELETE_FOR_GROUP_AND_READ_ONLY_FOR_OTHERS);
    }

    public Predicate isReadWriteDeleteForGroupAndReadWriteForOthers() {
        return isEqualPermission(Permission.READ_WRITE_DELETE_FOR_GROUP_AND_READ_WRITE_FOR_OTHERS);
    }

    public Predicate isReadWriteDeleteForGroupNoneForOthers() {
        return isEqualPermission(Permission.READ_WRITE_DELETE_FOR_GROUP_NONE_FOR_OTHERS);
    }

    public Predicate isReadWriteForAll() {
        return isEqualPermission(Permission.READ_WRITE_FOR_ALL);
    }

    public Predicate isReadWriteForGroupAndReadOnlyForOthers() {
        return isEqualPermission(Permission.READ_WRITE_FOR_GROUP_AND_READ_ONLY_FOR_OTHERS);
    }

    public Predicate isReadWriteForGroupNoneForOthers() {
        return isEqualPermission(Permission.READ_WRITE_FOR_GROUP_NONE_FOR_OTHERS);
    }

    private Predicate isNotEqualPermission(Permission permission) {
        return criteriaBuilder.notEqual(root.get("permission"), permission);
    }

    public Predicate isNotPrivate() {
        return isNotEqualPermission(Permission.PRIVATE);
    }

    public Predicate isNotReadOnlyForAll() {
        return isNotEqualPermission(Permission.READ_ONLY_FOR_ALL);
    }

    public Predicate isNotReadOnlyForGroupNoneForOthers() {
        return isNotEqualPermission(Permission.READ_ONLY_FOR_GROUP_NONE_FOR_OTHERS);
    }

    public Predicate isNotReadWriteDeleteForAll() {
        return isNotEqualPermission(Permission.READ_WRITE_DELETE_FOR_ALL);
    }

    public Predicate isNotReadWriteDeleteForGroupAndReadOnlyForOthers() {
        return isNotEqualPermission(Permission.READ_WRITE_DELETE_FOR_GROUP_AND_READ_ONLY_FOR_OTHERS);
    }

    public Predicate isNotReadWriteDeleteForGroupAndReadWriteForOthers() {
        return isNotEqualPermission(Permission.READ_WRITE_DELETE_FOR_GROUP_AND_READ_WRITE_FOR_OTHERS);
    }

    public Predicate isNotReadWriteDeleteForGroupNoneForOthers() {
        return isNotEqualPermission(Permission.READ_WRITE_DELETE_FOR_GROUP_NONE_FOR_OTHERS);
    }

    public Predicate isNotReadWriteForAll() {
        return isNotEqualPermission(Permission.READ_WRITE_FOR_ALL);
    }

    public Predicate isNotReadWriteForGroupAndReadOnlyForOthers() {
        return isNotEqualPermission(Permission.READ_WRITE_FOR_GROUP_AND_READ_ONLY_FOR_OTHERS);
    }

    public Predicate isNotReadWriteForGroupNoneForOthers() {
        return isNotEqualPermission(Permission.READ_WRITE_FOR_GROUP_NONE_FOR_OTHERS);
    }

    public Predicate isBelongTo() {
        return criteriaBuilder.equal(root.get("authorId"), authorId);
    }

    public Predicate isInUserGroup() {
        return criteriaBuilder.equal(root.get("userGroupId"), userGroupId);
    }

    public Predicate isNotBelongTo() {
        return criteriaBuilder.notEqual(root.get("authorId"), authorId);
    }

    public Predicate isNotInUserGroup() {
        return criteriaBuilder.notEqual(root.get("userGroupId"), userGroupId);
    }

    public Predicate isAccessible() {
        return criteriaBuilder.or(
            criteriaBuilder.and(isInUserGroup(), isNotBelongTo(), isNotPrivate()), isBelongTo(),
            criteriaBuilder.and(isNotBelongTo(), isNotInUserGroup(),
                criteriaBuilder.or(
                    isReadWriteDeleteForGroupAndReadWriteForOthers(),
                    isNotPrivate(),
                    isReadWriteDeleteForAll(),
                    isReadWriteForAll(),
                    isReadWriteDeleteForGroupAndReadOnlyForOthers())));
    }
}
