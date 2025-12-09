package libraries;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import testBase.CommandsAPI;
import java.util.HashMap;
import java.util.Map;

public class LIB_UserAPI {

    CommandsAPI api = new CommandsAPI();

    public void bc_createUser(String name, String job) {
        api.startBusinessComponent("bc_createUser");

        // Prepare Body
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("job", job);

        // Execute
        Response response = api.post("https://reqres.in/api/users", body);

        // Verify
        api.verifyStatusCode(response, 201);

        api.endBusinessComponent("bc_createUser");
    }
}