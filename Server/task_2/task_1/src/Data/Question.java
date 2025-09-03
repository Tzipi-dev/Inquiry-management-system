package Data;

import Exceptions.InquiryRunTimeException;
import HandleStoreFiles.ForSaving;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Question extends Inquiry implements Serializable {
    private int secondsOfTreatment = 8;

    public int getSeconds() {
        return secondsOfTreatment;
    }

    public void handling() {
        try {
            Thread.currentThread().sleep(secondsOfTreatment * 1000);
        } catch (InterruptedException e) {
            throw new InquiryRunTimeException(this.getCode());
        }

        System.out.println("your question in handle, your code is : " + getCode());
    }

}
