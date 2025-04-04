package model.enums;  // ✅ 이거 꼭 있어야 해!

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
