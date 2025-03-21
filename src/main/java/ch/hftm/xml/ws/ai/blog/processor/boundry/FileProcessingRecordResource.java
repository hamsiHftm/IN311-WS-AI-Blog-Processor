package ch.hftm.xml.ws.ai.blog.processor.boundry;

import ch.hftm.xml.ws.ai.blog.processor.dto.FileUploadResponseDTO;
import ch.hftm.xml.ws.ai.blog.processor.dto.ErrorResponseDTO1;
import ch.hftm.xml.ws.ai.blog.processor.dto.ResponseDTO1;
import ch.hftm.xml.ws.ai.blog.processor.entity.Blog;
import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import ch.hftm.xml.ws.ai.blog.processor.entity.FileType;
import ch.hftm.xml.ws.ai.blog.processor.entity.ProcessingStatus;
import ch.hftm.xml.ws.ai.blog.processor.service.*;

import ch.hftm.xml.ws.ai.blog.processor.service.messaging.FileProcessingProducer;
import ch.hftm.xml.ws.ai.blog.processor.service.model.FileProcessingRecordService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.common.NotImplementedYet;

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

    @Inject
    FileProcessingProducer fileProcessingProducer;

    @Inject
    JsonProcessingService jsonProcessingService;

    /*
    @Inject
    FileUploadingProducer fileUploadingProducer;

    @Inject
    OpenAIFileService openAIFileService;

    @Inject
    PDFProcessingAIService pdfProcessingAIService;
     */

    @POST
    @Path("/upload")
    public Response uploadFile(@QueryParam("path") @NotBlank String path,
                               @QueryParam("fileType") @Pattern(regexp = "pdf|html", message = "fileType must be either 'pdf' or 'html'") String fileType) {
        try {
            boolean isValid = fileService.validateFile(path, fileType);
            if (!isValid) {
                throw new IllegalStateException("File validation failed. Unknown error.");
            }
            FileType fileTypeEnum = FileType.valueOf(fileType.toUpperCase());
            ProcessingStatus status = fileTypeEnum == FileType.HTML ? ProcessingStatus.UPLOADED : ProcessingStatus.UPLOADING;
            FileProcessingRecord record = fileProcessingRecordService.uploadFile(path, fileTypeEnum, status);

            if (fileTypeEnum == FileType.PDF) {
                /*
                fileProcessingRecordService.updateStatusForRecord(record.getId(), ProcessingStatus.UPLOADED);
                // fileUploadingProducer.sendFileUploadRequest(record.getId());

                File file = new File(record.getFilePath());
                if (!file.exists() || !file.isFile()) {
                    LOG.error("File not found: " + file.getAbsolutePath());
                    throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
                }

                // checking pdf upload
                // String field = openAIFileService.uploadFileToOpenAI(file);
                String fileId = "file-F3JnQY9wp7d2aZoE5XdRvP";
                record.setOpenAIFileId(fileId);
                String response = pdfProcessingAIService.extractContent(fileId);
                return Response.status(Response.Status.OK).entity(response).build();
                 */
                throw new NotImplementedYet();
            }

            return Response.ok(new ResponseDTO1(true, new FileUploadResponseDTO(
                    record.getId(),
                    record.getFilePath(),
                    record.getJsonFilePath(),
                    record.getStatus(),
                    "File uploaded successfully"
            ))).build();
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
    public Response generateJson(@PathParam("fileProcessingRecordId") Long recordId) {
        try {
            // Retrieve the record
            FileProcessingRecord record = fileProcessingRecordService.getFileProcessingRecord(recordId);
            if (record == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponseDTO1("FileProcessingRecord not found for ID: " + recordId))
                        .build();
            }

            // check if the file is still on uploaded
            if (record.getStatus() == ProcessingStatus.UPLOADING) {
                return generateSuccessResponse(recordId, record, "Uploading is not completed. So pls wait..");
            }

            // Check if the file is already processed
            if (record.getStatus() == ProcessingStatus.PROCESSING) {
                return generateSuccessResponse(recordId, record, "Processing already started.");
            }

            // Check if the file is already processed
            if (record.getStatus() == ProcessingStatus.PROCESSED) {
                return generateSuccessResponse(recordId, record, "Processing already completed. JSON file available at: " + record.getJsonFilePath() + record.getJsonFilePath());
            }

            // Check if the file is already processed
            if (record.getStatus() == ProcessingStatus.STORED) {
                return generateSuccessResponse(recordId, record, "Blog is already stored in db. Pls check this url to see the content: http://localhost:8080/blog-preview/" + record.getBlog().getId());
            }

            // Check if the file is already processed
            if (record.getStatus() == ProcessingStatus.FAILED) {
                return generateSuccessResponse(recordId, record, "Something went wrong with this file. Pls check the fail");
            }

            // Update record status and file path
            record.setStatus(ProcessingStatus.PROCESSING);
            fileProcessingRecordService.updateFileProcessingRecord(record);

            // Send processing request to Kafka
            fileProcessingProducer.sendProcessingRequest(recordId);
            LOG.info("Started JSON generation for record ID: " + recordId);
            return generateSuccessResponse(recordId, record, "Processing started successfully.");

        } catch (Exception e) {
            LOG.error("Unexpected error while generating JSON: ", e);
            fileProcessingRecordService.updateStatusForRecord(recordId, ProcessingStatus.FAILED);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponseDTO1("Unexpected error: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/status/{fileProcessingRecordId}")
    public Response getFileProcessingStatus(@PathParam("fileProcessingRecordId") Long recordId) {
        try {
            // Retrieve the record
            FileProcessingRecord record = fileProcessingRecordService.getFileProcessingRecord(recordId);
            if (record == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponseDTO1("FileProcessingRecord not found for ID: " + recordId))
                        .build();
            }

            return generateSuccessResponse(recordId, record, "Current status: " + record.getStatus());
        } catch (Exception e) {
            LOG.error("Unexpected error while retrieving status: ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponseDTO1("Unexpected error: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/parse/json/{fileProcessingRecordId}")
    @Transactional
    public Response parseJson(@PathParam("fileProcessingRecordId") Long recordId) {
        try {
            // Retrieve the record
            FileProcessingRecord record = fileProcessingRecordService.getFileProcessingRecord(recordId);
            if (record == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponseDTO1("FileProcessingRecord not found for ID: " + recordId))
                        .build();
            }

            // check if the file is still on uploaded
            if (record.getStatus() == ProcessingStatus.UPLOADING) {
                return generateSuccessResponse(recordId, record, "Uploading is not completed. After that u need to start to generate json.");
            }

            // check if the file is still on uploaded
            if (record.getStatus() == ProcessingStatus.UPLOADED) {
                return generateSuccessResponse(recordId, record, "Pls first start to generate json.");
            }

            // Check if the file is already processed
            if (record.getStatus() == ProcessingStatus.STORED) {
                return generateSuccessResponse(recordId, record, "Blog is already stored in db. Pls check this url to see the content: http://localhost:8080/blog-preview/" + record.getBlog().getId());
            }

            // Check if the file is already processed
            if (record.getStatus() == ProcessingStatus.PROCESSING) {
                return generateSuccessResponse(recordId, record, "Processing not completed. Need to wait until json file is created. so pls wait.");
            }

            // Check if the file is already processed
            if (record.getStatus() == ProcessingStatus.FAILED) {
                return generateSuccessResponse(recordId, record, "Something went wrong with this file.");
            }

            // Ensure JSON file exists
            File jsonFile = new File(record.getJsonFilePath());
            if (!jsonFile.exists() || !jsonFile.isFile()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponseDTO1("JSON file not found: " + jsonFile.getAbsolutePath()))
                        .build();
            }

            // Call service to parse and save the blog
            Blog blog = jsonProcessingService.parseAndSaveBlog(record);
            return Response.ok(new ResponseDTO1(true, "Blog successfully stored with ID: " + blog.getId())).build();
        } catch (Exception e) {
            LOG.error("Unexpected error while parsing JSON: ", e);
            fileProcessingRecordService.updateStatusForRecord(recordId, ProcessingStatus.FAILED);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponseDTO1("Unexpected error: " + e.getMessage()))
                    .build();
        }
    }

    private Response generateSuccessResponse(Long recordId, FileProcessingRecord record, String msg) {
        return Response.ok(new ResponseDTO1(true, new FileUploadResponseDTO(
                recordId,
                record.getFilePath(),
                record.getJsonFilePath(),
                record.getStatus(),
                msg
        ))).build();
    }
}
