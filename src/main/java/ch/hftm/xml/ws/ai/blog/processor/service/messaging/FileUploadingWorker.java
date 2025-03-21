package ch.hftm.xml.ws.ai.blog.processor.service.messaging;

import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import ch.hftm.xml.ws.ai.blog.processor.entity.ProcessingStatus;
import ch.hftm.xml.ws.ai.blog.processor.service.model.FileProcessingRecordService;
import ch.hftm.xml.ws.ai.blog.processor.service.openai.OpenAIFileService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.io.File;

@ApplicationScoped
public class FileUploadingWorker {

//    private static final Logger LOG = Logger.getLogger(FileUploadingWorker.class);
//
//    @Inject
//    FileProcessingRecordService fileProcessingRecordService;
//
//    @Inject
//    OpenAIFileService openAIFileService;
//
//    @Incoming("file-uploading-worker")
//    @Transactional
//    @ActivateRequestContext
//    public Uni<Void> processFileAsync(Long recordId) {
//        return Uni.createFrom().item(recordId)
//                .onItem().transformToUni(id -> Uni.createFrom().voidItem().invoke(() -> uploadFileToOpenAI(id)))
//                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
//    }
//
//    public void uploadFileToOpenAI(Long recordId) {
//        try {
//            LOG.info("Uploading file to OpenAI for record ID: " + recordId);
//
//            FileProcessingRecord record = fileProcessingRecordService.getFileProcessingRecord(recordId);
//            if (record == null) {
//                LOG.error("FileProcessingRecord not found for ID: " + recordId);
//                return;
//            }
//
//            File file = new File(record.getFilePath());
//            if (!file.exists() || !file.isFile()) {
//                LOG.error("File not found: " + file.getAbsolutePath());
//                return;
//            }
//
//            String fileId = openAIFileService.uploadFileToOpenAI(file);
//            record.setOpenAIFileId(fileId);
//            record.setStatus(ProcessingStatus.UPLOADED);
//
//            fileProcessingRecordService.updateFileProcessingRecord(record);
//
//            LOG.info("File successfully uploaded to OpenAI. File ID: " + fileId);
//
//        } catch (Throwable t) {
//            LOG.error("Failed to upload file to OpenAI: " + t.getMessage(), t);
//            fileProcessingRecordService.updateStatusForRecord(recordId, ProcessingStatus.FAILED);
//        }
//    }
}
