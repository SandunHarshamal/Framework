package TestSuites;

import libraries.LIB_Common;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ExcelUtils;

import java.lang.reflect.Method;
import java.util.Map;

public class TS_001 {
    LIB_Common lIB_Common = new LIB_Common();

    @Test(dataProvider = "loginData",description = "Verify Valid Login1")
    public void tc_login1(Map<String, String> testData){
        lIB_Common.bc_login(testData.get("Url"),testData.get("Username"), testData.get("Password"));
        lIB_Common.bc_logOut();
    }

    @Test(dataProvider = "loginData",description = "Verify Valid Login2")
    public void tc_login2(Map<String, String> testData){
        lIB_Common.bc_login(testData.get("Url"),testData.get("Username"), testData.get("Password"));
        lIB_Common.bc_logOut();
    }

    @AfterMethod
    public void closeBrowser() {
        // You need to expose the tearDown method in your LIB_Common or call Commands directly
        // Assuming you add a wrapper in LIB_Common:
        lIB_Common.closeBrowser();
    }

//    @DataProvider(name = "loginData")
//    public Object[][] getLoginData() {
//        // CALL THE NEW METHOD
//        return ExcelUtils.getExcelDataAsMapForIteration( "LoginData");
//    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData(Method method) {
        // 1. Get the name of the method calling this data provider (e.g., "tc_001")
        String testCaseName = method.getName();

        // 2. Ask ExcelUtils to fetch rows where 'Test Case' column == "tc_001"
        return ExcelUtils.getExcelDataAsMap("LoginData", testCaseName);
    }



}
