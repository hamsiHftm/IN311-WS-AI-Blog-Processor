package ch.hftm.xml.ws.ai.blog.processor.service.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FileProcessingProducer {

    private static final Logger LOG = Logger.getLogger(FileProcessingProducer.class);

    @Inject
    @Channel("file-processing-requests")
    Emitter<Long> fileProcessingEmitter;

    public void sendProcessingRequest(Long recordId) {
        try {
            fileProcessingEmitter.send(recordId);
            LOG.info("Sent processing request to Kafka for record ID: " + recordId);
        } catch (Exception e) {
            LOG.error("Failed to send processing request for record ID " + recordId, e);
        }
    }
}
