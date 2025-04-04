package http;

import model.enums.HttpStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final DataOutputStream dos;
    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException {
        try {
            byte[] body = Files.readAllBytes(Paths.get("./webapp" + path));
            addHeader("Content-Type", contentType(path));
            addHeader("Content-Length", String.valueOf(body.length));
            writeResponse(HttpStatus.OK, body);
        } catch (IOException e) {
            String message = "<h1>404 Not Found</h1><p>요청하신 페이지를 찾을 수 없습니다.</p>";
            byte[] body = message.getBytes();
            addHeader("Content-Type", "text/html;charset=utf-8");
            addHeader("Content-Length", String.valueOf(body.length));
            writeResponse(HttpStatus.NOT_FOUND, body);
        }
    }

    public void redirect(String path) throws IOException {
        addHeader("Location", path);
        writeResponse(HttpStatus.FOUND, null);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private void writeResponse(HttpStatus status, byte[] body) throws IOException {
        dos.writeBytes("HTTP/1.1 " + status.code() + " " + status.message() + "\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        dos.writeBytes("\r\n");
        if (body != null) {
            dos.write(body, 0, body.length);
        }
        dos.flush();
    }

    private String contentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
