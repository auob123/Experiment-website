// F:/spring_app/src/main/java/com/labassistant/controller/SubmissionController.java
package com.labassistant.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.labassistant.payload.response.SubmissionResponse;
import com.labassistant.model.*;
import com.labassistant.payload.request.SubmissionRequest;
import com.labassistant.repository.*;
import com.labassistant.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
@RestController
@RequestMapping("/api/student/submissions")
public class SubmissionController {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);
    private final SubmissionService submissionService;
    private final UserRepository userRepository;
    private final ExperimentRepository experimentRepository;
    private final FileStorageService fileStorageService;

    public SubmissionController(SubmissionService submissionService,
                               UserRepository userRepository,
                               ExperimentRepository experimentRepository,
                               FileStorageService fileStorageService) {
        this.submissionService = submissionService;
        this.userRepository = userRepository;
        this.experimentRepository = experimentRepository;
        this.fileStorageService = fileStorageService;
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionResponse> submitResults(
        @RequestPart("submissionRequest") SubmissionRequest submissionRequest,
        @RequestParam("files") MultipartFile[] files
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        Experiment experiment = experimentRepository.findById(submissionRequest.getExperimentId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiment not found"));
    
        Submission submission = new Submission();
        submission.setExperiment(experiment);
        submission.setUser(user);
        submission.setStatus(Submission.SubmissionStatus.SUBMITTED);
    
        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
                }
                String storedFileName = fileStorageService.storeFile(file); // Может выбросить IOException
                
                Attachment attachment = new Attachment();
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFileType(file.getContentType());
                attachment.setFilePath(storedFileName);
                attachment.setFileSize(file.getSize());
                attachment.setSubmission(submission);
                
                submission.getAttachments().add(attachment);
            }
    
            Submission saved = submissionService.submitExperiment(submission);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                new SubmissionResponse(saved.getSubmissionId(), saved.getStatus().name())
            );
        } catch (IOException e) {
            // Логируем ошибку для отладки
            logger.error("File storage failed: {}", e.getMessage());
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to store file: " + e.getMessage()
            );
        }
    }
}