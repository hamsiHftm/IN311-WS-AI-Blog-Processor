package ch.hftm.xml.ws.ai.blog.processor.service.model;

import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import ch.hftm.xml.ws.ai.blog.processor.entity.FileType;
import ch.hftm.xml.ws.ai.blog.processor.entity.ProcessingStatus;
import ch.hftm.xml.ws.ai.blog.processor.repository.FileProcessingRecordRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FileProcessingRecordService {

    private static final Logger LOG = Logger.getLogger(FileProcessingRecordService.class);

    @Inject
    FileProcessingRecordRepository fileProcessingRecordRepository;

    @Transactional
    public FileProcessingRecord uploadFile(String htmlFilePath, FileType fileType, ProcessingStatus status) {
        FileProcessingRecord record = new FileProcessingRecord();
        record.setFilePath(htmlFilePath);
        record.setFileType(fileType);
        record.setStatus(status);

        fileProcessingRecordRepository.persist(record);
        LOG.info("Stored file record: " + htmlFilePath);
        return record;
    }

    public FileProcessingRecord getFileProcessingRecord(Long id) {
        return fileProcessingRecordRepository.findById(id);
    }

    @Transactional
    public void updateFileProcessingRecord(FileProcessingRecord record) {
        FileProcessingRecord existingRecord = fileProcessingRecordRepository.findById(record.getId());

        if (existingRecord != null) {
            existingRecord.setStatus(record.getStatus());
            existingRecord.setJsonFilePath(record.getJsonFilePath());
            existingRecord.setFileType(record.getFileType());
            existingRecord.setOpenAIFileId(record.getOpenAIFileId());

            fileProcessingRecordRepository.persistAndFlush(existingRecord);
        } else {
            throw new IllegalStateException("FileProcessingRecord not found for ID: " + record.getId());
        }
    }

    @Transactional
    public void updateStatusForRecord(Long id, ProcessingStatus status) {
        FileProcessingRecord record = fileProcessingRecordRepository.findById(id);

        if (record != null) {
            record.setStatus(status);
            fileProcessingRecordRepository.persistAndFlush(record);
        } else {
            throw new IllegalStateException("FileProcessingRecord not found for ID: " + id);
        }
    }
}
