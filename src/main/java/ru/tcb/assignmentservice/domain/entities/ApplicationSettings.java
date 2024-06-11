package ru.tcb.assignmentservice.domain.entities;

import io.jmix.appsettings.defaults.AppSettingsDefaultLong;
import io.jmix.appsettings.entity.AppSettingsEntity;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@JmixEntity
@Table(name = "APPLICATION_SETTINGS")
@Entity
public class ApplicationSettings extends AppSettingsEntity {

    @Column(name = "LOCK_DURATION")
    @AppSettingsDefaultLong(900)
    private Long lockDuration;

    public Long getLockDuration() {
        return lockDuration;
    }

    public void setLockDuration(Long lockDuration) {
        this.lockDuration = lockDuration;
    }
}