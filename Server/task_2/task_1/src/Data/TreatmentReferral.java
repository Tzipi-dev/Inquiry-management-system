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

//    public void closeInquiry() {
//        inquiry.setStatus(Inquiry.Status.History);
//        File inquiryForHistory = new File("History/" + inquiry.getCode() + ".csv");
//        File parentDir = inquiryForHistory.getParentFile();
//        if (!parentDir.exists()) {
//            parentDir.mkdirs();
//
//        }
//        try (FileWriter writer = new FileWriter(inquiryForHistory)) {
//            writer.write(inquiry.getCode());
//            writer.write(inquiry.getDescription());
//            writer.write(inquiry.getCreationDate().toString());
//            writer.write(inquiry.getClassName());
//            writer.write(inquiry.getStatus().toString());
//            System.out.println("קובץ CSV עבור סגירת פניה נוצר בהצלחה: " + inquiryForHistory.getAbsolutePath());
//
//            System.out.printf("שם נציג שהתפנה עקב סגירת פניה:" + getNameOfRepresentative());
//            deleteInquiryFile(inquiry.getCode());
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
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

        // כאן הקריאה למחיקת הקובץ מכל התקיות האחרות
        deleteInquiryFile(inquiry.className+inquiry.getCode());

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    public void deleteInquiryFile(String inquiryCode) {
        String[] folders = {"Complaint/", "Question/", "Request/"};
        for (String folder : folders) {
            File file = new File(folder + inquiryCode + ".csv");
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("הקובץ נמחק מהתקיה: " + folder);
                } else {
                    System.out.println("לא הצלחנו למחוק את הקובץ מהתקיה: " + folder);
                }
            }
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
    public void handlingInquiry(TreatmentReferral inquiryForHandling) {
        // שלב 1: טיפול בפנייה
        inquiryForHandling.getInquiryForRepresentative().handling();

        // שלב 2: סגירת הפנייה ושמירה ב-History
        inquiryForHandling.closeInquiry();

        // שלב 3: החזרת הנציג לתור
        Manager.getInstance().getRepresentativeQueue().add(inquiryForHandling.representative);

        // שלב 4: הסרת הפנייה מתור המטופלות (אופציונלי - למנוע כפילות)
        Manager.getInstance().getInquiryHandalingQueue().remove(inquiryForHandling);
    }




}
