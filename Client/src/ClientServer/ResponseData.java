package ClientServer;

import java.io.Serializable;
import java.util.Optional;

public class ResponseData implements Serializable {

    ResponseStatus status;
    String message;
    Object result;

    public ResponseData(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseData(ResponseStatus status, String message, Object result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }
}
