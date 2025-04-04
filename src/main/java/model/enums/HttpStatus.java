package model.enums;  // ✅ 이거 꼭 있어야 해!
public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
