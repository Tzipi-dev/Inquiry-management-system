package Data;

import Exceptions.InquiryException;
import Exceptions.InquiryRunTimeException;
import HandleStoreFiles.ForSaving;
import HandleStoreFiles.HandleFiles;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public abstract class Inquiry implements ForSaving, Serializable {
    public enum Status {Open, Cancelled, Treated, History}
    static Integer nextCodeVal = 0;
    private Integer code;
    private String description;
    private LocalDateTime creationDate;
    private static Status status;
    String className;

    public Status getStatus() {
        return this.status;
    }

    public String getClassName() {
        return className;
    }

    public static Integer getNextCodeVal() {
        return nextCodeVal;
    }

    public static void setNextCodeVal(Integer nextCodeVal) {
        Inquiry.nextCodeVal = nextCodeVal;

    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    protected Inquiry() {
        Inquiry.setNextCodeVal(nextCodeVal + 1);
        className = this.getClass().getSimpleName();
        HandleFiles.saveNextCodeVal(nextCodeVal,"nextCodeVal.txt");
        Inquiry.status=Status.Open;
    }

    public void fillDataByUser() {
        code = nextCodeVal;
        System.out.println("enter your quiry description");
        Scanner scan = new Scanner(System.in);
        setDescription(scan.nextLine());
        setCreationDate(LocalDateTime.now());
    }

    public abstract void handling();

    @Override
    public String getFolderName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getFileName() {
        String type = this.getClass().getSimpleName();
        switch (type) {
            case "Question":
                return "Question" + String.valueOf(this.code);
            case "Request":
                return "Request" + String.valueOf(this.code);
            case "Complaint":
                return "Complaint" + String.valueOf(this.code);
            default:
                System.out.println("there is no such type of inquiry");
                break;
        }
        return "";
    }

    @Override
    public String getData() {
        return getClassName() + "," + this.description;
    }

    @Override
    public List<String> readFile(File f) {
        return List.of();
    }

    @Override
    public void parseFromFile() {

    }
    private int seconds=5;
    public int getSeconds(){
        return seconds;
    }
}


