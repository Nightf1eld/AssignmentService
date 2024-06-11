package ru.tcb.assignmentservice.domain.entities;

import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.data.DbView;
import io.jmix.data.DdlGeneration;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@DbView
@JmixEntity
@DdlGeneration(value = DdlGeneration.DbScriptGenerationMode.DISABLED)
@Table(name = "V_SIEBEL_EMP", schema = "SIEBEL")
@Entity
@Immutable
public class SiebelEmployee {
    @Column(name = "ROW_ID")
    @Id
    private String id;

    @Column(name = "LOGIN")
    private String username;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "UNI_RESP_FLG")
    private Boolean uniResp;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getUniResp() {
        return uniResp;
    }

    public void setUniResp(Boolean uniResp) {
        this.uniResp = uniResp;
    }
}