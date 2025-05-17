import java.io.*;
import java.net.Socket;
import java.nio.file.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private static final String ROOT = "wwwroot";

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }

            System.out.println("Requisição: " + requestLine);

            String[] tokens = requestLine.split(" ");
            if (tokens.length < 2) {
                send400(out);
                return;
            }
            String path = tokens[1];

            if (path.equals("/")) {
                path = "/index.html";
            }

            Path filePath = Paths.get(ROOT).resolve(path.substring(1)).normalize();

            if (!filePath.startsWith(Paths.get(ROOT))) {
                send403(out);
                return;
            }

            File file = filePath.toFile();

            if (!file.exists() || file.isDirectory()) {
                send404(out);
                return;
            }

            String contentType = "text/plain";
            if (file.getName().endsWith(".html") || file.getName().endsWith(".htm")) {
                contentType = "text/html";
            }

            byte[] fileBytes = Files.readAllBytes(file.toPath());

            String header = """
                            HTTP/1.1 200 OK\r
                            Content-Type: """ + contentType + "\r\n" +
                    "Content-Length: " + fileBytes.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

            out.write(header.getBytes());
            out.write(fileBytes);
            out.flush();

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send404(OutputStream out) throws IOException {
        String response = """
                          HTTP/1.1 404 Not Found\r
                          Content-Type: text/html\r
                          Connection: close\r
                          \r
                          <html><body><h1>404 Not Found</h1></body></html>
                          """;
        out.write(response.getBytes());
        out.flush();
        clientSocket.close();
    }

    private void send403(OutputStream out) throws IOException {
        String response = """
                          HTTP/1.1 403 Forbidden\r
                          Content-Type: text/html\r
                          Connection: close\r
                          \r
                          <html><body><h1>403 Forbidden</h1></body></html>
                          """;
        out.write(response.getBytes());
        out.flush();
        clientSocket.close();
    }

    private void send400(OutputStream out) throws IOException {
        String response = """
                          HTTP/1.1 400 Bad Request\r
                          Content-Type: text/html\r
                          Connection: close\r
                          \r
                          <html><body><h1>400 Bad Request</h1></body></html>
                          """;
        out.write(response.getBytes());
        out.flush();
        clientSocket.close();
    }
}
