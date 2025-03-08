package ch.hftm.xml.ws.ai.blog.processor.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FileUploadRequestDTO(
        @NotBlank(message = "File path must not be empty")
        String htmlFilePath
) {}
