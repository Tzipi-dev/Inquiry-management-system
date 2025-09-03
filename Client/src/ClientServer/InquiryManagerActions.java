package ClientServer;

public enum InquiryManagerActions {
    ALL_INQUIRY(1),ADD_INQUIRY(2),ADD_REPRESENTATIVE(3),DELETE_REPRESENTATIVE_BY_ID(4),CANCELLATION_INQUIRY(5),
    GET_AMOUNT_INQUIRY_IN_MONTH(6),EXIT(7);
    private final int code;

    InquiryManagerActions(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

