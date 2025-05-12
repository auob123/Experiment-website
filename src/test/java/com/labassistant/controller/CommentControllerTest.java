package com.labassistant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.labassistant.model.Comment;
import com.labassistant.model.Experiment;
import com.labassistant.model.User;
import com.labassistant.service.CommentService;
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
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private Comment testComment;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        User student = new User();
        student.setId(1L);
        student.setUsername("student1");

        Experiment experiment = new Experiment();
        experiment.setId(1L);
        experiment.setTitle("Air Cannon Experiment");

        testComment = new Comment();
        testComment.setCommentId(1L); // Explicitly set the commentId
        testComment.setContent("Great experiment!");
        testComment.setCreatedAt(new Date());
        testComment.setStatus(Comment.CommentStatus.PENDING);
        testComment.setUser(student);
        testComment.setExperiment(experiment);
    }

    @Test
    @WithMockUser(username = "student1", roles = "STUDENT")
    void createComment_ShouldReturnCreatedComment() throws Exception {
        System.out.println("\n=== ТЕСТ: СОЗДАНИЕ КОММЕНТАРИЯ ===");
        System.out.println("1. Студент оставляет комментарий: 'Отличный эксперимент!'.");

        given(commentService.createComment(any(Comment.class))).willReturn(testComment);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "content": "Great experiment!",
                        "experiment": {
                            "experimentId": 1
                        }
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1)) // Ensure commentId is 1
                .andExpect(jsonPath("$.content").value("Great experiment!"))
                .andDo(result -> {
                    System.out.println("✅ Комментарий успешно создан!");
                    System.out.println("   ID комментария: 1");
                    System.out.println("   Статус: PENDING");
                });
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getPendingComments_ShouldReturnListOfComments() throws Exception {
        System.out.println("\n=== ТЕСТ: ПОЛУЧЕНИЕ ОЖИДАЮЩИХ КОММЕНТАРИЕВ ===");
        System.out.println("1. Администратор запрашивает список ожидающих комментариев.");

        given(commentService.getPendingComments()).willReturn(Collections.singletonList(testComment));

        mockMvc.perform(get("/api/comments/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(1)) // Ensure commentId is 1
                .andExpect(jsonPath("$[0].content").value("Great experiment!"))
                .andDo(result -> {
                    System.out.println("✅ Список комментариев успешно получен!");
                    System.out.println("   Количество комментариев: 1");
                });
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCommentStatus_ShouldReturnUpdatedComment() throws Exception {
        System.out.println("\n=== ТЕСТ: ОБНОВЛЕНИЕ СТАТУСА КОММЕНТАРИЯ ===");
        System.out.println("1. Администратор обновляет статус комментария на 'APPROVED'.");

        testComment.setStatus(Comment.CommentStatus.APPROVED);

        given(commentService.updateStatus(1L, Comment.CommentStatus.APPROVED)).willReturn(testComment);

        mockMvc.perform(patch("/api/comments/1/status")
                .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1)) // Ensure commentId is 1
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andDo(result -> {
                    System.out.println("✅ Статус комментария успешно обновлен!");
                    System.out.println("   Новый статус: APPROVED");
                });
    }
}
