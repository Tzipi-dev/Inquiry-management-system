package Data;

import Exceptions.InquiryRunTimeException;
import HandleStoreFiles.ForSaving;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Request extends Inquiry implements Serializable {
    private int secondsOfTreatment=3;
    public int getSeconds(){
        return secondsOfTreatment;
    }

    public void handling(){
        try {
            Thread.currentThread().sleep(secondsOfTreatment*1000);
        }catch(InterruptedException e){
            throw new InquiryRunTimeException(this.getCode());
        }
        System.out.println("your request in handle, your code is : "+getCode());
    }
}
