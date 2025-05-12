// F:/spring_app/src/main/java/com/labassistant/repository/AttachmentRepository.java
package com.labassistant.repository;

import com.labassistant.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}