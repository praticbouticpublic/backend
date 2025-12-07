package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.services.SessionService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static javax.swing.text.StyleConstants.setBold;

@RestController
@RequestMapping("/api")
public class PDFQRCodeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.root.url.front}")
    private String rootUrlFront;
    
    @Value("${session.max.lifetime}")
    private int maxLifetime;

    /**
     * Endpoint pour générer un PDF avec des QR codes
     */
    @GetMapping(value = "/pdfqrcode")
    public ResponseEntity<?> generateQRCodePDF(
            @RequestParam("bouticid") int bouticId,
            @RequestParam("methv") int methV,
            @RequestParam("nbtable") int nbTable,
            @RequestParam("nbex") int nbEx) {
        
        try {

            // Récupérer le nom du boutic
            String boutic = getBouticName(bouticId);
            if (boutic == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error","Boutic non trouvée"));
            }
            
            // Générer le PDF
            byte[] pdfBytes = generatePDFWithQRCodes(boutic, methV, nbTable, nbEx);
            
            // Configurer les en-têtes de la réponse
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "qrcodes.pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }
    
    /**
     * Récupère le nom du boutic par son ID
     */
    private String getBouticName(int bouticId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT customer FROM customer WHERE customid = ?", 
                String.class, 
                bouticId
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Génère le PDF avec les QR codes
     */
    private byte[] generatePDFWithQRCodes(String boutic, int methV, int nbTable, int nbEx) throws IOException, WriterException {
        // Si methV est 3, on force nbTable à 1
        if (methV == 3) {
            nbTable = 1;
        }
        
        // Initialiser le stream de sortie pour le PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Créer le document PDF
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        int num = 0;
        int noTable = 0;
        
        // Calculer le nombre total de QR codes à générer
        int totalQRCodes = nbEx * nbTable;
        
        while (num < totalQRCodes) {
            // Créer une table 4x5 pour disposer les QR codes sur la page
            Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
            
            for (int j = 0; j < 5 && num < totalQRCodes; j++) {
                for (int i = 0; i < 4 && num < totalQRCodes; i++) {
                    num++;
                    noTable++;
                    
                    if (noTable > nbTable) {
                        noTable = 1;
                    }
                    
                    // Créer une cellule pour chaque QR code
                    Cell cell = new Cell();
                    cell.setPadding(10);
                    
                    // Ajouter le nom du boutic
                    Paragraph bouticName = new Paragraph(boutic);
                    bouticName.setTextAlignment(TextAlignment.CENTER);
                    cell.add(bouticName);
                    
                    // Ajouter le texte selon methV
                    String title;
                    String qrCodeContent;
                    
                    if (methV == 2) {
                        title = "Table " + noTable;
                        qrCodeContent = rootUrlFront + boutic + "/2/" + num;
                    } else { // methV == 3
                        title = "Click n Collect";
                        qrCodeContent = rootUrlFront + boutic;
                    }
                    
                    Paragraph titleParagraph = new Paragraph(title);
                    titleParagraph.setTextAlignment(TextAlignment.CENTER);
                    cell.add(titleParagraph);
                    
                    // Générer le QR code
                    byte[] qrCodeImage = generateQRCodeImage(qrCodeContent, 95, 95);
                    ImageData imageData = ImageDataFactory.create(qrCodeImage);
                    Image qrCodePdfImage = new Image(imageData);
                    //qrCodePdfImage.setAutoScale(true);
                    cell.add(qrCodePdfImage);
                    
                    table.addCell(cell);
                }
            }
            
            document.add(table);
            
            // Ajouter une nouvelle page si nécessaire
            /*if (num < totalQRCodes) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }*/
        }
        
        document.close();
        return outputStream.toByteArray();
    }
    
    /**
     * Génère l'image QR code à partir d'un contenu
     */
    private byte[] generateQRCodeImage(String content, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = bitMatrix.getWidth();
        int matrixHeight = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}