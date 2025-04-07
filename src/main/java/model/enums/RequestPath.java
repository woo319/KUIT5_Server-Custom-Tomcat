package model.enums;
public enum RequestPath {
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    LOGIN_FORM("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    INDEX("/index.html"),
    USER_LIST("/user/userList");

    private final String path;

    RequestPath(String path) {
        this.path = path;
    }

}
