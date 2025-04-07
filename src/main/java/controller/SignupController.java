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

public class SignupController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
        User user = createUserFromParams(params);
        MemoryUserRepository.getInstance().save(user);
        response.redirect("/index.html");
    }

    private User createUserFromParams(Map<String, String> params) throws UnsupportedEncodingException {
        return new User(
                decode(params.get(UserParamKey.USER_ID.key())), //key에 해당하는 값 찾아오기
                decode(params.get(UserParamKey.PASSWORD.key())),
                decode(params.get(UserParamKey.NAME.key())),
                decode(params.get(UserParamKey.EMAIL.key()))
        );
    }

    private String decode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, "UTF-8");
    }
}
