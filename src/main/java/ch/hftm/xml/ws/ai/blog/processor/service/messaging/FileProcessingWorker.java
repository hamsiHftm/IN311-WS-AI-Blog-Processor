package ch.hftm.xml.ws.ai.blog.processor.service.messaging;

import ch.hftm.xml.ws.ai.blog.processor.entity.FileProcessingRecord;
import ch.hftm.xml.ws.ai.blog.processor.entity.ProcessingStatus;
import ch.hftm.xml.ws.ai.blog.processor.service.FileService;
import ch.hftm.xml.ws.ai.blog.processor.service.ai.HTMLParsingAIService;
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
                          "value": "2 Internationales Luftrecht",
                          "orderIndex": 1
                        },
                        {
                          "type": "HEADING",
                          "level": 3,
                          "value": "2.1 Einleitung",
                          "orderIndex": 2
                        },
                        {
                          "type": "HEADING",
                          "level": 4,
                          "value": "2.1.1 Der Luftraum und die Lufthoheit",
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
                          "value": "2.1.2 Die internationale Normenhierarchie",
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
                            {
                              "value": "Völkerrecht",
                              "subItems": [
                                {
                                  "value": "Gesetze und Gebräuche des Völkerrechtes"
                                }
                              ]
                            },
                            {
                              "value": "Internationales Luftrecht",
                              "subItems": [
                                {
                                  "value": "multilaterale Abkommen (insbesondere das Übereinkommen über die internationale Zivilluftfahrt, das sogenannte Abkommen von Chicago, nachstehend mit CHI abgekürzt) mit entsprechenden Anhängen und Dokumenten"
                                },
                                {
                                  "value": "bilaterale Abkommen (z.B. der Staatsvertrag mit Frankreich betreffend den Flughafen Basel oder der Staatsvertrag mit Österreich betreffend den Flugplatz St.Gallen-Altenrhein)"
                                },
                                {
                                  "value": "Anhänge, Änderungsprotokolle und Dokumente zu internationalen Abkommen (so z.B. die 19 Anhänge zum Abkommen von Chicago)"
                                }
                              ]
                            },
                            {
                              "value": "Regionales Luftrecht (im Sinne von supranationalem europäischem Luftrecht)",
                              "subItems": [
                                {
                                  "value": "Verordnungen der Europäischen Union (bei Anträgen der EASA auch als EASA-Rules bezeichnet)"
                                },
                                {
                                  "value": "Richtlinien des Europäischen Rates"
                                }
                              ]
                            },
                            {
                              "value": "Nationale Quellen",
                              "subItems": [
                                {
                                  "value": "Bundesverfassung (insbesondere Art. 87)"
                                },
                                {
                                  "value": "Bundesgesetze (insbesondere das Luftfahrtgesetz)"
                                },
                                {
                                  "value": "Verordnungen des Bundesrates (insbesondere die Luftfahrtverordnung)"
                                },
                                {
                                  "value": "Weisungen und Richtlinien des Bundesamtes für Zivilluftfahrt (BAZL)"
                                }
                              ]
                            }
                          ],
                          "orderIndex": 8
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Die internationale Normenhierarchie hat eine grosse Bedeutung für das Luftrecht der Schweiz. Als beispielsweise der Anhang 1 zum Abkommen von Chicago mit den Vorschriften über Berechtigungen für das Luftfahrtpersonal (Personal Licensing) dahingehend geändert wurde, dass für den Erwerb der Privatpilotenlizenz (PPL) insgesamt 40 statt wie vorher nur 35 Flugstunden erforderlich sein sollen, musste auch die Schweiz diese Regelung übernehmen. Die Schweiz hat das Abkommen von Chicago nämlich ratifiziert und somit als verbindliche Regelung anerkannt. Hätte sie die Änderung nicht übernommen, wären die schweizerischen Privatpilotenausweise im Ausland sehr bald nicht mehr akzeptiert worden.",
                          "orderIndex": 9
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Dasselbe hat sich später mit den europäischen Vorschriften wiederholt. Die Kandidaten für eine PPL(A)-Lizenz müssen zum Zeitpunkt der praktischen Prüfung jetzt mindestens 45 Flugstunden nachzuweisen. Ein neuer Standard schreibt nun vor, dass Piloten mit einer Light Aicraft Pilot Licence (LAPL), die eine PPL(A)-Privatpilotenlizenz erwerben möchten, 10 Stunden zusätzliche Flugausbildung absolvieren müssen. Wenn das Bundesamt für Zivilluftfahrt neue oder geänderte Vorschriften erlässt, handelt es sich folglich nicht um eine eigene Aktion, sondern meist um eine Reaktion auf den Druck des internationalen Luftfahrtrechts.",
                          "orderIndex": 10
                        },
                        {
                          "type": "HEADING",
                          "level": 4,
                          "value": "2.1.3 Die Luftfahrtbehörden im Überblick",
                          "orderIndex": 11
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Schon die Vielfalt der Rechtsquellen lässt vermuten, dass es auch verschiedene Behörden gibt, welche die Luftrechtsnormen vollziehen. Auch hier kann zwischen internationalen, regionalen (im Sinne von europäischen) und nationalen Behörden unterschieden werden.",
                          "orderIndex": 12
                        },
                        {
                          "type": "LIST",
                          "listType": "unordered",
                          "items": [
                            {
                              "value": "Internationale Luftfahrtbehörden",
                              "subItems": [
                                {
                                  "value": "ICAO (International Civil Aviation Organisation / Internationale Zivilluftfahrt Organisation), eine Unterorganisation der UNO mit Sitz in Montreal / Canada"
                                }
                              ]
                            },
                            {
                              "value": "Europäische Luftfahrtbehörden",
                              "subItems": [
                                {
                                  "value": "EASA (Europäische Agentur für Flugsicherheit / European Aviation Safety Agency), unabhängige europäische Sicherheits- und Aufsichtsbehörde für die Zivilluftfahrt"
                                }
                              ]
                            },
                            {
                              "value": "Nationale Luftfahrtbehörden",
                              "subItems": [
                                {
                                  "value": "NAA (National Aviation Authority / Nationale Zivilluftfahrtbehörde), in der Schweiz das Bundesamt für Zivilluftfahrt (BAZL) mit Sitz in Bern"
                                }
                              ]
                            }
                          ],
                          "orderIndex": 13
                        },
                        {
                          "type": "PARAGRAPH",
                          "value": "Von diesen Luftfahrtbehörden ist die ICAO mit Abstand am wichtigsten. Ihre Erlasse sind für die über 170 Vertragsstaaten des Übereinkommens über die Zivilluftfahrt verbindlich. Es bleibt den einzelnen Vertragsstaaten überlassen, ob sie die ICAO-Vorschriften als direkt anwendbar erklären oder über nationale Erlass in das Landesrecht umsetzen wollen.",
                          "orderIndex": 14
                        }
                      ]
                    }
                    ```
                    """;
            String jsonFilePath = fileService.saveJsonFile(jsonContent, recordId);
            record.setJsonFilePath(jsonFilePath);
            record.setStatus(ProcessingStatus.PROCESSED);
            fileProcessingRecordService.updateFileProcessingRecord(record);

            LOG.info("Processing completed for record ID: " + recordId);
        } catch (Exception e) {
            LOG.error("Error processing file ID " + recordId, e);
            fileProcessingRecordService.updateStatusForRecord(recordId, ProcessingStatus.FAILED);
        }
    }
}
