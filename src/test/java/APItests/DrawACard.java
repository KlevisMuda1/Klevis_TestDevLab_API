package APItests;


import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DrawACard {


    //1. Obtain one deck and assert if response body matches the schema from documentation (also assert attribute values)
    @DisplayName("Assert response body matches the schema")
    @Test
    public void test1(){

        JsonPath jsonPath = given().accept(ContentType.JSON)
                .queryParam("count", 1)
                .when().get("https://deckofcardsapi.com/api/deck/new/draw")
                .then().assertThat().statusCode(200)
                .and().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("SingleDeckSchema.json"))
                .log().all().extract().jsonPath();

        //2. Save deck_id as it would be required for next request
        String deck_id = jsonPath.getString("deck_id");
        System.out.println("deck_id" + deck_id);
    }


    //3. Draw 52 cards from deck
    //4. Assert that remaining is 0 and that cards array contains 8C card there and in that array there is 52 elements.
    @DisplayName("Draw 52 cards from deck")
    @Test
    public void test2() {

        JsonPath jsonPath = given().accept(ContentType.JSON)
                .queryParam("count", 52)
                .when().get("https://deckofcardsapi.com/api/deck/new/draw")
                .then().assertThat().statusCode(200)
                .log().all().extract().jsonPath();

        //Assert that remaining is 0
        assertThat(jsonPath.getInt("remaining"), is(0));


        //Cards array contains 8C card there and in that array there is 52 elements.
        List<String> listOfCode = jsonPath.getList("cards.code");
        System.out.println(listOfCode);
        assertThat(listOfCode, hasItem("8C"));
        assertThat(listOfCode.size(), is(52));


    }
}
