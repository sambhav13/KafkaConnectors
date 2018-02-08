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
 * Created by sgu197 on 5/10/2017.
 */
public class MySourceConnector extends SourceConnector{

    public static final String TOPIC_CONFIG = "topic";
    public static final String FILE_CONFIG = "file";

    private String filename;
    private String topic;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        filename = props.get(FILE_CONFIG);
        topic = props.get(TOPIC_CONFIG);
        if (topic == null || topic=="")
            throw new ConnectException("FileStreamSourceConnector configuration must include 'topic' setting");
        if (topic.contains(","))
            throw new ConnectException("FileStreamSourceConnector should only have a single topic when used as a source.");
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MySourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<Map<String,String>>();
        // Only one input stream makes sense.
        Map<String, String> config = new HashMap<String,String>();
        if (filename != null)
            config.put(FILE_CONFIG, filename);
        config.put(TOPIC_CONFIG, topic);
        configs.add(config);
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