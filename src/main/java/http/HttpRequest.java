package http;

import model.enums.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(HttpMethod method, String path, String version,
                        Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        //시작부분
        String startLine = br.readLine();
        String[] parts = startLine.split(" ");
        HttpMethod method = HttpMethod.from(parts[0]);
        String path = parts[1];
        String version = parts[2];

        //헤더
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(": ");
            if (idx > 0) {
                headers.put(line.substring(0, idx), line.substring(idx + 2));
            }
        }

        //바디
        int contentLength = headers.containsKey("Content-Length")
                ? Integer.parseInt(headers.get("Content-Length"))
                : 0;

        char[] bodyChars = new char[contentLength];
        br.read(bodyChars, 0, contentLength);
        String body = new String(bodyChars);

        return new HttpRequest(method, path, version, headers, body);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public boolean hasHeader(String key) {
        return headers.containsKey(key);
    }

    public String getBody() {
        return body;
    }
}
