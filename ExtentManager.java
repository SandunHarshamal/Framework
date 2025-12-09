package utils;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;
    // ThreadLocal is the secret sauce here!
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // Initialize the Report File (Run once at start of Suite)
    public static ExtentReports createInstance() {
        if (extent == null) {
            String fileName = System.getProperty("user.dir") + "/test-output/ExtentReport.html";
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(fileName);

            htmlReporter.config().setTheme(Theme.DARK);
            htmlReporter.config().setDocumentTitle("Automation Report");
            htmlReporter.config().setReportName("Test Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            extent.setSystemInfo("Tester", "Sandun");
            extent.setSystemInfo("Environment", "QA");
        }
        return extent;
    }

    // Create a test for the current thread
    public static void createTest(String testName) {
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest); // Save to ThreadLocal
    }

    // Get the test for the current thread (Commands class will use this)
    public static ExtentTest getTest() {
        return test.get();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
