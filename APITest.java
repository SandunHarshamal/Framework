package TestSuites;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class APITest {

    @Test
    public void getPatientDetails() {
        Response res = given()
                .baseUri("https://api.cambio.com")
                .when()
                .get("/patients/1001")
                .then()
                .statusCode(200)
                .extract().response();
        Assert.assertTrue(res.jsonPath().getString("name").length() > 0);
    }
}
