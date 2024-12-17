package library.SonTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OpenLibraryService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Books> searchBooksByTitle(String title) {
        List<Books> bookList = new ArrayList<>();
        try {
            // Gọi OpenLibrary API
            String apiUrl = "https://openlibrary.org/search.json?title=" + title.replace(" ", "%20");
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response
                JsonNode rootNode = objectMapper.readTree(response.toString());
                JsonNode docs = rootNode.get("docs");
                if (docs != null) {
                    for (JsonNode doc : docs) {
                        String titleField = doc.has("title") ? doc.get("title").asText() : "No Title";
                        String authorField = doc.has("author_name") ? doc.get("author_name").get(0).asText() : "Unknown Author";
                        String coverId = doc.has("cover_i") ? doc.get("cover_i").asText() : null;
                        String coverUrl = (coverId != null) ? "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg" : "";

                        Books book = new Books(titleField, authorField, coverUrl);
                        bookList.add(book);
                    }
                }
            } else {
                throw new RuntimeException("HTTP GET Request Failed. Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookList;
    }
}

