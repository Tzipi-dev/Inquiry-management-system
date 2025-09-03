package Exceptions;

public class InquiryRunTimeException extends RuntimeException{
    private int inquiryCode;

    public InquiryRunTimeException(int inquiryCode) {
        this.inquiryCode = inquiryCode;
    }

    @Override
    public String getMessage() {

        return "inquiry :"+String.valueOf(inquiryCode)+" error: "+super.getMessage();
    }
}
