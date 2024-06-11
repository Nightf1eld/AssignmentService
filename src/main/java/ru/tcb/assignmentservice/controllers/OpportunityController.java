package ru.tcb.assignmentservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tcb.assignmentservice.domain.dto.LockDto;
import ru.tcb.assignmentservice.domain.dto.RegisterDto;
import ru.tcb.assignmentservice.domain.dto.ReleaseDto;
import ru.tcb.assignmentservice.services.OpportunityService;

@RestController
@RequestMapping("api/v1/opportunity")
@Slf4j
public class OpportunityController {
    @Autowired
    private OpportunityService opportunityService;

    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody RegisterDto dto){
        opportunityService.register(dto.getOpportunity(), dto.getData());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping()
    public ResponseEntity<String> getOpportunity(@RequestParam String employeeId){
        log.info("Opportunity requested for employee: "+ employeeId);
        String opptyId = opportunityService.getOpportunity(employeeId);
        log.info("Returning oppty: " + opptyId);
        return new ResponseEntity<>(opptyId, HttpStatus.OK);
    }

    @PostMapping("lock")
    public ResponseEntity<Void> lock(@RequestBody LockDto dto){
        HttpStatus status = HttpStatus.ACCEPTED;
        try{
            opportunityService.lock(dto.getOpportunity(), dto.getEmployee());
        }catch (IllegalStateException e){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        return new ResponseEntity<>(status);
    }

    @PostMapping("release")
    public ResponseEntity<Void> release(@RequestBody ReleaseDto dto){
        opportunityService.release(dto.getOpportunity(), dto.isDelete(), dto.getStatus());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
