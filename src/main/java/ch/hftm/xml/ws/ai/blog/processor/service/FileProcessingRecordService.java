package ch.hftm.xml.ws.ai.blog.processor.service;

import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
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
    public FileProcessingRecord uploadFile(String htmlFilePath) {
        FileProcessingRecord record = new FileProcessingRecord();
        record.setHtmlFilePath(htmlFilePath);
        record.setStatus(ProcessingStatus.PENDING);

        fileProcessingRecordRepository.persist(record);
        LOG.info("Stored file record: " + htmlFilePath);
        return record;
    }

    public FileProcessingRecord getFileProcessingRecord(Long id) {
        return fileProcessingRecordRepository.findById(id);
    }

    @Transactional
    public void updateFileProcessingRecord(FileProcessingRecord record) {
        fileProcessingRecordRepository.persist(record);
    }
}
