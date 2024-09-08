package Booker;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class GetToken {

    private static final String BASE_URI = "https://restful-booker.herokuapp.com";
    private static final String LOGIN_PATH = "/auth";
    private static final String JSON_FILE_PATH = "src/test/resources/userDataLogIn.json";

    public GetToken() {
        RestAssured.baseURI = BASE_URI;
    }

    public String getToken() {
        try {
            String jsonBody = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH))); // читаннія з файлу для різноманіття

            Response response = given().
                    header("Content-Type", "application/json").
                    body(jsonBody).
                    when().
                    post(LOGIN_PATH).
                    then().
                    statusCode(200).
                    extract().response();

            return response.jsonPath().getString("token");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve token due to I/O error.");
        }
    }
}
