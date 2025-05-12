package com.labassistant.controller;

import com.labassistant.model.Instruction;
import com.labassistant.service.InstructionService;
import com.labassistant.model.ai.AIValidationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final InstructionService instructionService;

    public TeacherController(InstructionService instructionService) {
        this.instructionService = instructionService;
    }

    @PostMapping("/instructions")
    public ResponseEntity<Instruction> createInstruction(@RequestBody Instruction instruction) {
        return ResponseEntity.ok(instructionService.createInstruction(instruction));
    }

    @PostMapping("/instructions/{id}/validate")
    public ResponseEntity<Instruction> validateInstruction(@PathVariable Long id) {
        return ResponseEntity.ok(instructionService.validateInstructionWithAI(id));
    }

    @PostMapping("/instructions/{id}/publish")
    public ResponseEntity<Instruction> publishInstruction(@PathVariable Long id) {
        return ResponseEntity.ok(instructionService.publishInstruction(id));
    }

    @GetMapping("/instructions/{id}/validation")
    public ResponseEntity<AIValidationResult> getValidationResult(@PathVariable Long id) {
        return ResponseEntity.ok(instructionService.getValidationResult(id));
    }
}
