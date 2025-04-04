package webserver;

import http.HttpRequest;
import http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
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
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(out);

            RequestMapper requestMapper = new RequestMapper(httpRequest, httpResponse);
            requestMapper.proceed();

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
