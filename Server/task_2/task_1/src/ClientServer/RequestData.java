package ClientServer;

import Data.Inquiry;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestData implements Serializable {
    InquiryManagerActions action;
    List<Object> parameters;
    public RequestData(InquiryManagerActions action) {
        this.action = action;
    }
    public RequestData(InquiryManagerActions action, Object... parameters) {
        this.action = action;
        this.parameters =new ArrayList<>(Arrays.asList(parameters));
    }
    public InquiryManagerActions getAction() {
        return action;
    }
    public <T> List<T> castParameters(Class<T> targetType) {
        List<T> result = new ArrayList<>();
        for (Object param : parameters) {
            if (!targetType.isInstance(param)) {
                throw new ClassCastException("Parameter " + param + " is not of type " + targetType.getSimpleName());
            }
            result.add(targetType.cast(param));
        }
        return result;
    }
}