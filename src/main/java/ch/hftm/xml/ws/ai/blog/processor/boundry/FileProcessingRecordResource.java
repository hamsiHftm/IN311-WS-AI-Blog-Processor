package ch.hftm.xml.ws.ai.blog.processor.boundry;

import ch.hftm.xml.ws.ai.blog.processor.dto.request.FileUploadRequestDTO;
import ch.hftm.xml.ws.ai.blog.processor.dto.response.FileUploadResponseDTO;
import ch.hftm.xml.ws.ai.blog.processor.dto.response.ErrorResponseDTO1;
import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import ch.hftm.xml.ws.ai.blog.processor.service.FileProcessingRecordService;

import ch.hftm.xml.ws.ai.blog.processor.service.FileService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FileProcessingRecordResource {
    private static final Logger LOG = Logger.getLogger(FileProcessingRecordResource.class);

    @Inject
    FileProcessingRecordService fileProcessingRecordService;

    @Inject
    FileService fileService;

    @POST
    @Path("/upload")
    @Transactional
    public Response uploadFile(@Valid FileUploadRequestDTO request) {
        try {
            boolean isValid = fileService.validateFile(request.htmlFilePath());
            if (!isValid) {
                throw new Exception("File validation failed");
            }
            FileProcessingRecord record = fileProcessingRecordService.uploadFile(request.htmlFilePath());
            return Response.ok(new FileUploadResponseDTO(
                    record.getId(),
                    record.getHtmlFilePath(),
                    record.getStatus(),
                    "File uploaded successfully"
            )).build();
        } catch (FileNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO1(e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                    .entity(new ErrorResponseDTO1(e.getMessage()))
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponseDTO1("File read error: " + e.getMessage()))
                    .build();
        } catch (Exception e) {
            LOG.error("Unexpected error: ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponseDTO1("Unexpected error: " + e.getMessage()))
                    .build();
        }
    }
}
