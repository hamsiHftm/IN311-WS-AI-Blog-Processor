package ch.hftm.xml.ws.ai.blog.processor.dto;

import ch.hftm.xml.ws.ai.blog.processor.entity.ProcessingStatus;

public record FileUploadResponseDTO(
        Long recordId,
        String filePath,
        String jsonFilePath,
        ProcessingStatus status,
        String message
) {}
