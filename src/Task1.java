import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Task1 {

    public void addUser(UserDto user) throws IOException, URISyntaxException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users";
        HttpRequest httpRequest = HttpRequest.newBuilder(new URI(uri))
                .POST(HttpRequest.BodyPublishers.ofString(user.toString()))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(httpResponse.body());
    }

    public void updateUser(UserDto user, String id) throws IOException, URISyntaxException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users" + id;
        HttpRequest httpRequest = HttpRequest.newBuilder(new URI(uri))
                .PUT(HttpRequest.BodyPublishers.ofString(user.toString()))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(httpResponse.body());
    }

    public void deleteUser(String id) throws IOException, URISyntaxException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users/" + id;
        HttpRequest httpRequest = HttpRequest.newBuilder(new URI(uri))
                .DELETE()
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("StatusCode = " + httpResponse.statusCode());
    }

    public void getUsers() throws IOException, URISyntaxException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users";
        System.out.println(getFromUri(uri));
    }

    public void getUserID(String id) throws IOException, URISyntaxException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users/" + id;
         System.out.println(getFromUri(uri));
    }

    public void getUserUsername(String username) throws IOException, URISyntaxException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users?username=" + username;
        System.out.println(getFromUri(uri));
    }

    public void getComents() throws URISyntaxException, IOException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/posts/"+ getIdLastPost().id + "/comments";
        createJsonFile(uri);
        System.out.println(getFromUri(uri));
    }



    public void openTasks() throws IOException, URISyntaxException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users/1/todos";

        ObjectMapper objectMapper = new ObjectMapper();
        List<TaskDto> taskDtos = objectMapper.readValue(getFromUri(uri), new TypeReference<List<TaskDto>>(){});
        List<TaskDto> unCompleted = taskDtos.stream()
                .filter(taskDto -> taskDto.getCompleted() == false)
                .collect(Collectors.toList());

        System.out.println(unCompleted);
    }

    private String getFromUri(String uri) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(new URI(uri))
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return httpResponse.body();
    }

    private PostDto getIdLastPost () throws URISyntaxException, IOException, InterruptedException {
        String postsUri = "https://jsonplaceholder.typicode.com/users/1/posts";
        ObjectMapper objectMapper = new ObjectMapper();
        List<PostDto> postDtos = objectMapper.readValue(getFromUri(postsUri), new TypeReference<List<PostDto>>(){});
        PostDto lastPost = postDtos.stream()
                .max(Comparator.comparing(PostDto::getId))
                .get();

        return lastPost;
    }

    private void createJsonFile(String uri) throws IOException, URISyntaxException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(new URI(uri))
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .build();

        HttpResponse<Path> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofFile(Path.of("user-"
                + getIdLastPost().userId
                + "-post-"
                + getIdLastPost().id + "-comments.json")));
    }
}
