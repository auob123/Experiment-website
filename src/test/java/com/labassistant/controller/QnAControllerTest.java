package com.labassistant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.labassistant.model.Question;
import com.labassistant.model.User;
import com.labassistant.service.QnAService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QnAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QnAService qnaService;

    private Question testQuestion;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        User student = new User();
        student.setId(1L);
        student.setUsername("student1");

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setContent("What is the air pressure in the cannon?");
        testQuestion.setCreatedAt(new Date());
        testQuestion.setStatus(Question.QuestionStatus.PENDING);
        testQuestion.setUser(student);
    }

    @Test
    @WithMockUser(username = "student1", roles = "STUDENT")
    void submitQuestion_ShouldReturnCreatedQuestion() throws Exception {
        System.out.println("\n=== ТЕСТ: ОТПРАВКА ВОПРОСА ===");
        System.out.println("1. Студент задает вопрос: 'Какое давление воздуха в пушке?'.");

        given(qnaService.submitQuestion(any(Question.class))).willReturn(testQuestion);

        mockMvc.perform(post("/api/qna/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "content": "What is the air pressure in the cannon?"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("What is the air pressure in the cannon?"))
                .andDo(result -> {
                    System.out.println("✅ Вопрос успешно отправлен!");
                    System.out.println("   ID вопроса: 1");
                    System.out.println("   Статус: PENDING");
                });
    }

    @Test
    @WithMockUser(username = "teacher1", roles = "TEACHER")
    void getPendingQuestions_ShouldReturnListOfQuestions() throws Exception {
        System.out.println("\n=== ТЕСТ: ПОЛУЧЕНИЕ ОЖИДАЮЩИХ ВОПРОСОВ ===");
        System.out.println("1. Преподаватель запрашивает список ожидающих вопросов.");

        given(qnaService.getPendingQuestions()).willReturn(Collections.singletonList(testQuestion));

        mockMvc.perform(get("/api/qna/questions/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("What is the air pressure in the cannon?"))
                .andDo(result -> {
                    System.out.println("✅ Список вопросов успешно получен!");
                    System.out.println("   Количество вопросов: 1");
                });
    }

    @Test
    @WithMockUser(username = "teacher1", roles = "TEACHER")
    void answerQuestion_ShouldReturnUpdatedQuestion() throws Exception {
        System.out.println("\n=== ТЕСТ: ОТВЕТ НА ВОПРОС ===");
        System.out.println("1. Преподаватель отвечает на вопрос: 'Какое давление воздуха в пушке?'.");

        testQuestion.setStatus(Question.QuestionStatus.ANSWERED);
        testQuestion.setAnswer("The air pressure is 3 bar.");

        given(qnaService.answerQuestion(1L, "The air pressure is 3 bar.")).willReturn(testQuestion);

        mockMvc.perform(post("/api/qna/questions/1/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("The air pressure is 3 bar."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ANSWERED"))
                .andExpect(jsonPath("$.answer").value("The air pressure is 3 bar."))
                .andDo(result -> {
                    System.out.println("✅ Ответ на вопрос успешно сохранен!");
                    System.out.println("   Ответ: 'Давление воздуха составляет 3 бара.'");
                });
    }
}
