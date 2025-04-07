package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest request;
    private final HttpResponse response;
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/user/signup", new SignupController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/userList", new UserListController());
    }

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public void proceed() throws Exception {
        Controller controller = controllers.get(request.getPath());
        if (controller != null) {
            controller.service(request, response);
        } else {
            handleStaticResourceOr404();
        }
    }

    private void handleStaticResourceOr404() throws Exception {
        String path = request.getPath();

        if (path.equals("/index.html") || path.equals("/user/form.html") || path.equals("/user/login.html") ||
                path.equals("/user/login_failed.html") || path.equals("/qna/form.html") || path.equals("/qna/show.html")) {
            response.forward(path);
        } else if (path.endsWith(".css")) {
            response.forward(path);
        } else {
            response.forward("/404.html");
        }
    }
}
