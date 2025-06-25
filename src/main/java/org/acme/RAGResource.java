package org.acme;

import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.rag.PdfSignature;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;

@Path("/rag")
public class RAGResource {

    String FILE_TO_SIGN = "mission1";

    @Inject
    MissionLoader missionLoader;

    @Inject
    PdfSignature pdfSignature;

    @Inject
    Logger logger;

   @GET
   @Path("/verify")
   public String verifyPdf() throws GeneralSecurityException, IOException {
       InputStream src = RAGResource.class.getClassLoader().getResourceAsStream("/" + FILE_TO_SIGN + "-sign.pdf");
       pdfSignature.verifySignature(src);

       return "Verified";
   }

    @GET
    @Path("/sign")
    public String signPdf() throws GeneralSecurityException, IOException {

        InputStream src = RAGResource.class.getClassLoader().getResourceAsStream("/" + FILE_TO_SIGN + ".pdf");
        String dest = FILE_TO_SIGN + "-sign.pdf";
        InputStream keyStoreFile = RAGResource.class.getClassLoader().getResourceAsStream("/keystore.p12");

        char[] password = "ragrag".toCharArray();

        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(keyStoreFile, password);

        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, password);
        Certificate[] chain = ks.getCertificateChain(alias);

        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        SignerProperties signerProperties = new SignerProperties();
        signerProperties.setFieldName("sig");
        signer.setSignerProperties(signerProperties);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "BC");
        IExternalDigest digest = new BouncyCastleDigest();

        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        return "PDF signed successfully!";
    }

    @POST
    @Path("uploadItem")
    public Response uploadItem(@RestForm("mission") FileUpload file) throws IOException {

       logger.infof("%s file received.", file.fileName());

       Mission m = missionLoader.ingestDocument(
               Files.readAllBytes(file.uploadedFile()),
               file.fileName()
       );

       if (m == null) {
           return Response.serverError().build();
       }

       final Map<String, Object> returnObject = new HashMap<>();
       returnObject.put("id", m.id);
       JsonObject jsonObject = new JsonObject(returnObject);

       return Response.ok().entity(jsonObject).build();

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws IOException {

        InputStream resourceAsStream = RAGResource.class.getClassLoader().getResourceAsStream("/mission1.pdf");
        byte[] allBytes = resourceAsStream.readAllBytes();

        missionLoader.ingestDocument(allBytes, "mission1.pdf");
        return "Hello from Quarkus REST";
    }
}
