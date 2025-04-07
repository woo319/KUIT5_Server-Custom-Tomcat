package model.enums;
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
