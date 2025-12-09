package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class DriverManager {

    // 1. Private Static Instance (Singleton)
    private static DriverManager instance = null;

    // 2. ThreadLocal for Parallel Execution Support
    private static ThreadLocal<WebDriver> thDriver = new ThreadLocal<>();

    // 3. Private Constructor
    private DriverManager() {
    }

    // 4. Global Access Point
    public static DriverManager getInstance() {
        if (instance == null) {
            instance = new DriverManager();
        }
        return instance;
    }

    // Factory Logic to initialize driver
    public void initDriver() {
        // Only initialize if it's null for this thread
        if (thDriver.get() == null) {
            String browser = ConfigReader.getInstance().getProperty("browser");
            WebDriver driver; // Local variable only

            if (browser.equalsIgnoreCase("chrome")) {
                driver = new ChromeDriver();
            } else if (browser.equalsIgnoreCase("firefox")) {
                driver = new FirefoxDriver();
            } else {
                throw new RuntimeException("Browser not supported!");
            }
            thDriver.set(driver);
        }
    }

    public static WebDriver getDriver() {
        return thDriver.get();
    }

    public static void quitDriver() {
        if (thDriver.get() != null) {
            thDriver.get().quit();
            thDriver.remove();
        }
    }
}