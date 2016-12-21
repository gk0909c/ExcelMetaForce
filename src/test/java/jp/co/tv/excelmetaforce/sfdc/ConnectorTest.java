package jp.co.tv.excelmetaforce.sfdc;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.ReadResult;
import com.sforce.soap.metadata.SaveResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class ConnectorTest {
    @Spy
    private MetadataConnection metaConn;

    @InjectMocks
    private ConnectionManager manager;
    
    @Rule
    public ExpectedException connectionException = ExpectedException.none();

    /**
     * setup test
     * 
     * @throws ConnectionException sfdc connection exception
     */
    @Before
    public void setup() throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        metaConn = new MetadataConnection(config);
        manager = spy(ConnectionManager.class);
        MockitoAnnotations.initMocks(this);

        doReturn(metaConn).when(manager).getMetadataConnection();
    }

    @Test
    public void readMetadataReturnResult() throws Exception {
        String objApi = "TestObj__c";
        CustomObject obj = new CustomObject();
        obj.setFullName(objApi);

        // mock MetadataConnection
        Metadata[] expect = new Metadata[]{obj};
        ReadResult result = new ReadResult();
        result.setRecords(expect);
        doReturn(result).when(metaConn).readMetadata(anyString(), any());
        
        // testing
        Connector connector = new Connector(manager);
        Metadata[] ret = connector.readMetadata("CustomObject", new String[]{objApi});
        assertThat(ret, is(expect));
    }
    
    @Test
    public void readMetadataFail() throws ConnectionException {
        // mock MetadataConnection
        doThrow(ConnectionException.class).when(metaConn).readMetadata(anyString(), any());
        
        // testing
        Connector connector = new Connector(manager);
        connectionException.expect(RuntimeException.class);
        connector.readMetadata("CustomObject", new String[]{"test__c"});
    }

    @Test
    public void createMetadataCalledPer10Metadata() throws Exception {
        String objApi = "TestObj__c";
        CustomObject obj = new CustomObject();
        obj.setFullName(objApi);

        // mock MetadataConnection
        SaveResult saveResult1 = new SaveResult();
        saveResult1.setSuccess(true);
        SaveResult saveResult2 = new SaveResult();
        saveResult2.setSuccess(false);
        SaveResult[] result = new SaveResult[]{saveResult1, saveResult2};
        doReturn(result).when(metaConn).createMetadata(any());
        
        // testing
        Connector connector = new Connector(manager);
        List<SaveResult> ret = connector.createMetadata(create21Objects());
        verify(metaConn, times(3)).createMetadata(any());
        assertThat(ret.size(), is(3));
    }
    
    @Test
    public void createMetadataFail() throws ConnectionException {
        // mock MetadataConnection
        doThrow(ConnectionException.class).when(metaConn).createMetadata(any());
        
        // testing
        Connector connector = new Connector(manager);
        connectionException.expect(RuntimeException.class);
        connector.createMetadata(new CustomObject[]{new CustomObject()});
    }
    
    private CustomObject[] create21Objects() {
        List<CustomObject> objectList = new ArrayList<CustomObject>();
        
        for (int i = 0; i < 22; i++) {
            CustomObject object = new CustomObject();
            objectList.add(object);
        }
        
        return objectList.toArray(new CustomObject[]{});
    }
}
