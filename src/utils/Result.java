package utils;

import java.util.List;

public class Result {
    private List<String> message;
    private boolean success;
    public Result(List<String> message, boolean success) {
        this.message = message;
        this.success = success;
    }
    public List<String> getMessage() {return message; }
    public boolean isSuccess() { return success; }
}
