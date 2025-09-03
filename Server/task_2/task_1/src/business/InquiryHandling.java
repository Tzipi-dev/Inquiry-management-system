package business;
import Data.Complaint;
import Data.Inquiry;
import Data.Question;
import Data.Request;
import Exceptions.InquiryException;
import Exceptions.InquiryRunTimeException;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
//1. Create a class InquiryHandling that extends Thread and has a field of type Inquiry.
public class InquiryHandling extends Thread{

    private Inquiry currentInquiry;

    public InquiryHandling(Inquiry inquiry) {
        this.currentInquiry = inquiry;
    }

    public Inquiry getCurrentInquiry() {
        return currentInquiry;
    }

    public void setCurrentInquiry(Inquiry currentInquiry) {
        this.currentInquiry = currentInquiry;
    }

    @Override
    public void run() {
        try {
            currentInquiry.handling();
        } catch (InquiryRunTimeException e) {
            throw new InquiryRunTimeException(currentInquiry.getCode());
        }
    }

    @Deprecated
    public void createInquiry() throws Exception {
        System.out.println("enter your quiry type: for question - 1 , for request - 2,for complaint - 3");
        Scanner scan = new Scanner(System.in);
        switch (scan.nextInt()){
            case 1:
                currentInquiry= new Question();
                Thread.currentThread().setPriority(10);
                break;
            case 2:
                currentInquiry = new Request();
                break;
            case 3:
                currentInquiry = new Complaint();
                break;
            default:
                System.out.println("there is no such quiry type");
                break;
        }
        currentInquiry.fillDataByUser();
    }

}
