package org.acme;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import io.quarkiverse.docling.runtime.client.model.ConvertDocumentResponse;
import io.quarkiverse.docling.runtime.client.model.OutputFormat;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.ai.MissionDto;
import org.acme.ai.MissionExtractor;
import org.acme.rag.Docling;
import org.acme.rag.MissionIngestor;
import org.acme.rag.PdfSignature;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Map;

@ApplicationScoped
public class MissionLoader {

    @Inject
    Docling docling;

    @Inject
    MissionExtractor missionExtractor;

    @Inject
    MissionIngestor missionIngestor;

    @Inject
    PdfSignature pdfSignature;

    @ConfigProperty(name = "rag.verify", defaultValue = "false")
    boolean verify;

    @Inject
    Logger logger;

    public Mission ingestDocument(byte[] document, String filename) {

        if (verify) {
            try {
                boolean verified = pdfSignature
                        .verifySignature(new ByteArrayInputStream(document));

                if (!verified) {
                    logger.errorf("%s filename has invalid signature", filename);
                    return null;
                }

            } catch (IOException | GeneralSecurityException e) {
                logger.errorf("%s filename has invalid signature", filename);
                logger.error(e);
                return null;
            }
        }

        ConvertDocumentResponse convertDocumentResponse = docling.convertFromBytes(document, filename, OutputFormat.TEXT);
        String textContent = convertDocumentResponse.getDocument().getTextContent();

        MissionDto missionDto = missionExtractor.extract(textContent);

        Mission m = new Mission();
        m.agentCodeName = missionDto.agentCodeName();
        m.contactPerson = missionDto.contactPerson();
        m.email = missionDto.email();
        m.missionBriefing = missionDto.missionBriefing();
        m.instructions = missionDto.instructions();
        m.location = missionDto.location();
        m.objective = missionDto.objective();
        m.phone = missionDto.phoneNumber();
        m.fullReport = textContent;

        m.dateTime = LocalDateTime.parse(missionDto.dateTime());

        QuarkusTransaction.begin();
        m.persist();
        QuarkusTransaction.commit();

        Metadata metadata = Metadata.from(Map.of("id", m.id));
        missionIngestor.ingest(Document.document(textContent, metadata));

        return m;

    }

}
