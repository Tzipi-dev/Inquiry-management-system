package Data;

import Exceptions.InquiryException;
import Exceptions.InquiryRunTimeException;
import HandleStoreFiles.ForSaving;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;

public class Complaint extends Inquiry implements Serializable {

    private String assignedBranch;
    private int secondsOfTreatment=5;
    public int getSeconds(){
        return secondsOfTreatment;
    }
    public String getAssignedBranch() {
        return assignedBranch;
    }

    public void setAssignedBranch(String assignedBranch) {
        this.assignedBranch = assignedBranch;
    }

    @Override
    public void fillDataByUser() {
        super.fillDataByUser();
        System.out.println("enter the branch name where the complaint is being filed ");
        Scanner scan = new Scanner(System.in);
        setAssignedBranch(scan.nextLine());
    }
    public void handling(){
        try {
            Thread.currentThread().sleep(secondsOfTreatment*1000);
        }catch(InterruptedException e) {
            throw new InquiryRunTimeException(this.getCode());
        }
        Thread thread = Thread.currentThread();
        System.out.println("your complaint in handle, your code is : "+getCode());
    }

}
