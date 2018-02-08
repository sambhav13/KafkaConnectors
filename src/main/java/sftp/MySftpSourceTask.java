package sftp;

import com.jcraft.jsch.*;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by sgu197 on 5/11/2017.
 */
public class MySftpSourceTask  extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(MySftpSourceTask.class);
    public static final String DIR_FIELD = "directory";
    public static final String POSITION_FIELD = "position";
    public static final String FILENAME_FIELD = "position";
    private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

    private String directory;
    private String topic = null;

    private int streamOffset;
    private Vector<ChannelSftp.LsEntry> vlist;

    private SortedMap<String, FileMetaData> loadedMap2;

    //Sftp properties
    private String host;
    private int port;
    private String user;
    private int count  = 0;


    //Sftp handle
    Session session;
    ChannelSftp channelSftp;

    List<FileMetaData> metaDataLst = new ArrayList<FileMetaData>();

    //@Override
    public String version() {
        return new MySftpSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        directory = props.get(MySftpSourceConnector.DIR_CONFIG);
        /*if (directory == null || directory=="") {
            loadedMap2 = null;
           // reader = new BufferedReader(new InputStreamReader(stream));
        }*/
        loadedMap2 = new TreeMap<String, FileMetaData>();
        topic = props.get(MySftpSourceConnector.TOPIC_CONFIG);
        host = props.get(MySftpSourceConnector.HOST_CONFIG);
        port = Integer.parseInt(props.get(MySftpSourceConnector.PORT_CONFIG));
        user = props.get(MySftpSourceConnector.USER_CONFIG);

        createChannel();
        if (topic == null)
            throw new ConnectException("FileStreamSourceTask config missing topic setting");


    }

    private void createChannel() {

        String privateKey = "/root/.ssh/id_rsa";

        JSch jsch = new JSch();
        try {

            jsch.addIdentity(privateKey);
            System.out.println("Private Key Added.");
            session = jsch.getSession(user, host, port);
            System.out.println("session created.");
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            System.out.println("Before setting session config");

            session.setConfig(config);
            session.connect();
            System.out.println("connecting session");
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            System.out.println("channel connected!!!!");
            System.out.println("The current directory is -->"+channelSftp.lpwd());
        } catch (JSchException je) {
            je.printStackTrace();
        }
    }

        /*
    @Override
    public List<SourceRecord> poll() throws InterruptedException{
        System.out.println("poller sleeping");
        Thread.sleep(10000);
        System.out.println("poller awake");

         try {
                //stream = new FileInputStream(directory);
                vlist = channelSftp.ls(directory);
                System.out.println("Polled again");

                if(loadedMap2.size()>0) {
                    //Sftp Poller
                    System.out.println("Reading previous offsets");
                    Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(DIR_FIELD, directory));
                    if (offset != null) {
                        long temp  = 0L;
                        Object lastRecordedOffset = offset.get(POSITION_FIELD);
                        if (lastRecordedOffset != null && !(lastRecordedOffset instanceof Long))
                            throw new ConnectException("Offset position is the incorrect type");
                        if (lastRecordedOffset != null) {
                            log.info("Found previous offset, trying to skip to file offset {}", lastRecordedOffset);
                            temp = (Long)lastRecordedOffset ;
                            int skipLeft = (int) temp;
                            while (skipLeft > 0) {

                                System.out.println("skipping positions"+skipLeft);
                                int skipped = loadedMap2.headMap(skipLeft).size();
                                skipped +=1;
                                loadedMap2.headMap(skipLeft).clear();
                                loadedMap2.remove(skipLeft);
                                System.out.println("cleared loaded map of previous entries with new size"+loadedMap2.size());
                                //loadedMap2 = loadedMap2.tailMap(skipLeft);
                                 System.out.println("Number of files skipped "+skipped);
                                skipLeft -= skipLeft;

                            }
                            log.info("Skipped to offset {}", lastRecordedOffset);
                        }
                        streamOffset = ((int) temp != 0) ?  (int) temp : 0;
                    } else {
                        streamOffset = 0;
                    }
                }

            } catch(SftpException se){
                se.printStackTrace();
            }
        


            ArrayList<SourceRecord> records = null;

            if (records == null)
                records = new ArrayList<SourceRecord>();

            for(int i =streamOffset;i<vlist.size();i++) {

                ChannelSftp.LsEntry v = vlist.get(i);
                if(!v.getFilename().startsWith(".")&&!v.getAttrs().isDir()) {
                    int posOffSet = streamOffset + (i + 1);
                    System.out.println("Reading File Number "+i +" with Offset "+posOffSet +" and stream offset "+streamOffset);
                    FileMetaData f = new FileMetaData(posOffSet, v.getFilename());
                    loadedMap2.put(posOffSet,f);
                    records.add(new SourceRecord(offsetKey(directory), offsetValue(posOffSet),
                            topic, VALUE_SCHEMA, f.toString()));
                }
            }

            

            return records;


    }

         */



    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        System.out.println("poller sleeping");
        Thread.sleep(5000);
        System.out.println("poller awake");

        try {
            //stream = new FileInputStream(directory);

            vlist = channelSftp.ls(directory);
            System.out.println("Polled again with vlist size: "+vlist.size());

            if (loadedMap2.size() > 0) {
                //Sftp Poller
                System.out.println("Reading previous offsets");
                Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(DIR_FIELD, directory));
                if (offset != null) {
                    System.out.println("offset not null");
                    long temp = 0L;
                    Object lastRecordedOffset = offset.get(POSITION_FIELD);
                    if (lastRecordedOffset != null && !(lastRecordedOffset instanceof Long))
                        throw new ConnectException("Offset position is the incorrect type");
                    if (lastRecordedOffset != null) {
                        log.info("Found previous offset, trying to skip to file offset {}", lastRecordedOffset);
                        temp = (Long) lastRecordedOffset;
                        int skipLeft = (int) temp;
                        while (skipLeft > 0) {

                            System.out.println("skipping positions" + skipLeft);
                            int skipped = skipLeft;
                           /* int skipped = loadedMap2.headMap(skipLeft).size();
                            skipped += 1;
                            loadedMap2.headMap(skipLeft).clear();
                            loadedMap2.remove(skipLeft);*/
                            System.out.println(
                                    "The loaded map size before skipping is "+loadedMap2.size()
                            );
                            //loadedMap2.keySet().removeAll(Arrays.asList(loadedMap2.keySet().toArray()).subList(0, skipLeft));
                            System.out.println("cleared loaded map of previous entries with new size" + loadedMap2.size());
                            //loadedMap2 = loadedMap2.tailMap(skipLeft);
                            System.out.println("Number of files skipped " + skipLeft);
                            skipLeft -= skipped;

                        }
                        log.info("Skipped to offset {}", lastRecordedOffset);
                    }
                    streamOffset = ((int) temp != 0) ? (int) temp : 0;
                } else {
                    System.out.println("offset set as null");
                    streamOffset = 0;
                }
            }

        } catch (SftpException se) {
            se.printStackTrace();
        }


        ArrayList<SourceRecord> records = null;

        if (records == null)
            records = new ArrayList<SourceRecord>();

        for (int i = 0; i < vlist.size(); i++) {

            ChannelSftp.LsEntry v = vlist.get(i);
            if (v.getFilename().startsWith(".") && v.getAttrs().isDir()) {
                 vlist.remove(i);
            }
        }
       /// int count = 0;
        System.out.println("Before loading Stream offset:" +streamOffset + "and vlist size: "+vlist.size());
        for (int i = 0; i < vlist.size(); i++) {

            ChannelSftp.LsEntry v = vlist.get(i);
            if (!v.getFilename().startsWith(".") && !v.getAttrs().isDir()) {
               // int posOffSet = streamOffset + (i + 1);
               // System.out.println("Reading File Number " + i + " with Offset " + posOffSet + " and stream offset " + streamOffset);
                if(!loadedMap2.containsKey(v.getFilename())) {
                    count++;
                    System.out.println("Reading File Number " + i + " with count " + count);

                    FileMetaData f = new FileMetaData(count, v.getFilename());

                    loadedMap2.put(v.getFilename(), f);
                    records.add(new SourceRecord(offsetKey(directory), offsetValue((long) count),
                            topic, VALUE_SCHEMA, f.toString()));
                    streamOffset = count;
                }
            }
        }


        return records;


    }

    @Override
    public void stop() {
        log.trace("Stopping");
        session.disconnect();
        channelSftp.disconnect();
    }

    private Map<String, String> offsetKey(String direcctory) {
        return Collections.singletonMap(DIR_FIELD, directory);
    }

    private Map<String, Long> offsetValue(Long pos) {
        return Collections.singletonMap(POSITION_FIELD, pos);
    }
}




