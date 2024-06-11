package ru.tcb.assignmentservice.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import ru.tcb.assignmentservice.domain.entities.Employee;
import ru.tcb.assignmentservice.domain.entities.Opportunity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "WORK_LOG", indexes = {
        @Index(name = "IDX_WORK_LOG_EMPLOYEE", columnList = "EMPLOYEE_ID"),
        @Index(name = "IDX_WORK_LOG_OPPORTUNITY", columnList = "OPTY_INT_ID")
})
@Entity
public class WorkLog {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Employee employee;

    @JoinColumn(name = "OPPORTUNITY_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Opportunity opportunity;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "OPTY_INT_ID", length = 50)
    private String opportunityIntegrationId;

    public String getOpportunityIntegrationId() {
        return opportunityIntegrationId;
    }

    public void setOpportunityIntegrationId(String opportunityIntegrationId) {
        this.opportunityIntegrationId = opportunityIntegrationId;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}