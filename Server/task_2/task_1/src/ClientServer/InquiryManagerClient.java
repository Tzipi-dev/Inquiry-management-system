package ClientServer;

import Data.*;
import HandleStoreFiles.HandleFiles;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class InquiryManagerClient {

    Socket connectToServer;
    HandleFiles handleFiles = new HandleFiles();
    RequestData request = null;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public InquiryManagerClient() {
        try {
            this.connectToServer = new Socket("localhost", 5000);
            this.out = new ObjectOutputStream(connectToServer.getOutputStream());
            this.in = new ObjectInputStream(connectToServer.getInputStream());
            System.out.println("connect to server at port "+this.connectToServer.getLocalSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void execut(){
        mainMenu();
    }

    public void closeConnection(){
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (connectToServer != null && !connectToServer.isClosed()) {
                connectToServer.close();
            }
            System.out.println("connection stopped...");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void mainMenu(){
        System.out.println("To view all inquiries press : 1 ,\nTo add inquiry press : 2 ,\nTo add representative press : 3 ,\nTo delete representative press : 4 ,\nTo cancellation Inquiry  press :5,");
        Scanner scan = new Scanner(System.in);
        int value = scan.nextInt();
        while (value!=7){
            switch (value){
                case 1 ->{
                    createRequest(InquiryManagerActions.ALL_INQUIRY);
                    sendRequest();
                }
                case 2 ->{
                    inquiryCreation();
                    if(request!=null){
                        sendRequest();}
                }
                case 3 ->{
                    addRepresentative();
                    if(request!=null){
                        sendRequest();}
                }
                case 4 ->{
                    deleteRepresentative();
                    sendRequest();
                }
                case 5 ->{
                    cancellationInquiry();
                    sendRequest();
                }
//                case 6 ->{
//                    getAmountInquiryInMonth();
//                    sendRequest();
//                }

            }
            System.out.println("To view all inquiries press : 1 , To add inquiry press : 2 , To add representative press : 3 , To delete representative press : 4 , To finish press : 5");
            value = scan.nextInt();
        }
        createRequest(InquiryManagerActions.EXIT);  // בקשת יציאה
        sendRequest();
        closeConnection();
    }
    public void createRequest(InquiryManagerActions action){
        this.request =  new RequestData(action);
    }
    public void createRequest(InquiryManagerActions action, Inquiry inquiry){
        if(this.request==null)
        {
            List<Inquiry> inquiries = new ArrayList<>();
            inquiries.add(inquiry);
            this.request= new RequestData(action,inquiries.toArray(new Inquiry[0]));
        }
        else this.request.parameters.add(inquiry);
    }
    public void createRequest(InquiryManagerActions action, Representative representative){
        if(this.request==null)
        {
            List<Representative> representatives = new ArrayList<>();
            representatives.add(representative);
            this.request= new RequestData(action,representatives.toArray(new Representative[0]));
        }
        else this.request.parameters.add(representative);
    }
    public void createRequest(InquiryManagerActions action, int id){
        if(this.request==null)
        {
            List<Integer> id_List = new ArrayList<>();
            id_List.add(id);
            this.request= new RequestData(action,id_List.toArray(new Integer[0]));
        }
        else this.request.parameters.add(id);
    }
    public void inquiryCreation(){
        System.out.println("enter your quiry type: for question - 1 , for request - 2,for complaint - 3 ,for exit - 0");
        Scanner scan = new Scanner(System.in);
        int num = scan.nextInt();
        while(num!=0) {
            Inquiry inquiry = createInquiry(num);
            createRequest(InquiryManagerActions.ADD_INQUIRY,inquiry);
            if (inquiry != null) {
                handleFiles.saveNextCodeVal(inquiry.getCode(),"nextCodeVal.txt");
            }
            System.out.println("enter your quiry type: for question - 1 , for request - 2,for complaint - 3 ,for exit - 0");
            num = scan.nextInt();
        }
    }
    public Inquiry createInquiry(int num){
        Inquiry inquiry = null;
        switch (num){
            case 1:
                inquiry= new Question();

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
        if(inquiry!=null){
            System.out.println(inquiry.getCode()+"//"+inquiry.getData());
        }
        return inquiry;
    }

    public void addRepresentative(){
        File dir = new File("Representative");
        boolean x=dir.mkdir();
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
            boolean success = handleFiles.saveCSV(representative, dir.getPath() + "\\" +representative.getName());
            handleFiles.saveNextCodeVal(representative.getRepresentative_code(),"nextCodeVal-representatives.txt");
            createRequest(InquiryManagerActions.ADD_REPRESENTATIVE,representative);
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

    public void deleteRepresentative(){
        System.out.println("enter representative Id for delete");
        Scanner scan = new Scanner(System.in);
        int Id=scan.nextInt();
        createRequest(InquiryManagerActions.DELETE_REPRESENTATIVE_BY_ID, Id);
        System.out.println("delete successfully");
    }
    public void cancellationInquiry(){
        System.out.println("enter code for delete");
        Scanner scan = new Scanner(System.in);
        int code=scan.nextInt();
        createRequest(InquiryManagerActions.CANCELLATION_INQUIRY, code);
        System.out.println("delete successfully");
    }

    private void handleInquiryListResponse(Object result) {
        if (result != null && result instanceof LinkedList<?>) {
            LinkedList<?> allInquiries = (LinkedList<?>) result;
            if (!allInquiries.isEmpty() && allInquiries.getFirst() instanceof Inquiry) {
                for (Object obj : allInquiries) {
                    Inquiry inquiry = (Inquiry) obj;
                    System.out.println(inquiry.getClassName() + " " + inquiry.getCode());
                }
            }
        }
        System.out.println("list of inquiries is null or empty");
    }


    public void sendRequest() {
        try {
            out.writeObject(request);
            out.flush();
            System.out.println("Request sent to server.");

            ResponseData response = (ResponseData) in.readObject();
            if (response.result!=null){
                handleInquiryListResponse(response.result);
            }
            System.out.println("תגובה מהשרת: " + response.message  +   " status :"+response.status);
            this.request = null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending request: " + e.getMessage(), e);
        }
    }




}