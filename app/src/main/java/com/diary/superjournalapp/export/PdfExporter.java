package com.diary.superjournalapp.export;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import com.diary.superjournalapp.dto.BulletEntryDetails;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.JournalCategories.BulletJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.DreamJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.GratitudeJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.ReflectiveJournalEntity;
import com.diary.superjournalapp.entity.Tag;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * PDF export implementation using iText7
 * NOTE: Image embedding not implemented as app doesn't support images in text editor
 */
public class PdfExporter {
    
    private static final String TAG = "PdfExporter";
    private Context context;
    
    public PdfExporter(Context context) {
        this.context = context;
    }
    
    /**
     * Export a journal to PDF format
     * 
     * @param journal The journal metadata
     * @param content The journal content (entity)
     * @param tags Associated tags
     * @param options Export options
     * @return PDF data as byte array
     */
    public byte[] exportToPdf(Journal journal, Object content, 
                             List<Tag> tags, ExportOptions options) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Set up fonts
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont italicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
            
            // Add header
            addHeader(document, journal, boldFont, regularFont);
            
            // Add tags if requested
            if (options.isIncludeTags() && tags != null && !tags.isEmpty()) {
                addTags(document, tags, regularFont);
            }
            
            // Add divider
            document.add(new Paragraph("\n"));
            addDivider(document);
            document.add(new Paragraph("\n"));
            
            // Add content based on journal type
            String journalContent = extractContent(content);
            if (journalContent != null && !journalContent.isEmpty()) {
                addFormattedContent(document, journalContent, regularFont, boldFont, italicFont);
            }
            
            // Add metadata if requested
            if (options.isIncludeMetadata()) {
                document.add(new Paragraph("\n"));
                addDivider(document);
                document.add(new Paragraph("\n"));
                addMetadata(document, journal, journalContent, regularFont, italicFont);
            }
            
            // Add footer (app branding)
            addFooter(document, regularFont);
            
