package Data;

import Exceptions.InquiryRunTimeException;
import HandleStoreFiles.ForSaving;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Request extends Inquiry implements Serializable {
    private int seconds=3;
    public int getSeconds(){
        return seconds;
    }

    public void handling(){
        try {
            Thread.currentThread().sleep(seconds*1000);
        }catch(InterruptedException e){
            throw new InquiryRunTimeException(this.getCode());
        }
        System.out.println("your request in handle, your code is : "+getCode());
    }
}
