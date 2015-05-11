package yjfc.view;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import yjfc.db.CheckoutItemPOJO;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfExporter {
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    
    public static void export(LocalDate date, List<CheckoutItemPOJO> aList) {
        try {
            String name = "YJFC Armory Checkout " + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(name));
            document.open();
            addMetaData(document, date);
            addContent(document, date, aList);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private static void addMetaData(Document document, LocalDate date) {
        document.addTitle("YJFC Armory Checkout " + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        document.addSubject("YJFC Armory Checkout");
        document.addKeywords("YJFC, armory, checkout");
        document.addAuthor("Yellow Jacket Fencing");
        document.addCreator("Susanna Dong");
    }

    private static void addContent(Document document, LocalDate date, List<CheckoutItemPOJO> aList)
            throws DocumentException {
        Anchor anchor = new Anchor("Armory Checkout", catFont);
        anchor.setName("First Chapter");

        Chapter catPart = new Chapter(new Paragraph(anchor), 1);

        Paragraph subPara = new Paragraph("People Who Checked Out Today", subFont);
        Section subCatPart = catPart.addSection(subPara);
        createTable(subCatPart, aList);

        document.add(catPart);

    }

    private static void createTable(Section subCatPart, List<CheckoutItemPOJO> aList)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        PdfPCell c1 = null;

        c1 = new PdfPCell(new Phrase("Person"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        c1 = new PdfPCell(new Phrase("Type"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Num"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        String person = "";
        for(CheckoutItemPOJO item : aList) {
            if(!person.equals(item.getPerson())) {
                table.addCell(item.getPerson());
                person = item.getPerson();
            } else {
                table.addCell("");
            }
            table.addCell(item.getType());
            table.addCell(Integer.toString(item.getNum()));
        }

        subCatPart.add(table);

    }
}
