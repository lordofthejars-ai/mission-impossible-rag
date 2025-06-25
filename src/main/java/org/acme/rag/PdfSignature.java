package org.acme.rag;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;

@ApplicationScoped
public class PdfSignature {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public boolean verifySignature(InputStream pdf) throws IOException, GeneralSecurityException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdf));
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);

        PdfPKCS7 pkcs7 = signUtil.readSignatureData("sig");
        boolean isVerified = pkcs7.verifySignatureIntegrityAndAuthenticity();
        pdfDoc.close();

        return isVerified;
    }
}
