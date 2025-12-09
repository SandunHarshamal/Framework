package testBase;


import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import utils.ConfigReader;
import utils.DriverManager;
import utils.ExtentManager;

import java.time.Duration;


public class Commands {

    private static final Logger logger = LogManager.getLogger(Commands.class);
    int elementFunctionReTry = Integer.parseInt(ConfigReader.getInstance().getProperty("elementFunctionReTry").toLowerCase());

    public void open(String url) {
        // Call the Singleton
        ExtentTest testLog = ExtentManager.getTest();
        try {
            DriverManager.getInstance().initDriver();
            DriverManager.getDriver().manage().window().maximize();
            DriverManager.getDriver().get(url);
            if (testLog != null) testLog.pass("Successfully opened browser. Url : "+url);
            logger.info("Successfully opened browser. Url : "+url);
        } catch (Exception e) {
            logger.error("Failed to open the browser" + e.getMessage());
            if (testLog != null) {
                // FIX: Added title to the screenshot
                testLog.fail("Failed to open the browser",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(captureScreenshotAsBase64(), "Error Screen").build());
            }
            throw new RuntimeException(e);
        }
    }


    // Helper to close browser after test
    public void quitBrowser() {
        DriverManager.quitDriver();
    }


    /**
     * Click an element located by the given locator with a parameter.
     * @param locator By locator of the element to click.
     */
    public void click(By locator) {
        String msg;
        WebElement element = null;
        // Get the current logger safely
        ExtentTest testLog = ExtentManager.getTest();
        try {
            element = waitForElementAndPerformAction(locator);
            for (int attempt = 0; attempt < elementFunctionReTry+1; attempt++) {
                try {
                    logger.info("Trying to click on element [" +locator+ "]");
                    element.click();
                    msg = "Successfully clicked on element [" + locator + "]";
                    logger.info(msg);
                    // Log to Extent Report
                    if (testLog != null) testLog.pass(msg);
                    break;
                } catch (Exception e) {
                    if (attempt == elementFunctionReTry) {
                        throw new RuntimeException("Retry limit exceeded");
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            String elementInfo = (element != null) ? getxpath(element) : "null";
            msg = "<b>Failed to click:</b> " + locator + "<br><b>Element:</b> " + elementInfo;
            logger.error(msg + e.getMessage());
            if (testLog != null) {
                // FIX: Added title to the screenshot
                testLog.fail(msg,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(captureScreenshotAsBase64(), "Error Screen").build());
            }

            // Throw simple exception so the Listener log is cleaner
            throw new RuntimeException("Element Click Failed: " + locator);
        }
    }

    /**
     * Type text into an element located by the given locator with a parameter.
     * @param locator By locator of the element to type into.
     * @param text    Text to type.
     */
    public void type(By locator, String text) {
        String msg;
        WebElement element = null;
        // Get the current logger safely
        ExtentTest testLog = ExtentManager.getTest();
        try {
            element = waitForElementAndPerformAction(locator);
            for (int attempt = 0; attempt < elementFunctionReTry + 1; attempt++) {
                try {
                    logger.info("Trying to type '[" + text + "]' into element [" +locator+ "]");
                    element.clear();
                    element.sendKeys(text);
                    msg = "Successfully typed '[" + text + "]' into element [" + locator + "] ";
                    logger.info(msg);
                    if (testLog != null) testLog.pass(msg);
                    break;
                } catch (Exception e) {
                    if (attempt == elementFunctionReTry) {
                        throw new RuntimeException("Retry limit exceeded");
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            // Safely construct the message even if element is null
            String elementInfo = (element != null) ? getxpath(element) : "null";
            msg = "<b>Failed to type:</b> " + text + " into locator " + locator+"<br><b>Element:</b>"+ elementInfo;
            logger.error(msg +  e.getMessage());
            if (testLog != null) {
                // FIX: Added title to the screenshot
                testLog.fail(msg,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(captureScreenshotAsBase64(), "Error Screen").build());
            }
            // Throw simple exception so the Listener log is cleaner
            throw new RuntimeException("Element Click Failed: " + locator);
        }
    }


    public WebElement waitForElementAndPerformAction(By locator) {
        int findElementReTry = Integer.parseInt(ConfigReader.getInstance().getProperty("findElementReTryCount").toLowerCase());
        for (int attempt = 0; attempt < findElementReTry; attempt++) {
            try {
                FluentWait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
                        .withTimeout(Duration.ofSeconds(10))
                        .pollingEvery(Duration.ofSeconds(5))
                        .ignoring(NoSuchElementException.class)
                        .ignoring(StaleElementReferenceException.class);
                return wait.until(driver -> driver.findElement(locator));
            } catch (TimeoutException e) {
                if (attempt == findElementReTry - 1) {
                    throw e; // Rethrow the exception if all retries fail
                }
//                logger.warn("Attempt " + (attempt + 1) + " failed. Retrying...");
            }
        }
        throw new RuntimeException("Element not found after " + findElementReTry + " retries.");
    }

    public String getxpath(WebElement element) {
        String raw = element.toString();

        // This pattern matches everything after '->' and before the last closing bracket
        int arrowIndex = raw.indexOf("->");
        int lastBracketIndex = raw.lastIndexOf("]");

        if (arrowIndex != -1 && lastBracketIndex != -1 && arrowIndex < lastBracketIndex) {
            String locator = raw.substring(arrowIndex + 2, lastBracketIndex).trim();
            // Remove "xpath:", "id:", "css selector:" prefixes if needed
            return locator.replaceFirst("^(xpath|id|css selector):\\s*", "");
        }

        // Fallback
        return raw;
    }

    /**
     * Capture a screenshot as a Base64 string.
     * @return The Base64 string of the screenshot.
     */
    public String captureScreenshotAsBase64() {
        return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
    }

    public void startBusinessComponent(String bcName) {
        logger.info("Business Component Started: " + bcName);

        // Extent Report Log (Using HTML to make it bold and blue)
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().info("<b style='color:teal'>Business Component Started: " + bcName + "</b>");
        }
    }

    public void endBusinessComponent(String bcName) {
        logger.info("Business Component Ended: " + bcName);

        // Extent Report Log
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().info("<b style='color:teal'>Business Component Ended: " + bcName + "</b>");
        }
    }

}
