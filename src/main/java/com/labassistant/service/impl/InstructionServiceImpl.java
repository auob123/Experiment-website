package com.labassistant.service.impl;

import com.labassistant.model.Instruction;
import com.labassistant.model.ai.AIValidationResult;
import com.labassistant.model.ai.AIInteraction;
import com.labassistant.repository.InstructionRepository;
import com.labassistant.repository.AIInteractionRepository;
import com.labassistant.service.AIService;
import com.labassistant.service.InstructionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class InstructionServiceImpl implements InstructionService {

    private final InstructionRepository instructionRepository;
    private final AIService aiService;
    private final AIInteractionRepository aiInteractionRepository;

    public InstructionServiceImpl(InstructionRepository instructionRepository,
                                 AIService aiService,
                                 AIInteractionRepository aiInteractionRepository) {
        this.instructionRepository = instructionRepository;
        this.aiService = aiService;
        this.aiInteractionRepository = aiInteractionRepository;
    }

    @Override
    public Instruction createInstruction(Instruction instruction) {
        instruction.setCreationDate(new Date());
        instruction.setStatus(Instruction.InstructionStatus.DRAFT);
        return instructionRepository.save(instruction);
    }

    @Override
    public Instruction validateInstructionWithAI(Long instructionId) {
        Instruction instruction = instructionRepository.findById(instructionId)
            .orElseThrow(() -> new RuntimeException("Instruction not found"));
        
        AIInteraction interaction = new AIInteraction();
        interaction.setInputText(instruction.getContent()); // FIXED ARGUMENT
        interaction.setInteractionType(AIInteraction.InteractionType.INSTRUCTION_VALIDATION);
        interaction = aiInteractionRepository.save(interaction);
        
        AIValidationResult validationResult = aiService.validateInstruction(instruction.getContent()); // FIXED TYPE
        AIValidationResult validation = new AIValidationResult();
        validation.setValidationDate(new Date());
        validation.setSafetyApproved(validationResult.isSafetyApproved());
        validation.setScientificAccuracy(validationResult.isScientificAccuracy());
        validation.setValidationSource("Mistral AI");
        validation.setValidatedInstruction(validationResult.getValidatedInstruction());
        validation.setInteraction(interaction);
        
        instruction.setValidationResult(validation);
        instruction.setStatus(Instruction.InstructionStatus.VALIDATION_PENDING);
        
        return instructionRepository.save(instruction);
    }

    @Override
    public Instruction publishInstruction(Long instructionId) {
        Instruction instruction = instructionRepository.findById(instructionId)
            .orElseThrow(() -> new RuntimeException("Instruction not found"));
        
        if (instruction.getValidationResult() == null || 
            !instruction.getValidationResult().isSafetyApproved()) {
            throw new RuntimeException("Instruction not validated or unsafe");
        }
        
        instruction.setStatus(Instruction.InstructionStatus.PUBLISHED);
        instruction.setPublishDate(new Date());
        return instructionRepository.save(instruction);
    }

    @Override
    public AIValidationResult getValidationResult(Long instructionId) {
        return instructionRepository.findById(instructionId)
            .map(Instruction::getValidationResult)
            .orElseThrow(() -> new RuntimeException("Instruction not found"));
    }
}