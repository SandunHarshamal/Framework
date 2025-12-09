package pages;

import org.openqa.selenium.By;

public enum PG_Common {
    // 1. Static element (Keep as By)
    ele_btnSubmit(By.id("submitBtn")),

    // 2. Dynamic element (Store as String Pattern with %s)
    // We do not wrap this in By.xpath() yet!
    ele_btnCommon("//button[normalize-space()='%s']");

    private By locator;
    private String xpathPattern;

    // Constructor for static elements
    PG_Common(By locator) {
        this.locator = locator;
    }

    // Constructor for dynamic elements
    PG_Common(String xpathPattern) {
        this.xpathPattern = xpathPattern;
    }

    public By getLocator() {
        return locator;
    }

    // New method to generate dynamic locator on the fly
    public By getDynamicLocator(String... replacements) {
        return By.xpath(String.format(xpathPattern, (Object[]) replacements));
    }
}
