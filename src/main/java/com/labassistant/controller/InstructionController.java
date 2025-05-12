package com.labassistant.controller;

import com.labassistant.model.Instruction;
import com.labassistant.model.User;
import com.labassistant.model.ai.AIValidationResult;
import com.labassistant.repository.UserRepository;
import com.labassistant.service.InstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/instructions")
public class InstructionController {

    private final InstructionService instructionService;
    private final UserRepository userRepository;

    @Autowired
    public InstructionController(InstructionService instructionService, 
                                UserRepository userRepository) {
        this.instructionService = instructionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Instruction> createInstruction(
        @RequestBody Instruction instruction
    ) {
        // Set server-controlled fields
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        instruction.setAuthor(author);
        instruction.setCreationDate(new Date());
        instruction.setStatus(Instruction.InstructionStatus.DRAFT);
        
        return ResponseEntity.ok(
            instructionService.createInstruction(instruction)
        );
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<Instruction> validateWithAI(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            instructionService.validateInstructionWithAI(id)
        );
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Instruction> publishInstruction(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            instructionService.publishInstruction(id)
        );
    }

    @GetMapping("/{id}/validation-result")
    public ResponseEntity<AIValidationResult> getValidationResult(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            instructionService.getValidationResult(id)
        );
    }
}