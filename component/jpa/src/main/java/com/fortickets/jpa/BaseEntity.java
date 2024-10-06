package com.fortickets.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    public static final String DELETED_FALSE = "is_deleted = false";

    @Column(name = "is_deleted")
    protected Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    protected LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @CreatedBy
    @Column(updatable = false)
    protected String createdBy;
    @LastModifiedBy
    protected String updatedBy;
    protected String deletedBy;


    public void delete(String email) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = email;
    }

    public void setDeletedYnFalse() {
        this.isDeleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }

    public Boolean isDeleted() {
        return this.isDeleted;
    }
}
