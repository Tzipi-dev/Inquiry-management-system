package Data;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class TreatmentReferral {
    private Inquiry inquiry;
    private Representative representative;

    public TreatmentReferral(Inquiry inquiry, Representative representative) {
        this.inquiry = inquiry;
        this.representative = representative;
    }

    public String getNameOfRepresentative() {
        return this.representative.getName();
    }

    public Inquiry getInquiryForRepresentative() {
        return inquiry;
    }

    public void closeInquiry() {
        inquiry.setStatus(Inquiry.Status.History);
        File inquiryForHistory = new File("History/" + inquiry.getCode() + ".csv");
        File parentDir = inquiryForHistory.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (FileWriter writer = new FileWriter(inquiryForHistory)) {
            writer.write(inquiry.getCode());
            writer.write(inquiry.getDescription());
            writer.write(inquiry.getCreationDate().toString());
            writer.write(inquiry.getClassName());
            writer.write(inquiry.getStatus().toString());
            System.out.println("קובץ CSV עבור סגירת פניה נוצר בהצלחה: " + inquiryForHistory.getAbsolutePath());

            System.out.printf("שם נציג שהתפנה עקב סגירת פניה:" + getNameOfRepresentative());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void cancellationInquiry() {
        inquiry.setStatus(Inquiry.Status.Cancelled);
        File inquiryForHistory = new File("History/" + inquiry.getCode() + ".csv");
        File parentDir = inquiryForHistory.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (FileWriter writer = new FileWriter(inquiryForHistory)) {
            writer.write(inquiry.getCode());
            writer.write(inquiry.getDescription());
            writer.write(inquiry.getCreationDate().toString());
            writer.write(inquiry.getClassName());
            writer.write(inquiry.getStatus().toString());
            System.out.println("קובץ CSV עבור פניה שבוטלה נוצר בהצלחה: " + inquiryForHistory.getAbsolutePath());
            System.out.printf("שם נציג שהתפנה עקב ביטול פניה:" + getNameOfRepresentative());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void handlingInquiry(TreatmentReferral inquiryForHandling){
        inquiryForHandling.getInquiryForRepresentative().handling();
        inquiryForHandling.closeInquiry();
        //כשמפעילים את הפונקציה הזאת לדאוג שלפני ההפעלה תישלח הודעה לclient שהפניה בטיפול, וכשהפונקציה מסתיימת, יש להוסיף את הנציג חזרה לתור
        //זה לא יכול להתבצע כאן, כי אין פה את האפשרות לכתוב לclient, רק איפה שיש את הsocket, ואין גם את תור הנציגים...
    }
}
