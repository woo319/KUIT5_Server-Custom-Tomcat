package webserver;

import db.MemoryUserRepository;
import model.User;
import http.util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private final Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String path = tokens[1];

            Map<String, String> headers = new HashMap<>();
            int contentLength = 0;
            boolean isLogined = false;

            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                int index = line.indexOf(": ");
                if (index > 0) {
                    headers.put(line.substring(0, index), line.substring(index + 2));
                }

                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }

                if (line.startsWith("Cookie")) {
                    String[] cookies = line.split(": ")[1].split("; ");
                    for (String cookie : cookies) {
                        if ("logined=true".equals(cookie)) {
                            isLogined = true;
                        }
                    }
                }
            }

            if ("POST".equals(method) && "/user/signup".equals(path)) {
                String body = readBody(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                User user = createUserFromParams(params);
                MemoryUserRepository.getInstance().save(user);
                response302Header(dos, "/index.html");
                return;
            }

            if ("POST".equals(method) && "/user/login".equals(path)) {
                String body = readBody(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

                String userId = decode(params.get("userId"));
                String password = decode(params.get("password"));

                if (isLoginSuccess(userId, password)) {
                    response302WithCookie(dos, "/index.html", "logined=true");
                    return;
                }

                response302Header(dos, "/user/login_failed.html");
                return;
            }

            if ("GET".equals(method) && "/index.html".equals(path)) {
                byte[] body = Files.readAllBytes(Paths.get("./webapp/index.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if ("GET".equals(method) && "/qna/show.html".equals(path)) {
                byte[] body = Files.readAllBytes(Paths.get("./webapp/qna/show.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if ("GET".equals(method) && "/qna/form.html".equals(path)) {
                byte[] body = Files.readAllBytes(Paths.get("./webapp/qna/form.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if ("GET".equals(method) && "/user/form.html".equals(path)) {
                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/form.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if ("GET".equals(method) && "/user/userList".equals(path)) {
                if (isLogined) {
                    byte[] body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } else {
                    response302Header(dos, "/user/login.html");
                }
                return;
            }

            if ("GET".equals(method) && "/user/login.html".equals(path)) {
                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/login.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if ("GET".equals(method) && "/user/login_failed.html".equals(path)) {
                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/login_failed.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);
                return;
            }

            if (path.endsWith(".css")) {
                byte[] body = Files.readAllBytes(Paths.get("./webapp" + path));
                response200HeaderForCSS(dos, body.length);
                responseBody(dos, body);
                return;
            }

            byte[] body = "<h1>404 Not Found</h1>".getBytes();
            response404Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private String readBody(BufferedReader br, int contentLength) throws IOException {
        char[] bodyChars = new char[contentLength];
        br.read(bodyChars, 0, contentLength);
        return new String(bodyChars);
    }

    private String decode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, "UTF-8");
    }

    private User createUserFromParams(Map<String, String> params) throws UnsupportedEncodingException {
        String userId = decode(params.get("userId"));
        String password = decode(params.get("password"));
        String name = decode(params.get("name"));
        String email = decode(params.get("email"));
        return new User(userId, password, name, email);
    }

    private boolean isLoginSuccess(String userId, String password) {
        User user = MemoryUserRepository.getInstance().findByUserId(userId);
        return user != null && user.getPassword().equals(password);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderForCSS(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void response302Header(DataOutputStream dos, String path) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found\r\n");
        dos.writeBytes("Location: " + path + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void response302WithCookie(DataOutputStream dos, String path, String cookie) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found\r\n");
        dos.writeBytes("Location: " + path + "\r\n");
        dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
        dos.writeBytes("\r\n");
    }
}
