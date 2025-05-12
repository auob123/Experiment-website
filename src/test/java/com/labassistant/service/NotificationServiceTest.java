package com.labassistant.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.labassistant.model.Notification;
import com.labassistant.model.User;
import com.labassistant.repository.NotificationRepository;
import com.labassistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(notificationRepository, userRepository);
    }

    @Test
    void shouldCreateNotificationForStudentSubmission() {
        System.out.println("\n=== ТЕСТ: УВЕДОМЛЕНИЕ О НОВОЙ ОТПРАВКЕ ЭКСПЕРИМЕНТА ===");
        System.out.println("1. Студент отправляет эксперимент с названием 'Воздушная пушка'.");

        User student = new User();
        student.setId(1L);
        student.setUsername("student1");

        given(userRepository.findById(1L)).willReturn(java.util.Optional.of(student));

        notificationService.createNotification(
            1L,
            Notification.NotificationType.SUBMISSION_UPDATE,
            "New submission for experiment 1",
            1L
        );

        verify(notificationRepository).save(any(Notification.class));
        System.out.println("✅ Уведомление успешно создано!");
        System.out.println("   - Тип: SUBMISSION_UPDATE");
        System.out.println("   - Сообщение: 'Новая отправка для эксперимента 1'");
        System.out.println("   - ID связанной сущности: 1");
    }

    @Test
    void shouldCreateNotificationForNewQuestion() {
        System.out.println("\n=== ТЕСТ: УВЕДОМЛЕНИЕ О НОВОМ ВОПРОСЕ ===");
        System.out.println("1. Студент задает вопрос: 'Какое давление воздуха в пушке?'.");

        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");

        given(userRepository.findById(2L)).willReturn(java.util.Optional.of(admin));

        notificationService.createNotification(
            2L,
            Notification.NotificationType.NEW_QUESTION,
            "New question submitted",
            1L
        );

        verify(notificationRepository).save(any(Notification.class));
        System.out.println("✅ Уведомление успешно создано!");
        System.out.println("   - Тип: NEW_QUESTION");
        System.out.println("   - Сообщение: 'Задан новый вопрос'");
        System.out.println("   - ID связанной сущности: 1");
    }

    @Test
    void shouldCreateNotificationForAnsweredQuestion() {
        System.out.println("\n=== ТЕСТ: УВЕДОМЛЕНИЕ О ОТВЕТЕ НА ВОПРОС ===");
        System.out.println("1. Преподаватель отвечает на вопрос: 'Какое давление воздуха в пушке?'.");

        User student = new User();
        student.setId(1L);
        student.setUsername("student1");

        given(userRepository.findById(1L)).willReturn(java.util.Optional.of(student));

        notificationService.createNotification(
            1L,
            Notification.NotificationType.QUESTION_ANSWERED,
            "Your question has been answered",
            1L
        );

        verify(notificationRepository).save(any(Notification.class));
        System.out.println("✅ Уведомление успешно создано!");
        System.out.println("   - Тип: QUESTION_ANSWERED");
        System.out.println("   - Сообщение: 'На ваш вопрос был дан ответ'");
        System.out.println("   - ID связанной сущности: 1");
    }
}
