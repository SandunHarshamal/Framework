package testBase;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ExtentManager;

import java.util.Map;

public class CommandsAPI {

    private static final Logger logger = LogManager.getLogger(CommandsAPI.class);

    // ThreadSafe Storage for Authentication
    private static ThreadLocal<String> threadBearerToken = new ThreadLocal<>();
    private static ThreadLocal<String[]> threadBasicAuth = new ThreadLocal<>();

    // =======================================================
    //  AUTHENTICATION METHODS
    // =======================================================

    /**
     * Set Bearer Token for all subsequent requests in this thread.
     */
    public void setBearerToken(String token) {
        threadBearerToken.set(token);
        logger.info("Bearer Token set for current thread.");
    }

    /**
     * Set Basic Auth for all subsequent requests in this thread.
     */
    public void setBasicAuth(String username, String password) {
        threadBasicAuth.set(new String[]{username, password});
        logger.info("Basic Auth set for current thread.");
    }

    /**
     * Clear all auth headers (Call this after test or logout).
     */
    public void resetAuth() {
        threadBearerToken.remove();
        threadBasicAuth.remove();
        logger.info("Authentication cleared.");
    }

    // =======================================================
    //  REQUEST METHODS
    // =======================================================

    public Response get(String endpoint) {
        return sendRequest("GET", endpoint, null, null);
    }

    public Response post(String endpoint, Object body) {
        return sendRequest("POST", endpoint, body, null);
    }

    public Response post(String endpoint, Object body, Map<String, String> headers) {
        return sendRequest("POST", endpoint, body, headers);
    }

    public Response put(String endpoint, Object body) {
        return sendRequest("PUT", endpoint, body, null);
    }

    public Response delete(String endpoint) {
        return sendRequest("DELETE", endpoint, null, null);
    }

    // =======================================================
    //  CORE ENGINE
    // =======================================================

    private Response sendRequest(String method, String endpoint, Object body, Map<String, String> headers) {
        ExtentTest testLog = ExtentManager.getTest();
        Response response = null;
        String fullUrl = RestAssured.baseURI != null ? RestAssured.baseURI + endpoint : endpoint;

        try {
            RequestSpecification request = RestAssured.given();

            // 1. APPLY AUTHENTICATION (If set)
            if (threadBearerToken.get() != null) {
                request.header("Authorization", "Bearer " + threadBearerToken.get());
            } else if (threadBasicAuth.get() != null) {
                String[] auth = threadBasicAuth.get();
                request.auth().basic(auth[0], auth[1]);
            }

            // 2. Add Custom Headers
            if (headers != null) {
                request.headers(headers);
            }

            // 3. Add Body
            if (body != null) {
                request.contentType(ContentType.JSON);
                request.body(body);
            }

            // 4. Logging
            logger.info("Sending " + method + " request to: " + fullUrl);
            if (testLog != null) {
                testLog.info("<b>Request:</b> " + method + " " + fullUrl);
                if (body != null) {
                    testLog.info(MarkupHelper.createCodeBlock(body.toString(), CodeLanguage.JSON));
                }
            }

            // 5. Execute
            switch (method.toUpperCase()) {
                case "GET" -> response = request.get(endpoint);
                case "POST" -> response = request.post(endpoint);
                case "PUT" -> response = request.put(endpoint);
                case "DELETE" -> request.delete(endpoint);
                case "PATCH" -> request.patch(endpoint);
                default -> throw new IllegalArgumentException("Invalid API Method: " + method);
            }

            // 6. Response Logging
            if (response != null) {
                String logMsg = "Received Response. Status: " + response.getStatusCode();
                logger.info(logMsg);
                if (testLog != null) {
                    testLog.pass(logMsg);
                    String prettyRes = response.asPrettyString();
                    if (prettyRes != null && !prettyRes.isEmpty()) {
                        testLog.info(MarkupHelper.createCodeBlock(prettyRes, CodeLanguage.JSON));
                    }
                }
            }
            return response;

        } catch (Exception e) {
            String error = "API Call Failed: " + method + " " + endpoint;
            logger.error(error, e);
            if (testLog != null) testLog.fail(error + "<br>Error: " + e.getMessage());
            throw new RuntimeException(error, e);
        }
    }

    // =======================================================
    //  HELPER METHODS
    // =======================================================

    /**
     * Extracts a String value from the JSON Response using JSONPath.
     * Example: extractString(response, "token") or "data.id"
     */
    public String extractString(Response response, String jsonPath) {
        try {
            String value = response.jsonPath().getString(jsonPath);
            logger.info("Extracted '" + jsonPath + "': " + value);
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract '" + jsonPath + "' from response.");
        }
    }

    public void verifyStatusCode(Response response, int expectedCode) {
        int actual = response.getStatusCode();
        if (actual == expectedCode) {
            ExtentManager.getTest().pass("Status Verified: " + expectedCode);
        } else {
            ExtentManager.getTest().fail("Status Mismatch. Exp: " + expectedCode + ", Act: " + actual);
            throw new AssertionError("Status Mismatch");
        }
    }

    public void startBusinessComponent(String bcName) {
        // Same as your existing code
        if (ExtentManager.getTest() != null)
            ExtentManager.getTest().info("<b style='color:teal'>API BC Started: " + bcName + "</b>");
    }

    public void endBusinessComponent(String bcName) {
        // Same as your existing code
        if (ExtentManager.getTest() != null)
            ExtentManager.getTest().info("<b style='color:teal'>API BC Ended: " + bcName + "</b>");
    }
}