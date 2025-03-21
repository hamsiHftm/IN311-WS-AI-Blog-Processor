package ch.hftm.xml.ws.ai.blog.processor.service.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FileUploadingProducer {

   /* private static final Logger LOG = Logger.getLogger(FileUploadingProducer.class);

    @Inject
    @Channel("file-uploading-requests") // Outgoing topic
    Emitter<Long> fileUploadingEmitter;

    public void sendFileUploadRequest(Long recordId) {
        try {
            fileUploadingEmitter.send(recordId);
            LOG.info("Sent file uploading request to Kafka for record ID: " + recordId);
        } catch (Exception e) {
            LOG.error("Failed to send uploading request for record ID " + recordId, e);
        }
    }*/
}
