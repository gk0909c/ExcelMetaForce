package jp.co.tv.excelmetaforce.sfdc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.ReadResult;
import com.sforce.soap.metadata.SaveResult;
import com.sforce.ws.ConnectionException;


public class Connector {
    private final MetadataConnection metaConn;
    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class);
    
    public Connector(ConnectionManager manager) {
        metaConn = manager.getMetadataConnection();
    }

    /**
     * get specified metadata 
     * 
     * @param type metadata type
     * @param target target metadata list
     * @return Metadata[]
     */
    public Metadata[] readMetadata(String type, String... target) {
        LOGGER.info(String.format("read metadata %s", type));
        
        try {
            ReadResult result = metaConn.readMetadata(type, target);
            return result.getRecords();
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * create metadata on Salesforce
     * 
     * @param metadata metadata array
     * @return error results
     */
    public List<SaveResult> createMetadata(Metadata... metadata) {
        Function<Metadata[], SaveResult[]> func = list -> {
            try {
                return metaConn.createMetadata(list);
            } catch (ConnectionException e) {
                throw new RuntimeException(e);
            }
        };
        
        return saveMetadata(metadata, func);
    }
    
    /**
     * update metadata on Salesforce
     * 
     * @param metadata metadata array
     * @return error results
     */
    public List<SaveResult> updateMetadata(Metadata... metadata) {
        Function<Metadata[], SaveResult[]> func = list -> {
            try {
                return metaConn.updateMetadata(list);
            } catch (ConnectionException e) {
                throw new RuntimeException(e);
            }
        };
        
        return saveMetadata(metadata, func);
    }
    
    /**
     * specify create or update in func argument
     */
    private List<SaveResult> saveMetadata(Metadata[] metadata, Function<Metadata[], SaveResult[]> func) {
        List<SaveResult> errorMessage = new ArrayList<SaveResult>();          
        List<List<Metadata>> partitionedMetadata = ListUtils.partition(Arrays.asList(metadata), 10); 

        for (int i = 0 ; i < partitionedMetadata.size(); i++) {
            SaveResult[] save = func.apply(partitionedMetadata.get(i).toArray(new Metadata[]{}));

            for (SaveResult s : save) {
                if (!(s.isSuccess())) {
                    errorMessage.add(s);
                }
            }
        }

        return errorMessage;
    }
}
