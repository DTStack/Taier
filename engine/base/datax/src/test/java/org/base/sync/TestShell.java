package org.base.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class TestShell {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		Process p = Runtime.getRuntime().exec("ssh myhost");
		PrintStream out = new PrintStream(p.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		out.println("ls -l /home/me");
		while (in.ready()) {
		  String s = in.readLine();
		  System.out.println(s);
		}
		out.println("exit");
		p.waitFor();
	}

}
