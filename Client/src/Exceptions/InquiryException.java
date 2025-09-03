package Exceptions;

public class InquiryException extends Exception{
    private int inquiryCode;

    public InquiryException(int inquiryCode) {
        this.inquiryCode = inquiryCode;
    }

    @Override
    public String getMessage() {
        return "inquiry :"+String.valueOf(inquiryCode)+" error: "+super.getMessage();
    }
}
