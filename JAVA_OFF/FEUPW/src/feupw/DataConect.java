/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feupw;

/**
 *
 * @author Washington
 */
public class DataConect {
    
    
    private String server;
    private String base;
    private String logid;
    private String password;
    
    public DataConect(){}

    public String getBase() {
        return base;
    }

    public String getPassword() {
        return password;
    }

    public String getLogid() {
        return logid;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setLogid(String logid) {
        this.logid = logid;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
}
