import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class FileUploadUsingHttpURLConnection {

    private static final String API_URL = "https://file.io";
    private static final String BOUNDARY = "------------------------boundary";

    public static void main(String[] args) {
        File file = new File("example_call.txt"); // Replace with path to your file
        try {
            String response = uploadFile(file);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String uploadFile(File file) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        connection.setDoOutput(true);

        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            out.writeBytes("--" + BOUNDARY + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
            out.writeBytes("Content-Type: text/plain\r\n\r\n");
            out.write(Files.readAllBytes(file.toPath()));
            out.writeBytes("\r\n");
            out.writeBytes("--" + BOUNDARY + "--\r\n");
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            throw new IOException("Server returned non-OK code: " + responseCode);
        }
    }
}


