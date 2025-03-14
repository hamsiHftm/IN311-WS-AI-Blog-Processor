package ch.hftm.xml.ws.ai.blog.processor.boundry;

import ch.hftm.xml.ws.ai.blog.processor.dto.response.FileUploadResponseDTO;
import ch.hftm.xml.ws.ai.blog.processor.dto.response.ErrorResponseDTO1;
import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import ch.hftm.xml.ws.ai.blog.processor.entity.ProcessingStatus;
import ch.hftm.xml.ws.ai.blog.processor.service.FileProcessingProducer;
import ch.hftm.xml.ws.ai.blog.processor.service.FileProcessingRecordService;

import ch.hftm.xml.ws.ai.blog.processor.service.FileProcessingWorker;
import ch.hftm.xml.ws.ai.blog.processor.service.FileService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestQuery;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

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

    @Inject
    FileProcessingProducer fileProcessingProducer;

    @POST
    @Path("/upload")
    @Transactional
    public Response uploadFile(@QueryParam("path") @NotBlank String path,
                               @QueryParam("fileType") @Pattern(regexp = "pdf|html", message = "fileType must be either 'pdf' or 'html'") String fileType) {
        try {
            boolean isValid = fileService.validateFile(path, fileType);
            if (!isValid) {
                throw new IllegalStateException("File validation failed. Unknown error.");
            }
            FileProcessingRecord record = fileProcessingRecordService.uploadFile(path);
            return Response.ok(new FileUploadResponseDTO(
                    record.getId(),
                    record.getHtmlFilePath(),
                    record.getStatus(),
                    "File uploaded successfully"
            )).build();
        } catch (FileNotFoundException | IllegalStateException e) {
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

    @POST
    @Path("/generate/json/{fileProcessingRecordId}")
    public Response generateJson(@PathParam("fileProcessingRecordId") Long recordId,
                                 @RestQuery String JsonFilePathDes) {
        try {
            // Retrieve the record
            FileProcessingRecord record = fileProcessingRecordService.getFileProcessingRecord(recordId);
            if (record == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("FileProcessingRecord not found for ID: " + recordId).build();
            }
            // Read the HTML file content
            String htmlContent = fileService.readHtmlFile(record);

            record.setStatus(ProcessingStatus.PROCESSING);
            record.setJsonFilePath(JsonFilePathDes);
            fileProcessingRecordService.updateFileProcessingRecord(record);

            fileProcessingProducer.sendProcessingRequest(recordId);

            LOG.info("Started JSON generation for record ID: " + recordId);
            return Response.ok("Processing started for record ID: " + recordId).build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("File processing error: " + e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Unexpected error while generating JSON: ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unexpected error: " + e.getMessage()).build();
        }
    }


}
