package ru.tcb.assignmentservice.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReleaseDto {
    private String opportunity;
    private boolean delete;
    private String status;
}
