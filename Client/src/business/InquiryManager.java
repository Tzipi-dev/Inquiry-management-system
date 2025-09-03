package business;

import Data.*;
import HandleStoreFiles.HandleFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static HandleStoreFiles.HandleFiles.readFile;

public class InquiryManager {
    static final Queue<InquiryHandling> QUIRIES_QUEUE = new LinkedList<>();
    static List<Representative> representatives = new ArrayList<Representative>();
    HandleFiles handleFiles = new HandleFiles();

    private static InquiryManager instance;

    private InquiryManager() {
    }

    public static InquiryManager getInstance() {
        if (instance == null)
            instance = new InquiryManager();
        return instance;
    }

    public void inquiryCreation() {
        System.out.println("enter your quiry type: for question - 1 , for request - 2,for complaint - 3 ,for exit - 0");
        Scanner scan = new Scanner(System.in);
        int num = scan.nextInt();
        while (num != 0) {
            Inquiry inquiry = createInquiry(num);
            if (inquiry != null) {
                handleFiles.saveFile(inquiry);
                handleFiles.saveNextCodeVal(inquiry.getCode(),"nextCodeVal");
            }
            System.out.println("enter your quiry type: for question - 1 , for request - 2,for complaint - 3 ,for exit - 0");
            num = scan.nextInt();
        }
    }

    public Inquiry createInquiry(int num) {
        Inquiry inquiry = null;
        switch (num) {
            case 1:
                inquiry = new Question();
                Thread.currentThread().setPriority(10);
                inquiry.fillDataByUser();
                break;
            case 2:
                inquiry = new Request();
                inquiry.fillDataByUser();
                break;
            case 3:
                inquiry = new Complaint();
                inquiry.fillDataByUser();
                break;
            default:
                System.out.println("there is no such quiry type");
                break;
        }
        if (inquiry != null) {
            addInquiry(inquiry);
        }
        return inquiry;
    }

    public void addInquiry(Inquiry inquiry) {
        inquiry.setCode(Inquiry.getNextCodeVal());
        InquiryHandling inquiryHandling = new InquiryHandling(inquiry);
        handleFiles.saveFile(inquiry);
        handleFiles.saveNextCodeVal(inquiry.getCode(),"nextCodeVal.txt");
        boolean add = QUIRIES_QUEUE.add(inquiryHandling);
        System.out.println("added : " + add);
    }

    public void processInquiryManager() {
        while (!QUIRIES_QUEUE.isEmpty()) {
            InquiryHandling quiry = QUIRIES_QUEUE.poll();
            Thread thread = new Thread(quiry);
            thread.start();
        }
    }

    public static Object parseFromFile(List<String> values) throws IOException {
        String[] properties = values.toArray(new String[0]);
        if (properties.length == 3) {
            String firstName = properties[0];
            int tz = Integer.parseInt(properties[1]);
            return new Representative(firstName, tz);
        }
        String type = properties[0];
        String data = properties[1];
        LocalDateTime creationDate = LocalDateTime.parse(properties[2]);
        int code = Integer.parseInt(properties[3]);
        Inquiry inquiry = null;
        switch (type) {
            case "Question":
                inquiry = new Question();
                break;
            case "Complaint":
                inquiry = new Complaint();
                ((Complaint) inquiry).setAssignedBranch(properties[4]);
                break;
            case "Request":
                inquiry = new Request();
                break;
            default:
                System.out.println("Unknown inquiry type in the file");
                return null;
        }
        inquiry.setDescription(data);
        inquiry.setCreationDate(creationDate);
        inquiry.setCode(code);
        return inquiry;
    }

    public void defineRepresentative() {
        File dir = new File("Representative");
        dir.mkdir();
        System.out.println("enter representative details - press 1 ,exit -press 0 ");
        Scanner scan = new Scanner(System.in);
        int num = scan.nextInt();
        scan.nextLine();
        while (num == 1) {
            System.out.println("enter the Representative name ");
            String name = scan.nextLine();
            System.out.println("enter the Representative tz ");
            int tz = scan.nextInt();
            scan.nextLine();
            Representative representative = new Representative(name, tz);
            boolean success = handleFiles.saveCSV(representative, dir.getPath() + "\\" + String.valueOf(representative.getRepresentative_code()));
            handleFiles.saveNextCodeVal(representative.getRepresentative_code(),"nextCodeVal-representatives.txt");
            if (success) {
                System.out.println("Representative saved successfully."+representative.getRepresentative_code());
            } else {
                System.out.println("Failed to save representative.");
            }
            System.out.println("enter representative details - press 1 ,exit -press 0 ");
            num = scan.nextInt();
            scan.nextLine();
        }
    }

//    static {
//        System.out.println("in inquiry manager static block");
//        InquiryManager.getInstance();
//        getAllInqureAndRepresentative();
//
//    }


    public static void getAllInqureAndRepresentative() {
        String[] directoriesArray = {"Complaint", "Question", "Request", "Representative"};
        for (String path : directoriesArray) {
            Path dir = Paths.get(path);
            if (Files.exists(dir)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                    for (Path entry : stream) {
                        File file = entry.toFile();
                        List<String> properties = readFile(file);
                        if (path.equals("Representative")) {
                            Representative representative = (Representative) parseFromFile(properties);
                            if (representative != null) {
                                representatives.add(representative);
                                if (representative.getRepresentative_code() > Representative.getCounter()) {
                                    Representative.setCounter(representative.getRepresentative_code());
                                }
                            }
                        } else {
                            Inquiry inquiry = (Inquiry) parseFromFile(properties);
                            if (inquiry != null) {
                                InquiryHandling inquiryHandling = new InquiryHandling(inquiry);
                                QUIRIES_QUEUE.add(inquiryHandling);
                            }
                        }
                        System.out.println(entry.getFileName());

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File f = new File("nextCodeVal.txt");
        if (f.exists()) {
            HandleFiles handleFiles = new HandleFiles();
            Inquiry.setNextCodeVal(handleFiles.readNextCodeVal(f));
            System.out.println("code :" + Inquiry.getNextCodeVal());
        }
    }


    public int getAmountInquiryInMonth(int numMonth) {
        int sum = 0;
            String[] directoriesArray = {"Complaint", "Question", "Request"};
            for (String path : directoriesArray) {
                Path dir = Paths.get(path);
                if (Files.exists(dir)) {
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {

                        for (Path entry : stream) {
                            File file = entry.toFile();
                            List<String> properties = readFile(file);
                            String[] propertiesData = properties.toArray(new String[0]);
                            String data = propertiesData[1];
                            LocalDateTime dateTime = LocalDateTime.parse(data);
                            int month = dateTime.getMonthValue();
                            if(numMonth==month)
                                sum++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        return  sum;
    }

}



