package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            String line;
            while (!(line = br.readLine()).isEmpty()) {
                System.out.println(line);
            }
            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String path = tokens[1];

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
            else if ("GET".equals(method) && "/user/list.html".equals(path)) {
                log.info("Serving form.html");
                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));
                response200Header(dos, body.length);
                responseBody(dos, body);

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
}
