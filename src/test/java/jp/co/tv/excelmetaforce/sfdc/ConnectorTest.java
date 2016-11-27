package jp.co.tv.excelmetaforce.sfdc;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.ReadResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class ConnectorTest {
    @Spy
    private MetadataConnection metaConn;

    @InjectMocks
    private ConnectionManager manager;

    /**
     * setup test
     * 
     * @throws ConnectionException sfdc connection exception
     */
    @Before
    public void setup() throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        metaConn = new MetadataConnection(config);
        manager = mock(ConnectionManager.class);
        MockitoAnnotations.initMocks(this);

        doReturn(metaConn).when(manager).getMetadataConnection();
    }

    @Test
    public void testReadMetadata() throws Exception {
        String objApi = "TestObj__c";
        CustomObject obj = new CustomObject();
        obj.setFullName(objApi);

        Metadata[] expect = new Metadata[]{obj};
        ReadResult result = new ReadResult();
        result.setRecords(expect);
        doReturn(result).when(metaConn).readMetadata(anyString(), any());
        
        Connector connector = new Connector(manager);
        Metadata[] ret = connector.readMetadata("CustomObject", new String[]{objApi});
        assertThat(ret, is(expect));
    }
}
