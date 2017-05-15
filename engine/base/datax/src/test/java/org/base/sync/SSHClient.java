package org.base.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.StreamGobbler;

public class SSHClient {
	
	
	public static void ssh(String serverIp,String command, String usernameString,String password) throws IOException{
        System.out.println("inside the ssh function");
        try
        {
            Connection conn = new Connection(serverIp);
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(usernameString, password);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");        
            ch.ethz.ssh2.Session sess = conn.openSession();
            sess.execCommand("cd /opt/dtstack;ls"); 
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            System.out.println("the output of the command is");
            while (true)
            {
                String line = br.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            }
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close();
            conn.close();
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);

        }
    }
	
	public static void main(String[] args) throws IOException{
		ssh("172.16.1.155","ls","root","abc123");
	}

}
