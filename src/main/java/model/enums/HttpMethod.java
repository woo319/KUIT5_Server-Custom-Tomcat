package model.enums;  // ✅ 이거 꼭 있어야 해!
public enum HttpMethod {
    GET, POST;

    public static HttpMethod from(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }
}
