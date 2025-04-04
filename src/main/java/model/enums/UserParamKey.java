package model.enums;  // ✅ 이거 꼭 있어야 해!
public enum UserParamKey {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String key;

    UserParamKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
