package com.labassistant.service;

import com.labassistant.model.Instruction;
import com.labassistant.model.ai.AIValidationResult;

public interface InstructionService {
    Instruction createInstruction(Instruction instruction);
    Instruction validateInstructionWithAI(Long id);
    Instruction publishInstruction(Long id);
    AIValidationResult getValidationResult(Long id);
}