            document.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to create PDF", e);
            return null;
        }
    }
    
    /**
     * Add header section to PDF
     */
    private void addHeader(Document document, Journal journal, 
                          PdfFont boldFont, PdfFont regularFont) throws Exception {
        // App name
        Paragraph appName = new Paragraph("DiaryVerse")
            .setFont(boldFont)
            .setFontSize(18)
            .setFontColor(new DeviceRgb(63, 81, 181)) // Material Blue
            .setTextAlignment(TextAlignment.CENTER);
        document.add(appName);
        
        // Journal title
        Paragraph title = new Paragraph(journal.getTitle())
            .setFont(boldFont)
            .setFontSize(22)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(10);
        document.add(title);
        
        // Category and date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", 
                                                          Locale.getDefault());
        String dateStr = dateFormat.format(journal.getJournalCreatedOn());
        
        Paragraph metadata = new Paragraph(journal.getJournalCategory() + " • " + dateStr)
            .setFont(regularFont)
            .setFontSize(12)
            .setFontColor(ColorConstants.GRAY)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(metadata);
    }
    
    /**
     * Add tags section to PDF
     */
    private void addTags(Document document, List<Tag> tags, PdfFont font) throws Exception {
        Paragraph tagsParagraph = new Paragraph()
            .setFont(font)
            .setFontSize(11)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(5);
        
        tagsParagraph.add("Tags: ");
        
        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            String tagText = "#" + tag.getName() + " ";
            
            Text tagElement = new Text(tagText)
                .setFont(font)
                .setFontSize(10)
                .setFontColor(new DeviceRgb(33, 150, 243)) // Blue
                .setBackgroundColor(new DeviceRgb(227, 242, 253)); // Light blue
            
            tagsParagraph.add(tagElement);
        }
        
        document.add(tagsParagraph);
    }
    
    /**
     * Add divider line to PDF
     */
    private void addDivider(Document document) {
        Table divider = new Table(1);
        divider.setWidth(UnitValue.createPercentValue(100));
        Cell cell = new Cell()
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.LIGHT_GRAY, 1));
        divider.addCell(cell);
        document.add(divider);
    }
    
    /**
     * Extract content string from journal entity
     */
    private String extractContent(Object content) {
        if (content instanceof ReflectiveJournalEntity) {
            return ((ReflectiveJournalEntity) content).getJournalContent();
        } else if (content instanceof GratitudeJournalEntity) {
            return ((GratitudeJournalEntity) content).getJournalContent();
        } else if (content instanceof DreamJournalEntity) {
            return ((DreamJournalEntity) content).getJournalContent();
        } else if (content instanceof BulletJournalEntity) {
            return formatBulletJournalContent((BulletJournalEntity) content);
        }
        return null;
    }
    
    /**
     * Format bullet journal tasks into readable text
     */
    private String formatBulletJournalContent(BulletJournalEntity bulletJournal) {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<BulletEntryDetails>>() {}.getType();
            ArrayList<BulletEntryDetails> tasks = gson.fromJson(
                bulletJournal.getTaskListJson(), listType);
            
            if (tasks == null || tasks.isEmpty()) {
                return "No tasks in this bullet journal.";
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Tasks:\n\n");
            
            for (BulletEntryDetails task : tasks) {
                String checkbox = task.getIsTaskDone() ? "☑" : "☐";
                sb.append(checkbox)
                  .append(" ")
                  .append(task.getTaskName())
                  .append("\n");
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse bullet journal", e);
            return "Error parsing tasks.";
        }
    }
    
    /**
     * Add formatted content to PDF (handles HTML)
     */
    private void addFormattedContent(Document document, String htmlContent,
                                    PdfFont regularFont, PdfFont boldFont, 
                                    PdfFont italicFont) {
        // Strip HTML tags and convert to plain text
        // Note: For full HTML rendering, use a more sophisticated HTML parser
        String plainText = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY).toString();
        
        Paragraph content = new Paragraph(plainText)
            .setFont(regularFont)
            .setFontSize(12)
            .setTextAlignment(TextAlignment.JUSTIFIED)
            .setFixedLeading(18); // Line spacing
        
        document.add(content);
    }
    
    /**
     * Add metadata section to PDF
     */
    private void addMetadata(Document document, Journal journal, String content,
                            PdfFont regularFont, PdfFont italicFont) {
        Paragraph metadataTitle = new Paragraph("Metadata")
            .setFont(regularFont)
            .setFontSize(12)
            .setBold();
        document.add(metadataTitle);
        
        // Journal ID
        Paragraph journalId = new Paragraph("Journal ID: " + journal.getJournalId())
            .setFont(italicFont)
            .setFontSize(10)
            .setFontColor(ColorConstants.DARK_GRAY);
        document.add(journalId);
        
        // Word count (approximate)
        int wordCount = 0;
        if (content != null) {
            wordCount = content.split("\\s+").length;
        }
        Paragraph words = new Paragraph("Word Count: " + wordCount)
            .setFont(italicFont)
            .setFontSize(10)
            .setFontColor(ColorConstants.DARK_GRAY);
        document.add(words);
        
        // Export date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", 
                                                          Locale.getDefault());
        String exportDate = dateFormat.format(new java.util.Date());
        Paragraph exported = new Paragraph("Exported: " + exportDate)
            .setFont(italicFont)
            .setFontSize(10)
            .setFontColor(ColorConstants.DARK_GRAY);
        document.add(exported);
    }
    
    /**
     * Add footer with app branding
     */
    private void addFooter(Document document, PdfFont font) {
        document.add(new Paragraph("\n\n"));
        
        Paragraph footer = new Paragraph("Generated by DiaryVerse")
            .setFont(font)
            .setFontSize(9)
            .setFontColor(ColorConstants.GRAY)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(footer);
        
        Paragraph link = new Paragraph("https://play.google.com/store/apps/details?id=com.diary.superjournalapp")
            .setFont(font)
            .setFontSize(8)
            .setFontColor(new DeviceRgb(33, 150, 243))
            .setTextAlignment(TextAlignment.CENTER);
        document.add(link);
    }
}
