package controller;

import http.HttpRequest;
import http.HttpResponse;
import model.enums.HttpHeader;

public class UserListController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String cookie = request.getHeader(HttpHeader.COOKIE.key());

        if (cookie != null && cookie.contains("logined=true")) {
            response.forward("/user/list.html");
        } else {
            response.redirect("/user/login.html");
        }
    }
}
