package ch.hftm.xml.ws.ai.blog.processor.service;

import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import ch.hftm.xml.ws.ai.blog.processor.entity.ProcessingStatus;
import ch.hftm.xml.ws.ai.blog.processor.service.ai.HTMLParsingAIService;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import org.eclipse.microprofile.reactive.messaging.Incoming;


@ApplicationScoped
public class FileProcessingWorker {

    private static final Logger LOG = Logger.getLogger(FileProcessingWorker.class);

    @Inject
    FileProcessingRecordService fileProcessingRecordService;

    @Inject
    FileService fileService;

    @Inject
    HTMLParsingAIService htmlParsingAIService;

    @Incoming("file-processing-worker")
    @Transactional
    @Blocking
    @ActivateRequestContext
    public void processFile(Long recordId) {
        try {
            FileProcessingRecord record = fileProcessingRecordService.getFileProcessingRecord(recordId);
            if (record == null) {
                LOG.error("FileProcessingRecord not found: " + recordId);
                return;
            }

            String htmlContent = fileService.readHtmlFile(record);
            // String jsonContent = htmlParsingAIService.parseHTMLToJson(htmlContent);
            String jsonContent = """
                    ```json
                    {
                      "title": "Internationales Luftrecht",
                      "content": [
                        {
                          "type": "HEADING",
                          "level": 2,
                          "value": "Internationales Luftrecht",
                          "orderIndex": 1
                        },
                        {
                          "type": "HEADING",
                          "level": 3,
                          "value": "Einleitung",
                          "orderIndex": 2
                        },
                        {
                          "type": "HEADING",
                          "level": 4,
                          "value": "Der Luftraum und die Lufthoheit",
                          "orderIndex": 3
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Nach einem allgemeinen völkerrechtlichen Grundsatz ist der Luftraum frei. Doch bereits zu Beginn des 20. Jahrhunderts wurde von den Juristen die Einschränkung gemacht, dass bei der Benützung des Luftraumes auf die Sicherheitsbedürfnisse der angrenzenden Staaten Rücksicht zu nehmen sei. Damit sollte der Luftraum dem Verkehr ebenso frei zugänglich sein, wie die Hohe See. Doch verschiedene Staaten wollten den Luftraum über ihrem Staatsgebiet ganz oder teilweise für fremde Luftfahrzeuge sperren. Um über ihrem Gebiet die Lufthoheit zu wahren, setzten sie rigoros Waffengewalt ein. Leider gibt es auch heute noch Staaten, die selbst harmlosen zivilen Luftfahrzeugen den Überflug verweigern. So wurde 1995 anlässlich des damaligen Gordon Bennet Race ein Ballon über Weissrussland ohne Vorwarnung abgeschossen und 1998 wurde der Weltrekordversuch des Ballonfahrers Bertrand Piccard durch eine Überflugsverweigerung von China gestoppt. Selbst internationale Ambulanzflüge der Schweizerischen Rettungsflugwacht werden leider immer noch tagtäglich auf Grund von luftraumrechtlichen Problemen behindert oder sogar verunmöglicht.",
                          "orderIndex": 4
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Die enorme Zunahme des internationalen Luftverkehrs machte den Abschluss von internationalen Abkommen unumgänglich. Dabei wurde von der Interessentheorie ausgegangen, d.h. jeder Staat kann über seinen Luftraum höhenmäs sig so weit verfügen, als sein Interesse reicht. Damit ist die vertikale Ausdehnung des Luftraumes zwar immer noch nicht definiert, doch steht wenigstens fest, dass zumindest für Satelliten keine Überflugsgenehmigungen eingeholt werden müssen.",
                          "orderIndex": 5
                        },
                        {
                          "type": "HEADING",
                          "level": 4,
                          "value": "Die internationale Normenhierarchie",
                          "orderIndex": 6
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Nicht alle luftrechtlichen Vorschriften haben die gleiche Bedeutung; sie stehen deshalb auch nicht alle auf der gleichen Hierarchiestufe. Grundsätzlich können, bezogen auf die Schweiz, folgende Stufen unterschieden werden, wobei jeweils die höhere der tieferen vorgeht:",
                          "orderIndex": 7
                        },
                        {
                          "type": "LIST",
                          "listType": "unordered",
                          "items": [
                            "Völkerrecht",
                            "Internationales Luftrecht",
                            "Regionales Luftrecht (im Sinne von supranationalem europäischem Luftrecht)",
                            "Nationale Quellen"
                          ],
                          "orderIndex": 8
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Die internationale Normenhierarchie hat eine grosse Bedeutung für das Luftrecht der Schweiz. Als beispielsweise der Anhang 1 zum Abkommen von Chicago mit den Vorschriften über Berechtigungen für das Luftfahrtpersonal (Personal Licensing) dahingehend geändert wurde, dass für den Erwerb der Privatpilotenlizenz (PPL) insgesamt 40 statt wie vorher nur 35 Flugstunden erforderlich sein sollen, musste auch die Schweiz diese Regelung übernehmen. Die Schweiz hat das Abkommen von Chicago nämlich ratifiziert und somit als verbindliche Regelung anerkannt. Hätte sie die Änderung nicht übernommen, wären die schweizerischen Privatpilotenausweise im Ausland sehr bald nicht mehr akzeptiert worden. Dasselbe hat sich später mit den europäischen Vorschriften wiederholt. Die Kandidaten für eine PPL(A)-Lizenz müssen zum Zeitpunkt der praktischen Prüfung jetzt mindestens 45 Flugstunden nachzuweisen. Ein neuer Standard schreibt nun vor, dass Piloten mit einer Light Aircraft Pilot Licence (LAPL), die eine PPL(A)-Privatpilotenlizenz erwerben möchten, 10 Stunden zusätzliche Flugausbildung absolvieren müssen. Wenn das Bundesamt für Zivilluftfahrt neue oder geänderte Vorschriften erlässt, handelt es sich folglich nicht um eine eigene Aktion, sondern meist um eine Reaktion auf den Druck des internationalen Luftfahrtrechts.",
                          "orderIndex": 9
                        },
                        {
                          "type": "HEADING",
                          "level": 4,
                          "value": "Die Luftfahrtbehörden im Überblick",
                          "orderIndex": 10
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Schon die Vielfalt der Rechtsquellen lässt vermuten, dass es auch verschiedene Behörden gibt, welche die Luftrechtsnormen vollziehen. Auch hier kann zwischen internationalen, regionalen (im Sinne von europäischen) und nationalen Behörden unterschieden werden.",
                          "orderIndex": 11
                        },
                        {
                          "type": "LIST",
                          "listType": "unordered",
                          "items": [
                            "Internationale Luftfahrtbehörden",
                            "Europäische Luftfahrtbehörden",
                            "Nationale Luftfahrtbehörden"
                          ],
                          "orderIndex": 12
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Von diesen Luftfahrtbehörden ist die ICAO mit Abstand am wichtigsten. Ihre Erlass sind für die über 170 Vertragsstaaten des Übereinkommens über die Zivilluftfahrt verbindlich. Es bleibt den einzelnen Vertragsstaaten überlassen, ob sie die ICAO-Vorschriften als direkt anwendbar erklären oder über nationale Erlass in das Landesrecht umsetzen wollen.",
                          "orderIndex": 13
                        }
                      ]
                    }
                    ```
                    """;
            String jsonFileName = fileService.saveJsonFile(jsonContent, record.getJsonFilePath(), recordId);

            record.setJsonFileName(jsonFileName);
            record.setStatus(ProcessingStatus.PROCESSED);
            fileProcessingRecordService.updateFileProcessingRecord(record);

            LOG.info("Processing completed for record ID: " + recordId);
        } catch (Exception e) {
            LOG.error("Error processing file ID " + recordId, e);
            fileProcessingRecordService.updateStatusForRecord(recordId, ProcessingStatus.FAILED);
        }
    }

}
