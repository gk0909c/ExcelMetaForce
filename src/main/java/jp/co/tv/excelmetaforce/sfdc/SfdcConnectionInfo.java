package jp.co.tv.excelmetaforce.sfdc;

public class SfdcConnectionInfo {
    private String username;
    private String password;
    private String securityToken;
    private String partnerUri;
    private String proxyHost;
    private int proxyPort;
    private String proxyUser;
    private String proxyPass;

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSecurityToken() {
        return securityToken;
    }
    
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
    
    public String getPartnerUri() {
        return partnerUri;
    }
    
    public void setPartnerUri(String partnerUri) {
        this.partnerUri = partnerUri;
    }
    
    public String getProxyHost() {
        return proxyHost;
    }
    
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    public int getProxyPort() {
        return proxyPort;
    }
    
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    public String getProxyUser() {
        return proxyUser;
    }
    
    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }
    
    public String getProxyPass() {
        return proxyPass;
    }
    
    public void setProxyPass(String proxyPass) {
        this.proxyPass = proxyPass;
    }

}
