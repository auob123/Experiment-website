package com.labassistant.service;

import com.labassistant.model.User;
import com.labassistant.model.Experiment;
import com.labassistant.model.Step;
import com.labassistant.model.Category;
import com.labassistant.model.ai.AIValidationResult;
import java.util.List;
import java.util.Optional;
import com.labassistant.payload.request.ExperimentRequest;
import com.labassistant.repository.ExperimentRepository;
import com.labassistant.repository.CategoryRepository;
import com.labassistant.repository.AIInteractionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperimentServiceTest {

    
    @Mock
    private ExperimentRepository experimentRepository;
    @Mock
    private AIService aiService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AIInteractionRepository aiInteractionRepository;
    @InjectMocks
    private ExperimentService experimentService;

    @Mock
    private EntityManager entityManager;

    private void injectEntityManager() {
        try {
            Field field = ExperimentService.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(experimentService, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createExperiment_WithValidRequestAndUser_ReturnsExperiment() {
        injectEntityManager();
        try {
            ExperimentRequest request = new ExperimentRequest();
            request.setTitle("Test Experiment");
            request.setShortDescription("Test Description");
            request.setCategoryId(1L);

            when(aiService.validateExperimentSteps(anyList()))
                .thenReturn(new AIValidationResult(true, "Valid", "100"));
            when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(new Category()));

            User user = new User();
            user.setId(1L);
            
            com.labassistant.payload.request.StepRequest testStep = new com.labassistant.payload.request.StepRequest();
            testStep.setDescription("Description");
            testStep.setStepNumber(1);
            request.setSteps(List.of(testStep));

            Experiment expectedExperiment = new Experiment();
            expectedExperiment.setTitle(request.getTitle());
            expectedExperiment.setShortDescription(request.getShortDescription());
            expectedExperiment.setCreatedBy(user);

            when(experimentRepository.save(any(Experiment.class))).thenReturn(expectedExperiment);
            when(aiInteractionRepository.save(any())).thenReturn(null); // Mock save behavior

            Experiment result = experimentService.createExperiment(request, user);

            // Simulate a real Russian AI answer for demonstration
            String aiAnswer = "Название: Воздушная пушка\nКраткое описание: В этом эксперименте демонстрируется принцип действия воздушной пушки — устройства, выбрасывающего струю воздуха для перемещения легких предметов. Эксперимент помогает понять законы движения и давления воздуха.\nОписание шага 1: Подготовьте пластиковый контейнер (например, большую бутылку или ведро) с плотно натянутым воздушным шаром или резиновой мембраной на одном конце. В центре противоположной стороны проделайте отверстие диаметром 5-10 см.\nОписание шага 2: Поставьте легкий предмет (например, бумажный стаканчик или шарик из пенопласта) на расстоянии 1-2 метров от отверстия.\nНеобходимые материалы: Пластиковый контейнер, воздушный шар или резиновая мембрана, ножницы";

            System.out.println("Ответ ИИ (макс. 3 строки):");
            System.out.println(aiAnswer);

            assertNotNull(result);
            assertEquals(request.getTitle(), result.getTitle());
        } catch (Throwable t) {
            throw t;
        }
    }

    @Test
    void createExperiment_WithEntityAndUser_ReturnsSavedExperiment() {
        injectEntityManager();
        try {
            ExperimentRequest request = new ExperimentRequest();
            request.setTitle("Test Experiment");
            request.setShortDescription("Test Description");
            request.setCategoryId(1L);

            when(aiService.validateExperimentSteps(anyList()))
                .thenReturn(new AIValidationResult(true, "Valid", "100"));
            when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(new Category()));

            User user = new User();
            user.setId(1L);
            
            com.labassistant.payload.request.StepRequest testStep = new com.labassistant.payload.request.StepRequest();
            testStep.setDescription("Description");
            testStep.setStepNumber(1);
            request.setSteps(List.of(testStep));

            Experiment expectedExperiment = new Experiment();
            expectedExperiment.setTitle(request.getTitle());
            expectedExperiment.setCreatedBy(user);

            when(experimentRepository.save(any(Experiment.class))).thenReturn(expectedExperiment);
            when(aiInteractionRepository.save(any())).thenReturn(null); // Mock save behavior

            Experiment result = experimentService.createExperiment(request, user);

            assertNotNull(result);
            assertEquals(request.getTitle(), result.getTitle());
            assertEquals(user, result.getCreatedBy());
        } catch (Throwable t) {
            throw t;
        }
    }
}
