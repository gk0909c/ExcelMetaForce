package jp.co.tv.excelmetaforce.sfdc;

import java.io.FileInputStream;

import org.yaml.snakeyaml.Yaml;

import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;

public class ConnectionManager {
    private static PartnerConnection partnerConn;
    private static MetadataConnection metaConn;
    
    public MetadataConnection getMetadataConnection() {
        checkConnection();
        return metaConn;
    }
    
    public PartnerConnection getPartnerConnection() {
        checkConnection();
        return partnerConn;
    }
    
    private void checkConnection() {
        synchronized (Connector.class) {
            if (metaConn == null) {
                try {
                    //get connetion info
                    Yaml yaml = new Yaml();
                    SfdcConnectionInfo connectionInfo = 
                            yaml.loadAs(new FileInputStream("sfdc_connection_info.yml"), SfdcConnectionInfo.class);
                    
                    // get connection
                    ConnectorConfig partnerConfig = new ConnectorConfig();
                    partnerConfig.setAuthEndpoint(connectionInfo.getPartnerUri());
                    partnerConfig.setServiceEndpoint(connectionInfo.getPartnerUri());
                    partnerConfig.setManualLogin(true);
                    setProxy(partnerConfig, connectionInfo);

                    PartnerConnection connection = new PartnerConnection(partnerConfig);        
                    LoginResult loginResult = connection.login(connectionInfo.getUsername(),
                                  connectionInfo.getPassword() + connectionInfo.getSecurityToken());        

                    ConnectorConfig metadataConfig = createConnectorConfig(connectionInfo, loginResult);
                    metadataConfig.setServiceEndpoint(loginResult.getMetadataServerUrl());
                    metaConn = new MetadataConnection(metadataConfig);
                    
                    ConnectorConfig loginConfig = createConnectorConfig(connectionInfo, loginResult);
                    loginConfig.setServiceEndpoint(loginResult.getServerUrl());
                    partnerConn = new PartnerConnection(loginConfig);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private ConnectorConfig createConnectorConfig(
            SfdcConnectionInfo connectionInfo, 
            LoginResult loginResult) {

        ConnectorConfig config = new ConnectorConfig();
        config.setSessionId(loginResult.getSessionId());
        setProxy(config, connectionInfo);

        return config;    
    }

    private void setProxy(ConnectorConfig config, SfdcConnectionInfo connectionInfo) {
        if (connectionInfo.getProxyHost() != null ) {
            config.setProxy(connectionInfo.getProxyHost(), connectionInfo.getProxyPort());
        }
        
        if (connectionInfo.getProxyUser() != null ) {
            config.setProxyUsername(connectionInfo.getProxyUser());
            config.setProxyPassword(connectionInfo.getProxyPass());
        }
    }
    
    class SfdcConnectionInfo {
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

}
