package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;
import model.enums.UserParamKey;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class LoginController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());

        String userId = decode(params.get(UserParamKey.USER_ID.key()));
        String password = decode(params.get(UserParamKey.PASSWORD.key()));

        User user = MemoryUserRepository.getInstance().findByUserId(userId);

        if (user != null && user.getPassword().equals(password)) {
            response.addHeader("Set-Cookie", "logined=true");
            response.redirect("/index.html");
        } else {
            response.redirect("/user/login_failed.html");
        }
    }

    private String decode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, "UTF-8");
    }
}
