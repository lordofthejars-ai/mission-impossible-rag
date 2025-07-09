package org.acme.rag;

import io.quarkiverse.docling.runtime.client.DoclingService;
import io.quarkiverse.docling.runtime.client.api.DoclingApi;
import io.quarkiverse.docling.runtime.client.model.ConversionRequest;
import io.quarkiverse.docling.runtime.client.model.ConvertDocumentResponse;
import io.quarkiverse.docling.runtime.client.model.ConvertDocumentsOptions;
import io.quarkiverse.docling.runtime.client.model.FileSource;
import io.quarkiverse.docling.runtime.client.model.HttpSource;
import io.quarkiverse.docling.runtime.client.model.OutputFormat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.URI;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
public class Docling {

    @Inject
    DoclingService docling;

    public ConvertDocumentResponse convertFromBytes(byte[] document, String filename, OutputFormat outputFormat) {
        return docling.convertFromBytes(document, filename, outputFormat);
    }

    /**@Inject
    DoclingApi doclingApi;

    public ConvertDocumentResponse convertFromBytes(
            byte[] content, String filename, OutputFormat outputFormat) {
        String base64Document = Base64.getEncoder().encodeToString(content);

        return this.convertFromBase64ToText(base64Document, filename, outputFormat);
    }

    public ConvertDocumentResponse convertFromBase64ToText(
            String base64Content, String filename, OutputFormat outputFormat) {

        FileSource fileSource = new FileSource();
        fileSource.base64String(base64Content);
        fileSource.setFilename(filename);

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.addFileSourcesItem(fileSource);
        ConvertDocumentsOptions convertDocumentsOptions = new ConvertDocumentsOptions();
        convertDocumentsOptions.setToFormats(List.of(outputFormat));
        conversionRequest.options(convertDocumentsOptions);

        return doclingApi
                .processUrlV1alphaConvertSourcePost(conversionRequest);
    }
    **/
}
