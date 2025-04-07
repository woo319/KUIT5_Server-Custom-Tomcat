package model.enums;
public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie");

    private final String key;

    HttpHeader(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
