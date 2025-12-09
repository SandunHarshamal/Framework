package pages;

import org.openqa.selenium.By;

public enum PG_Login {
    ele_tfUserName(By.name("username")),
    ele_tfPassword(By.name("password")),
    ele_btnLogin(By.xpath("//button[normalize-space()='Login']")),
    ele_ddProfile(By.className("oxd-userdropdown-tab")),
    ele_btnLogOut(By.xpath("//a[text()='Logout']"));


    private By locator;
    private String xpathPattern;

    // Constructor for static elements
    PG_Login(By locator) {
        this.locator = locator;
    }

    // Constructor for dynamic elements
    PG_Login(String xpathPattern) {
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
