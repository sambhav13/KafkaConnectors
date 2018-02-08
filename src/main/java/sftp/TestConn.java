package sftp;

import com.jcraft.jsch.*;

/**
 * Created by sgu197 on 2/8/2018.
 */
public class TestConn {



    public static void main(String[] args){
        //String privateKey = "C:\\Users\\sgu197\\id_rsa";
        String privateKey = "/root/.ssh/id_rsa";

        Session session;
        ChannelSftp channelSftp;
        JSch jsch = new JSch();
        try {

            jsch.addIdentity(privateKey);
            System.out.println("Private Key Added.");
            session = jsch.getSession("root", "127.0.0.1", 22);
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
            try {
                System.out.println("The current directory is -->"+channelSftp.ls("/home/"));
            } catch (SftpException e) {
                e.printStackTrace();
            }
        } catch (JSchException je) {
            je.printStackTrace();
        }
    }
}
