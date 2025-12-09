package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    // Initialize Logger
    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.createInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Automatically starts the log with the Method Name
        ExtentManager.createTest(result.getMethod().getMethodName());
        ExtentManager.getTest().info("Test Started: " + result.getMethod().getMethodName());
        logger.info("Start of Test Case: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentManager.getTest().fail("Test Failed: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    // Implement onTestSuccess, onTestSkipped similarly if needed
}