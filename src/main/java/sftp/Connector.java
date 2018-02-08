package sftp;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgu197 on 5/16/2017.
 */
public class Connector extends SourceConnector{

    public static final String TOPIC_CONFIG = "topic";
    public static final String[] DIR_CONFIG = {"directory1","directory2","directory3","directory4"};
    public static final String DIR = "directory";

    public static final String HOST_CONFIG = "host";
    public static final String PORT_CONFIG = "port";
    public static final String USER_CONFIG = "user";


    private String directory[] = new String[4];


    private String topic;
    private String host;
    private String port;
    private String user;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        directory[0] = props.get(DIR_CONFIG[0]);
        directory[1] = props.get(DIR_CONFIG[1]);
        directory[2] = props.get(DIR_CONFIG[2]);
        directory[3] = props.get(DIR_CONFIG[3]);


        topic = props.get(TOPIC_CONFIG);
        host = props.get(HOST_CONFIG);
        port = props.get(PORT_CONFIG);
        user = props.get(USER_CONFIG);
        if (topic == null || topic=="")
            throw new ConnectException("FileStreamSourceConnector configuration must include 'topic' setting");
        if (topic.contains(","))
            throw new ConnectException("FileStreamSourceConnector should only have a single topic when used as a source.");
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MySftpSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<Map<String,String>>();
        // Only one input stream makes sense.

        for(int i=0;i<4;i++){
        Map<String, String> config = new HashMap<String,String>();

                config.put(DIR, directory[i]);
            config.put(TOPIC_CONFIG, topic);
            config.put(HOST_CONFIG, host);
            config.put(PORT_CONFIG, port);
            config.put(USER_CONFIG, user);
            configs.add(config);
        }
        return configs;
    }

    @Override
    public void stop() {
        // Nothing to do since FileStreamSourceConnector has no background monitoring.
    }

    public ConfigDef config() {
        return null;
    }
}

