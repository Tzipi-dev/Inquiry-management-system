package ClientServer;

import Data.Inquiry;
import Data.Representative;

import java.util.Arrays;
import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

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

    public List<Object> getParameters() {
        return parameters;
    }

    // Inquiry | Representative | Integer :פונקציה להמרת הרשימה לטיפוס הרצוי
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
