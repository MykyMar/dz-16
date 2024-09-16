package Booker;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;


public class BookerTests {

    GetToken getToken = new GetToken();
    String token = getToken.getToken(); //отримуэмо токен
    int id1;
    int id2;

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Cookie", String.format("token=%s", token))
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()
                .filter(new AllureRestAssured());
    }


    @Test(groups = "testGroup", priority = 1)
    public void testPOSTCreateBooking() {
        // Використання RandomDateGenerator для створення випадкових дат
        BookingDates randomDate = new RandomDateGenerator().generateRandomBookingDates();
        // Створення body
        CreateBookingRequest body = CreateBookingRequest.builder()
                .firstname("Tim")
                .lastname("Brown")
                .totalprice(131)
                .depositpaid(true)
                .bookingdates(randomDate)
                .additionalneeds("Breakfast")
                .build();
        // Відправка POST запиту
        Response response = given()
                .body(body)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .body(not(emptyOrNullString()))
                .body("bookingid", notNullValue())
                .extract()
                .response();

    }

    @Test(groups = "testGroup", priority = 2)
    public void testGETBookingIds() {
        // Відправка GET запиту
        Response response = RestAssured.get("/booking")
                .then()
                .statusCode(200)
                .body(not(emptyOrNullString()))
                .extract()
                .response();

        // Створіємо список з bookingid та беремо з них 2 випадкові значення
        List<Integer> bookingIds = response.jsonPath().getList("bookingid", Integer.class);
        Random random = new Random();
        id1 = bookingIds.get(random.nextInt(bookingIds.size()));
        id2 = bookingIds.get(random.nextInt(bookingIds.size()));

        // Перевіряємо, що нащі значення точно є в списку
        assertThat("Random ID 1 should be in the list", bookingIds, hasItem(id1));
        assertThat("Random ID 2 should be in the list", bookingIds, hasItem(id2));

    }

    @Test(dependsOnMethods = "testGETBookingIds")
    public void testPUTCHChangePrice() {
        int totalPrice = 1234;

        // Створення копії body для порівняня змін
        Response getResponse = RestAssured.get("/booking/{id}", id1);
        Map<String, Object> actualResponse = getResponse.jsonPath().getMap("");

        // Відправка PATCH запиту
        Response response = given()
                .body("{\"totalprice\": " + totalPrice + "}")
                .when()
                .patch("/booking/{id}", id1)
                .then()
                .statusCode(200)
                .body(not(emptyOrNullString()))
                .body("totalprice", equalTo(totalPrice))
                .extract()
                .response();

        // Перевіряємо, що змінились тільки вказані в запиті поля
        Map<String, Object> expectedResponse = response.jsonPath().getMap("");
        expectedResponse.remove("totalprice");
        actualResponse.remove("totalprice");
        Assert.assertEquals(expectedResponse, actualResponse);

    }

    @Test(dependsOnMethods = "testGETBookingIds")
    public void testPUTChangeParams() {
        // Створення копій body для порівняня змін
        Response getResponse = RestAssured.get("/booking/{id}", id2);
        Map<String, Object> oldBody = getResponse.jsonPath().getMap("");

        Map<String, Object> newBody = new HashMap<>(oldBody);
        newBody.put("totalprice", 500);
        newBody.put("additionalneeds", "Lunch");

        // Відправка PUT запиту
        Response putResponse = RestAssured
                .given()
                .body(newBody)
                .put("/booking/{id}", id2)
                .then()
                .statusCode(200)
                .body(not(emptyOrNullString()))
                .body("totalprice", equalTo(500))
                .body("additionalneeds", equalTo("Lunch"))
                .extract()
                .response();

        // Перевіряємо, що змінились тільки вказані в запиті поля
        newBody.remove("totalprice"); newBody.remove("additionalneeds");
        oldBody.remove("totalprice"); oldBody.remove("additionalneeds");

        Assert.assertEquals(oldBody, newBody);

    }

    @Test(dependsOnMethods = "testPUTCHChangePrice")
    public void testDELETEBooking() {

        Response response = RestAssured
                .given()
                .delete("/booking/{id}", id1)
                .then()
                .statusCode(201)
                .extract()
                .response();

        Response getResponse = RestAssured.get("/booking/{id}", id1)
                .then()
                .statusCode(404)
                .extract()
                .response();

    }

}

