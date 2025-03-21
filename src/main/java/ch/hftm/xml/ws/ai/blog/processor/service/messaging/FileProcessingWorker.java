package ch.hftm.xml.ws.ai.blog.processor.service.messaging;

import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import ch.hftm.xml.ws.ai.blog.processor.entity.FileType;
import ch.hftm.xml.ws.ai.blog.processor.entity.ProcessingStatus;
import ch.hftm.xml.ws.ai.blog.processor.service.FileService;
import ch.hftm.xml.ws.ai.blog.processor.service.ai.HTMLParsingAIService;
import ch.hftm.xml.ws.ai.blog.processor.service.ai.PDFProcessingAIService;
import ch.hftm.xml.ws.ai.blog.processor.service.model.FileProcessingRecordService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.resteasy.reactive.common.NotImplementedYet;

import java.io.IOException;


@ApplicationScoped
public class FileProcessingWorker {

    private static final Logger LOG = Logger.getLogger(FileProcessingWorker.class);


    @Inject
    FileProcessingRecordService fileProcessingRecordService;

    @Inject
    FileService fileService;

    @Inject
    HTMLParsingAIService htmlParsingAIService;

    @Inject
    PDFProcessingAIService pdfProcessingAIService; // NEW

    @Incoming("file-processing-worker")
    @Transactional
    @ActivateRequestContext
    public Uni<Void> processFileAsync(Long recordId) {
        return Uni.createFrom().item(recordId)
                .onItem().transformToUni(id -> Uni.createFrom().voidItem().invoke(() -> processFile(id)))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    private void processFile(Long recordId) {
        try {
            FileProcessingRecord record = fileProcessingRecordService.getFileProcessingRecord(recordId);
            if (record == null) {
                LOG.error("FileProcessingRecord not found: " + recordId);
                return;
            }

            // Determine file type (HTML or PDF)
            if (record.getFileType() == FileType.HTML) {
                processHtmlFile(record);
            } else if (record.getFileType() == FileType.PDF) {
                processPdfFile(record);
            } else {
                LOG.error("Unsupported file type: " + record.getFileType());
                fileProcessingRecordService.updateStatusForRecord(recordId, ProcessingStatus.FAILED);
            }
        } catch (Exception e) {
            LOG.error("Error processing file ID " + recordId, e);
            fileProcessingRecordService.updateStatusForRecord(recordId, ProcessingStatus.FAILED);
        }
    }

    private void processHtmlFile(FileProcessingRecord record) {
        try {
            String htmlContent = fileService.readHtmlFile(record);
            String jsonContent = htmlParsingAIService.parseHTMLToJson(htmlContent);

            String jsonFilePath = fileService.saveJsonFile(jsonContent, record.getId());
            record.setJsonFilePath(jsonFilePath);
            record.setStatus(ProcessingStatus.PROCESSED);
            fileProcessingRecordService.updateFileProcessingRecord(record);

            LOG.info("Processing completed for record ID: " + record.getId());
        } catch (Exception e) {
            LOG.error("Error processing file ID " + record.getId(), e);
            fileProcessingRecordService.updateStatusForRecord(record.getId(), ProcessingStatus.FAILED);
        }
    }

    private void processPdfFile(FileProcessingRecord record) {
        try {
            throw new NotImplementedYet();
            /*
            String jsonContent = pdfProcessingAIService.extractContent(record.getOpenAIFileId());
            String jsonFilePath = fileService.saveJsonFile(jsonContent, record.getId());

            record.setJsonFilePath(jsonFilePath);
            record.setStatus(ProcessingStatus.PROCESSED);
            fileProcessingRecordService.updateFileProcessingRecord(record);

            LOG.info("PDF Processing completed for record ID: " + record.getId());
             */
        } catch (Exception e) {
            LOG.error("Error processing file ID " + record.getId(), e);
            fileProcessingRecordService.updateStatusForRecord(record.getId(), ProcessingStatus.FAILED);
        }
    }
}
