package org.alexdev.roseau.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.alexdev.roseau.util.Util;

public class Log {

	private final static String PREFIX = "ROSEAU";

	public static void startup() {

		output("", false);
		output("-----------------------------------------", false);
		output("-- SERVER BOOT TIME: " + DateTime.formatDateTime(), false);
		output("-----------------------------------------", false);
		output("", false);

		println("Roseau - Java Server");
		println("Loading server...");
		println();
	}

	private static String generateDataFormat() {
		return "[" + DateTime.formatDateTime() + "]";//DateTime.now();
	}

	public static void println() {
		output(generateDataFormat() + " [" + PREFIX + "] ");
	}

	public static void println(Object format)  {
		output(generateDataFormat() + " [" + PREFIX + "] >> " + format.toString());
	}

	private static void output(String string) {
		output(string, true);
	}

	private static void output(String string, boolean log) {

		if (log) {
			System.out.println(string);
		}

		if (Util.getConfiguration().get("Logging", "log.output", boolean.class)) {
			writeToFile("log/output.log", string);
		}

	}

	public static void exception(Exception e) {

		println("---------------------------------------------");
		println("Error has occured!");
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		output(exceptionAsString, false);
		e.printStackTrace();
		println("---------------------------------------------");

		if (Util.getConfiguration().get("Logging", "log.errors", boolean.class)) {
			writeToFile("log/error.log", "---------------------------------------------");
			writeToFile("log/error.log", " " +DateTime.formatDateTime() + " - Error has occured!");
			writeToFile("log/error.log", exceptionAsString);
		}
	}
	
	private static void writeToFile(String dir, String string) {

		File file = new File(dir);
		
		try {

			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
		} catch (Exception e1) {

		}

		try {
			if (!file.exists()) { 	
				file.createNewFile();
			}

			PrintWriter writer =  new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true)));
			writer.println(string);
			writer.flush();
			writer.close();


		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
