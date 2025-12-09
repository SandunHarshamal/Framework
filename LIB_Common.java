package libraries;

import pages.PG_Common;
import pages.PG_Login;
import testBase.Commands;

public class LIB_Common {
    Commands commands = new Commands();

    public void bc_login(String url, String userName, String password){
        try {
            commands.startBusinessComponent("bc_login");
            commands.open(url);
            commands.type(PG_Login.ele_tfUserName.getLocator(),userName);
            commands.type(PG_Login.ele_tfPassword.getLocator(),password);
            commands.click(PG_Login.ele_btnLogin.getLocator());
            commands.endBusinessComponent("bc_login");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void bc_logOut(){
        try {
            commands.startBusinessComponent("bc_logOut");
            commands.click(PG_Login.ele_ddProfile.getLocator());
            commands.click(PG_Login.ele_btnLogOut.getLocator());
            commands.endBusinessComponent("bc_logOut");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeBrowser() {
        commands.quitBrowser();
    }
}
