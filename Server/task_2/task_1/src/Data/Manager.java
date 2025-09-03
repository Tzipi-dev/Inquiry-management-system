package Data;
import Exceptions.InquiryRunTimeException;
import HandleStoreFiles.HandleFiles;

import java.io.File;
import java.io.FileWriter;
import java.util.Queue;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;

import static HandleStoreFiles.HandleFiles.readFile;
import static business.InquiryManager.parseFromFile;
public class Manager {
    private static Queue<Inquiry> InquiryQueue;///פניות שלא טפלו
    private static Queue<Representative> RepresentativeQueue;///כל הנציגים
    private static Queue<TreatmentReferral> InquiryHandlingQueue;///פניות בטיפול
    private static Queue<Inquiry> AllInquiriesQueue;/// כל הפניות במערכת
    private static Manager instance;
    static HandleFiles handleFiles = new HandleFiles();
    static {
        InquiryQueue = new LinkedList<>();
        RepresentativeQueue = new LinkedList<>();
        InquiryHandlingQueue = new LinkedList<>();
        AllInquiriesQueue = new LinkedList<>();
        System.out.println("in manager static block");
        loadRepresentatives();
        loadAllInquiries();
        Thread connectThread = new Thread(() -> {
            while (true) {
                try {
                    connectInquiryToRepresentative();
                    Thread.sleep(2000); // השהייה בין בדיקות כדי לא להעמיס את המעבד
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        connectThread.setDaemon(true); // חשוב: שה-thread לא ימנע סגירת התוכנית אם הראשי נסגר
        connectThread.start();
    }
    private Manager(){
    }
    public static Manager getInstance(){
        if(instance==null)
            instance=new Manager();
        return instance;
    }
    public Queue<Inquiry> getInquiryQueue() {
        return InquiryQueue;
    }
    public Queue<Representative> getRepresentativeQueue() {
        return RepresentativeQueue;
    }
    public Queue<TreatmentReferral> getInquiryHandalingQueue() {
        return InquiryHandlingQueue;
    }
    public Queue<Inquiry> getAllInquiriesQueue() {
        return AllInquiriesQueue;
    }
    public void setAllInquiriesQueue(Queue<Inquiry> allInquiriesQueue) {
        this.AllInquiriesQueue = allInquiriesQueue;
    }
    public void setRepresentativeQueue(Queue<Representative> representativeQueue) {
        RepresentativeQueue = representativeQueue;
    }
    public void setInquiryQueue(Queue<Inquiry> inquiryQueue) {
        InquiryQueue = inquiryQueue;
    }
    public static void connectInquiryToRepresentative() {
        System.out.println("dini");
        while (!RepresentativeQueue.isEmpty()) {
            if (InquiryQueue.peek() != null) {
                Inquiry inquiry = InquiryQueue.poll();
                Representative representative = RepresentativeQueue.poll();
                TreatmentReferral newTreatmentReferral = new TreatmentReferral(inquiry, representative);
                InquiryHandlingQueue.add(newTreatmentReferral);
                File treatmentReferral = new File("TreatmentReferral/" + inquiry.getCode() + ".csv");
                File parentDir = treatmentReferral.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                try (FileWriter writer = new FileWriter(treatmentReferral)) {
                    writer.write("פרטי הנציג:");
                    writer.write(representative.getName());
                    writer.write(representative.getTz());
                    writer.write("פרטי הפניה:");
                    writer.write(inquiry.getCode());
                    writer.write(inquiry.getDescription());
                    writer.write(inquiry.getCreationDate().toString());
                    writer.write(inquiry.getClassName());
                    writer.write(inquiry.getStatus().toString());
                    System.out.println("קובץ CSV עבור קישור פניה לנציג נוצר בהצלחה: " + treatmentReferral.getAbsolutePath());
                    inquiry.setStatus(Inquiry.Status.Treated);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                newTreatmentReferral.handlingInquiry(newTreatmentReferral);
            }
        }
    }
    public void deleteRepresentative(int id) {
        if (RepresentativeQueue == null || RepresentativeQueue.isEmpty()) {
            System.out.println("RepresentativeQueue is null or empty. No representative to delete.");
            return;
        }
        Representative foundRepresentative = null;
        int initialSize = RepresentativeQueue.size();
        for (int i = 0; i < initialSize; i++) {
            Representative current = RepresentativeQueue.poll();
            if (current != null && current.getTz() == id) {
                foundRepresentative = current;
            } else {
                RepresentativeQueue.add(current);
            }
        }
        if (foundRepresentative == null) {
            System.out.println("Representative with ID " + id + " not found in the queue.");
            return;
        }
        String name = foundRepresentative.getName();
        String path = "Representative";
        String filePath = path + File.separator + name + ".csv";
        File fileToDelete = new File(filePath);
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                System.out.println("File for representative " + id + " (" + filePath + ") deleted successfully.");
            } else {
                System.err.println("Error deleting file for representative " + id + " (" + filePath + "). " +
                        "Possible reasons: file is open, no permissions, or other system error.");
            }
        } else {
            System.out.println("File for representative " + id + " (" + filePath + ") not found at specified path.");
        }
    }
    public  static void getAllTreatmentReferral() {
        String director = "TreatmentReferral";
        Path dir = Paths.get("TreatmentReferral");
        if (Files.exists(dir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    File file = entry.toFile();
                    List<String> properties = readFile(file);
                    Inquiry inquiry = (Inquiry) parseFromFile(properties);
                    if (inquiry != null) {
                        AllInquiriesQueue.add(inquiry);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void loadRepresentatives() {
        String path = "Representative";
        Path dir = Paths.get(path);
        if (Files.exists(dir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    File file = entry.toFile();
                    List<String> properties = readFile(file);
                    Representative representative = (Representative) parseFromFile(properties);
                    if (representative != null) {
                        RepresentativeQueue.add(representative);
                    }
                    System.out.println("Loaded Representative file: " + entry.getFileName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file = new File("nextCodeVal-representatives.txt");
        if(file.exists()){
            Representative.setCounter(handleFiles.readNextCodeVal(file));
            System.out.println("representative - code :"+Representative.getCounter());}
    }
    public static void loadAllInquiries() {
        String[] inquiryDirs = { "Complaint", "Question", "Request" ,"History"};
        for (String path : inquiryDirs) {
            Path dir = Paths.get(path);
            if (Files.exists(dir)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                    for (Path entry : stream) {
                        File file = entry.toFile();
                        List<String> properties = readFile(file);
                        Inquiry inquiry = (Inquiry) parseFromFile(properties);
                        if (inquiry != null) {
                            AllInquiriesQueue.add(inquiry);
                            InquiryQueue.add(inquiry);

                        }
                        System.out.println("Loaded Inquiry file: " + entry.getFileName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File file = new File("nextCodeVal.txt");
        if(file.exists()){
            Inquiry.setNextCodeVal(handleFiles.readNextCodeVal(file));
            System.out.println("inquiry - code :"+Inquiry.getNextCodeVal());
        }

    }
    public void addRepresentative( Representative representative){
        connectInquiryToRepresentative();
        File dir = new File("Representative");
        dir.mkdir();
        representative.setRepresentative_code(Representative.getCounter());
//        handleFiles.saveNextCodeVal(representative.getRepresentative_code(),"nextCodeVal-representatives.txt");
        boolean success = handleFiles.saveCSV(representative, dir.getPath() + "\\" + representative.getName());
        if (success) {
            System.out.println("Representative saved successfully.");
        } else {
            System.out.println("Failed to save representative.");
        }
        RepresentativeQueue.add(representative);
        System.out.println("Representative added successfully!!!.");
    }
    public void addInquiry(Inquiry inquiry) {
        loadAllInquiries();
        inquiry.setCode(Inquiry.getNextCodeVal());
        handleFiles.saveFile(inquiry);
        handleFiles.saveNextCodeVal(inquiry.getCode(),"nextCodeVal.txt");
        boolean add1 = AllInquiriesQueue.add(inquiry);
        boolean add2 = InquiryQueue.add(inquiry);
        System.out.println("added : " + add1);
    }





}
