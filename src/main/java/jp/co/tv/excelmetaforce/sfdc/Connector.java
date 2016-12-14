package jp.co.tv.excelmetaforce.sfdc;

import java.util.List;

import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.ReadResult;
import com.sforce.soap.metadata.SaveResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

public class Connector {
    private final MetadataConnection metaConn;
    private final PartnerConnection partnerConn;
    
    public Connector(ConnectionManager manager) {
        metaConn = manager.getMetadataConnection();
        partnerConn = manager.getPartnerConnection();
    }

    /**
     * get specified metadata 
     * 
     * @param type metadata type
     * @param target target metadata list
     * @return Metadata[]
     */
    public Metadata[] readMetadata(String type, String... target) {
        try {
            ReadResult result = metaConn.readMetadata(type, target);
            return result.getRecords();
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<SaveResult> createMetadata(Metadata[] metadata) {
        return null;
    }
    
}
