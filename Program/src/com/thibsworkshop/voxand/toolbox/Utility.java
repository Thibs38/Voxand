package com.thibsworkshop.voxand.toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utility {


	/**
	 * Read the file specified with path with the specified encoding and returns the content in a string
	 * @param path the path of the file
	 * @param encoding the encoding to read the file with
	 * @return the content of the file
	 * @throws IOException can't read the specified file
	 */
	public static String readFile(String path, Charset encoding) throws IOException {
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return new String(encoded, encoding);
	}

	private static final String OSstring = System.getProperty("os.name").toLowerCase();
	public static final OS os = getOS();
	public static final int cores = getNumberOfCPUCores();

	public enum OS { WINDOWS, MAC, UNIX, SOLARIS, UNKNOWN}

	//<editor-fold desc="System">

	private static int getNumberOfCPUCores() {
		String command = "";
		switch(os){
			case WINDOWS ->	command = "cmd /C WMIC CPU Get /Format:List";
			case MAC -> command = "sysctl -n machdep.cpu.core_count";
			case UNIX -> command = "lscpu";
			case SOLARIS, UNKNOWN -> {
				return 2;
			}
		}

		Process process = null;
		int numberOfCores = 0;
		int sockets = 0;
		try {
			if(os == OS.MAC){
				String[] cmd = { "/bin/sh", "-c", command};
				process = Runtime.getRuntime().exec(cmd);
			}else{
				process = Runtime.getRuntime().exec(command);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String line;

		try {
			while ((line = reader.readLine()) != null) {
				switch(os){
					case WINDOWS -> {
						if (line.contains("NumberOfCores")) {
							numberOfCores = Integer.parseInt(line.split("=")[1]);
						}
					}
					case MAC -> numberOfCores = line.length() > 0 ? Integer.parseInt(line) : 0;
					case UNIX -> {
						if (line.contains("Core(s) per socket:")) {
							numberOfCores = Integer.parseInt(line.split("\\s+")[line.split("\\s+").length - 1]);
						}
						if(line.contains("Socket(s):")){
							sockets = Integer.parseInt(line.split("\\s+")[line.split("\\s+").length - 1]);
						}
						return numberOfCores * sockets;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(numberOfCores > 0 && numberOfCores < 128)
			return numberOfCores;
		else return 2;
	}

	public static OS getOS(){
		if (OSstring.contains("win")) {
			return OS.WINDOWS;
		} else if (OSstring.contains("mac")) {
			return OS.MAC;
		} else if (OSstring.contains("nix") || OSstring.contains("nux") || OSstring.contains("aix")) {
			return OS.UNIX;
		} else if (OSstring.contains("sunos")) {
			return OS.SOLARIS;
		} else {
			return OS.UNKNOWN;
		}
	}
	//</editor-fold>


}
