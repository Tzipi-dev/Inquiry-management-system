package ClientServer;

public enum ResponseStatus {
    SCCESS(1), FAIL(2);
    private final int code ;
    ResponseStatus(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
