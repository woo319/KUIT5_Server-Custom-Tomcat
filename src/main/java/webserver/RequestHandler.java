package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String requestLine = br.readLine(); // 요청 첫 줄
            System.out.println(requestLine);
            if (requestLine == null || requestLine.isEmpty()) return;

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String path = tokens[1];

            //header 읽기(재사용 위해 List로)
                List<String> headers = new ArrayList<>();
                String line;
                int contentLength = 0;
                boolean isLogined = false;

                while ((line = br.readLine()) != null && !line.isEmpty()) {
                    headers.add(line);
                    if (line.startsWith("Content-Length")) {
                        contentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                    if (line.startsWith("Cookie")) {
                        String[] cookies = line.split(": ")[1].split("; ");
                        for (String cookie : cookies) {
                            if (cookie.equals("logined=true")) {
                                isLogined = true;
                                break;
                            }
                        }
                    }
                }

            /*if ("GET".equals(method) && path.startsWith("/user/signup")) {
                String queryString = "";
                if (path.contains("?")) {
                    queryString = path.substring(path.indexOf("?") + 1); // ? 다음 문자부터 잘라내기
                    path = path.substring(0, path.indexOf("?")); // 경로(/user/signup)만 따로 분리
                }

                // 파싱
                String userId = "", password = "", name = "", email = "";
                String[] params = queryString.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = URLDecoder.decode(keyValue[1], "UTF-8");
                        switch (key) {
                            case "userId" -> userId = value;
                            case "password" -> password = value;
                            case "name" -> name = value;
                            case "email" -> email = value;
                        }
                    }
                }

                // 저장
                User user = new User(userId, password, name, email);
                MemoryUserRepository.getInstance().save(user);

                // 리다이렉트
                response302Header(dos, "/index.html");
                return;
            }*/

            //회원가입
            if("POST".equals(method) && "/user/signup".equals(path)) {
                // 바디 읽기(post는 url이 아닌 body에 내용 담음 -> readline()하면 안되고 byte로 읽어야됨)
                char[] bodyChars = new char[contentLength];
                br.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars);
                System.out.println("!!!!!!!!!"+body);

                String userId = "", password = "", name = "", email = "";
                String[] params = body.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = URLDecoder.decode(keyValue[1], "UTF-8");
                        switch (key) {
                            case "userId" -> userId = value;
                            case "password" -> password = value;
                            case "name" -> name = value;
                            case "email" -> email = value;
                        }
                    }
                }

                User user = new User(userId, password, name, email);
                MemoryUserRepository.getInstance().save(user);

                response302Header(dos, "/index.html");
                return;
            }

            //로그인
            else if ("POST".equals(method) && "/user/login".equals(path)) {
                char[] bodyChars = new char[contentLength];
                br.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars);
                System.out.println("login body: " + body);

                String userId = "", password = "";
                String[] params = body.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = URLDecoder.decode(keyValue[1], "UTF-8");
                        switch (key) {
                            case "userId" -> userId = value;
                            case "password" -> password = value;
                        }
                    }
                }

                User user = MemoryUserRepository.getInstance().findByUserId(userId);
                System.out.println("찾은 유저: " + user);
                if (user != null && user.getPassword().equals(password)) {
                    System.out.println("로그인 성공!!!!!: " + userId);
                    response302WithCookie(dos, "/index.html", "logined=true");
                } else {
                    System.out.println("로그인 실패ㅠㅠ");
                    response302Header(dos, "/user/login_failed.html");
                }

                return;
            }


            //화면 띄우기
            if ("GET".equals(method) && "/index.html".equals(path)) {
                log.info("Serving index.html");
                byte[] body = Files.readAllBytes(Paths.get("./webapp/index.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);

            } else if ("GET".equals(method) && "/qna/show.html".equals(path)) {
                log.info("Serving show.html");
                byte[] body = Files.readAllBytes(Paths.get("./webapp/qna/show.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);

            } else if ("GET".equals(method) && "/qna/form.html".equals(path)) {
                log.info("Serving form.html");
                byte[] body = Files.readAllBytes(Paths.get("./webapp/qna/form.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);

            }
            else if ("GET".equals(method) && "/user/form.html".equals(path)) {
                log.info("Serving form.html");
                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/form.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);

            }
            else if ("GET".equals(method) && "/user/userList".equals(path)) {
                log.info("Serving form.html");
                if (isLogined) {
                    byte[] body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } else {
                    response302Header(dos, "/user/login.html");
                }
                return;

            }
            else if ("GET".equals(method) && "/user/login.html".equals(path)) {
                log.info("Serving form.html");
                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/login.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);

            }
            else if ("GET".equals(method) && "/user/login_failed.html".equals(path)) {
                log.info("Serving form.html");
                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/login_failed.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);

            }
            else {
                log.warning("Not Found: " + path);
                byte[] body = "<h1>404 Not Found</h1>".getBytes();
                response404Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
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
        dos.writeBytes("Content-Length: 0\r\n");
        dos.writeBytes("\r\n");
    }
}
