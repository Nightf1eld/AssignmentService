package ru.tcb.assignmentservice.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RegisterDto {
    private String opportunity;
    private Map<String, String> data;
}
