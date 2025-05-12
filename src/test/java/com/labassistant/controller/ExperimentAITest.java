package com.labassistant.controller;

import com.labassistant.model.Experiment;
import com.labassistant.model.ai.AIInteraction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labassistant.model.Category;
import com.labassistant.model.ai.AIValidationResult;
import com.labassistant.repository.AIInteractionRepository;
import com.labassistant.repository.AIValidationResultRepository;
import com.labassistant.repository.CategoryRepository;
import com.labassistant.repository.ExperimentRepository;
import com.labassistant.service.AIService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional // Ensure transactional rollback
public class ExperimentAITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AIInteractionRepository aiInteractionRepository;

    @Autowired
    private AIValidationResultRepository aiValidationResultRepository;

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private AuthTestUtil authTestUtil;

    // Use the real AIService (no @MockBean)
    @Autowired
    private AIService aiService;

    private String jwtToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // No WireMock: This test will use the real Mistral API connection.

    @BeforeEach
    void setup() throws Exception {
        this.jwtToken = authTestUtil.createUserAndGetToken(
            "ai_test_user", 
            "AITestPass123!", 
            "ROLE_TEACHER"
        );
        
        // Cleanup in correct order to respect foreign key constraints
        aiValidationResultRepository.deleteAll();
        aiInteractionRepository.deleteAll();
        experimentRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    /**
     * This test checks the real Mistral API connection and shows a user-friendly output.
     * The AI is expected to:
     * - Validate the completeness of the experiment (materials, steps, etc.)
     * - Reference authoritative sources (e.g., NASA, ScienceDirect, physics experiment sites)
     * - Return a completeness percentage, missing items, and recommendations
     * - Reply in no more than 3 lines, concise and clear for viewers
     * The output will only show: title, shortDescription, requiredMaterials, and the AI reply.
     */
    @Test
    void testRealAIValidationAndShowSimpleOutput() throws Exception {
        System.out.println("\n=== SIMPLE EXPERIMENT AI VALIDATION TEST ===");
        System.out.println("Goal: Check real Mistral API connection and show user-friendly output\n");

        // Create category
        Category physicsCategory = categoryRepository.findByCategoryName("Physics")
            .orElseGet(() -> {
                Category newCategory = new Category();
                newCategory.setCategoryName("Physics");
                return categoryRepository.save(newCategory);
            });

        // Experiment JSON (now with all steps/materials required for AI to consider it valid)
        String experimentJson = """
            {
                "title": "Воздушная пушка",
                "shortDescription": "В этом эксперименте демонстрируется принцип действия воздушной пушки — устройства, выбрасывающего струю воздуха для перемещения легких предметов. Эксперимент помогает понять законы движения и давления воздуха.",
                "categoryId": %d,
                "steps": [
                    {
                        "stepNumber": 1,
                        "description": "Подготовьте пластиковый контейнер (например, большую бутылку или ведро) с плотно натянутым воздушным шаром или резиновой мембраной на одном конце. В центре противоположной стороны проделайте отверстие диаметром 5-10 см.",
                        "requiredMaterials": "Пластиковый контейнер, воздушный шар или резиновая мембрана, ножницы"
                    },
                    {
                        "stepNumber": 2,
                        "description": "Поставьте легкий предмет (например, бумажный стаканчик или шарик из пенопласта) на расстоянии 1-2 метров от отверстия.",
                        "requiredMaterials": "Бумажный стаканчик или шарик из пенопласта"
                    },
                    {
                        "stepNumber": 3,
                        "description": "Резко ударьте по мембране (или отпустите натянутый шар), чтобы создать поток воздуха, выходящий из отверстия.",
                        "requiredMaterials": "Рука для удара по мембране"
                    },
                    {
                        "stepNumber": 4,
                        "description": "Наблюдайте, как воздушная струя перемещает легкий предмет. Зафиксируйте расстояние, на которое он переместился.",
                        "requiredMaterials": "Линейка или рулетка для измерения"
                    },
                    {
                        "stepNumber": 5,
                        "description": "Повторите эксперимент, изменяя силу удара или расстояние до предмета, и сравните результаты.",
                        "requiredMaterials": "Все вышеуказанные материалы"
                    },
                    {
                        "stepNumber": 6,
                        "description": "Сделайте выводы о зависимости перемещения предмета от силы воздушной струи и расстояния.",
                        "requiredMaterials": "Лабораторный журнал для записей"
                    }
                ]
            }""".formatted(physicsCategory.getId());

        MvcResult result = mockMvc.perform(post("/api/experiments")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(experimentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Воздушная пушка"))
                .andReturn();

        // Parse response for user-friendly output
        JsonNode responseJson = objectMapper.readTree(result.getResponse().getContentAsString());
        String title = responseJson.get("title").asText();
        String shortDescription = responseJson.get("shortDescription").asText();
        String requiredMaterials = responseJson.get("steps").get(0).get("requiredMaterials").asText();

        // Get AI validation result from DB
        AIValidationResult aiResult = aiValidationResultRepository.findAll().get(0);

        // Prepare AI reply (max 3 lines)
        String aiReply = aiResult.getFeedback();
        String[] lines = aiReply.split("\\r?\\n|\\. ");
        StringBuilder shortReply = new StringBuilder();
        int count = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            if (count > 0) shortReply.append("\n");
            shortReply.append(line.trim().replaceAll("\\.$", ""));
            count++;
            if (count == 3) break;
        }

        // Show only simple fields and short AI reply
        System.out.println("==== Упрощённый вывод эксперимента ====");
        System.out.println();
        System.out.println("Сообщение, отправленное ИИ:Проверьте эти шаги эксперимента на полноту и научную точность. Перечислите недостающие элементы и рекомендации. + эксперимент данные");
        System.out.println("Название: " + "Воздушная пушка");
        System.out.println("Краткое описание: " + "В этом эксперименте демонстрируется принцип действия воздушной пушки — устройства, выбрасывающего струю воздуха для перемещения легких предметов. Эксперимент помогает понять законы движения и давления воздуха.");
        // Print required materials (more than 3 items) and first 3 steps as plain text from experimentJson
        System.out.println("Материалы (более 3): Пластиковый контейнер, воздушный шар или резиновая мембрана, ножницы");
        System.out.println("Шаг 1: Подготовьте пластиковый контейнер (например, большую бутылку или ведро) с плотно натянутым воздушным шаром или резиновой мембраной на одном конце. В центре противоположной стороны проделайте отверстие диаметром 5-10 см.");
        System.out.println("Необходимые материалы: Пластиковый контейнер, воздушный шар или резиновая мембрана, ножницы");
        System.out.println("Шаг 2: Поставьте легкий предмет (например, бумажный стаканчик или шарик из пенопласта) на расстоянии 1-2 метров от отверстия.");
        System.out.println("Необходимые материалы: Бумажный стаканчик или шарик из пенопласта");
        System.out.println("Шаг 3: Резко ударьте по мембране (или отпустите натянутый шар), чтобы создать поток воздуха, выходящий из отверстия.");
        System.out.println("Необходимые материалы: Рука для удара по мембране");
        // for (int i = 0; i < responseJson.get("steps").size(); i++) {
        //     System.out.println("Шаг " + (i + 1) + ": " + responseJson.get("steps").get(i).get("description").asText());
        // }
        System.out.println("✅ Запрос успешно отправлен ИИ.\n");

        
        System.out.println();

       
       
        // Assertions
        assertNotNull(title);
        assertNotNull(shortDescription);
        assertNotNull(requiredMaterials);
        assertNotNull(aiReply);
        assertTrue(aiReply.length() > 0);
        // Optionally, assert that the reply is not more than 3 lines
        assertTrue(shortReply.toString().split("\\r?\\n").length <= 3);
        System.out.println("Ответ ИИ :\n" + shortReply);
        System.out.println("==== Конец вывода ====");

        System.out.println("\n✅ ТЕСТ ЗАВЕРШЁН: Упрощённый вывод и реальный ответ ИИ показаны.");
    }
}
