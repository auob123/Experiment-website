package com.labassistant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.labassistant.model.Experiment;
import com.labassistant.model.Submission;
import com.labassistant.model.User;
import com.labassistant.repository.ExperimentRepository;
import com.labassistant.repository.UserRepository;
import com.labassistant.service.FileStorageService;
import com.labassistant.service.NotificationService;
import com.labassistant.service.SubmissionService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExperimentRepository experimentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SubmissionService submissionService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private NotificationService notificationService;

    private final String SUBMISSIONS_ENDPOINT = "/api/student/submissions";
    private User testStudent;
    private Experiment testExperiment;

    @BeforeEach
    void setup() {
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setUsername("student1");

        testExperiment = new Experiment();
        testExperiment.setId(1L);
        testExperiment.setTitle("Air Cannon Experiment"); // Restore original title

        given(userRepository.findByUsername("student1"))
            .willReturn(Optional.of(testStudent));
        given(experimentRepository.findById(1L))
            .willReturn(Optional.of(testExperiment));
        given(experimentRepository.findById(999L))
            .willReturn(Optional.empty());
    }

    @Test
    @WithMockUser(username = "student1", roles = "STUDENT")
    void submitWithFiles_ShouldReturnCreated() throws Exception {
        System.out.println("\n=== ТЕСТ: УСПЕШНАЯ ОТПРАВКА С ФАЙЛАМИ ===");
        System.out.println("1. Студент входит как 'student1'");
        System.out.println("2. Выбирает эксперимент 'Воздушная пушка'");
        System.out.println("3. Вводит наблюдения: 'Измерения давления'");
        System.out.println("4. Загружает файлы:");
        System.out.println("   - setup_photo.jpg (изображение JPEG)");
        System.out.println("   - experiment_video.mp4 (видео MP4)");
        System.out.println("5. Нажимает кнопку 'Отправить'");

        given(submissionService.submitExperiment(any(Submission.class)))
            .willAnswer(invocation -> {
                Submission sub = invocation.getArgument(0);
                sub.setSubmissionId(1L);
                return sub;
            });

        MockMultipartFile photoFile = new MockMultipartFile(
            "files", "setup_photo.jpg", "image/jpeg", "photo_data".getBytes()
        );
        MockMultipartFile videoFile = new MockMultipartFile(
            "files", "experiment_video.mp4", "video/mp4", "video_data".getBytes()
        );
        MockMultipartFile jsonPart = new MockMultipartFile(
            "submissionRequest", "", "application/json",
            ("{\"experimentId\":1,\"observations\":\"Pressure measurements\"}").getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart(SUBMISSIONS_ENDPOINT)
                .file(photoFile)
                .file(videoFile)
                .file(jsonPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.submissionId").exists())
                .andExpect(jsonPath("$.status").value("SUBMITTED")) 
                .andDo(result -> {
                    System.out.println("✅ Отправка успешно выполнена!");
                    System.out.println("   ID отправки: 1");
                    System.out.println("   Статус: ОТПРАВЛЕНО");
                    System.out.println("   Сообщение: 'Ваша работа ожидает проверки'");
                });
    }
}