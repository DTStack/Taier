package com.dtstack.rdos.common.ssh;

import java.io.IOException;
import java.io.InputStream;

import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.common.util.LocalIpAddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;


/**
 * @author sishu.yss
 */
public class SSHClient {

    private Logger logger = LoggerFactory.getLogger(SSHClient.class);

    private String userName;

    private String password;

    private String serverIp;

    private int port = 22;

    public SSHClient(String userName, String password, String serverIp) {
        this.userName = userName;
        this.password = password;
        this.serverIp = serverIp;
    }

    public SSHClient(String userName, String password, String serverIp, int port) {
        this.userName = userName;
        this.password = password;
        this.serverIp = serverIp;
        this.port = port;
    }

    public String ssh(String command) throws Exception {
        StringBuffer sb = new StringBuffer();
        try {
            Connection conn = new Connection(serverIp, port);
            conn.connect();
            boolean isAuthenticated = false;
            if (StringUtils.isNotBlank(password)) {
                isAuthenticated = conn.authenticateWithPassword(userName, password);
            } else {
                isAuthenticated = conn.authenticateWithNone(userName);
            }
            if (isAuthenticated == false)
                throw new IOException("Authentication failed...");

            ch.ethz.ssh2.Session sess = conn.openSession();
            sess.execCommand(command);

            InputStream stdout = sess.getStdout();
            InputStream stderr = sess.getStderr();

            byte[] buffer = new byte[8192];

            while (true) {
                if ((stdout.available() == 0) && (stderr.available() == 0)) {
                    int conditions = sess.waitForCondition(ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA
                            | ChannelCondition.EOF, 1000 * 5);

                    /* Wait no longer than 5 seconds (= 5000 milliseconds) */
                    if ((conditions & ChannelCondition.TIMEOUT) != 0) {
                        logger.info("Timeout while waiting for data from peer.");
                        break;
                    }

                    /* Here we do not need to check separately for CLOSED, since CLOSED implies EOF */
                    if ((conditions & ChannelCondition.EOF) != 0) {
                        logger.info("... The remote side won't send us further data...");
                        if ((conditions & (ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA)) == 0) {
                            logger.info("... and we have consumed all data in the local arrival window.");
                            break;
                        }
                    }
                }

                while (stdout.available() > 0) {
                    int len = stdout.read(buffer);
                    if (len > 0) // this check is somewhat paranoid
                        sb.append(new String(buffer, 0, len));
                }

                while (stderr.available() > 0) {
                    int len = stderr.read(buffer);
                    if (len > 0) // this check is somewhat paranoid
                        sb.append(new String(buffer, 0, len));
                }
            }

            sess.close();
            conn.close();
        } catch (IOException e) {
            logger.error("", e);
        }

        return sb.toString();
    }


    public static void main(String[] args) throws Exception {


        String ip = LocalIpAddressUtil.getLocalAddress() + ":9020";
        String jobid = "1";
        String jobUrl = "http://" + ip + "/api/batch/batchTask/1";
        String str = PoolHttpClient.get(jobUrl);
        System.out.println(str);

        String command = " python dataxnew.py " + jobUrl + " --rip " + ip + " --jobid " + jobid;
        System.out.println(command);
        System.out.println(new SSHClient("root", "abc123", "172.16.1.155", 22).ssh("cd /opt/dtstack/datax/bin && nohup " + command + " &"));
    }

}
