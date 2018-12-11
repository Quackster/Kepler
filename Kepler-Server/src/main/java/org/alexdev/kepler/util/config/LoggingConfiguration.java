package org.alexdev.kepler.util.config;

import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class LoggingConfiguration {
    /**
     * Create the configuration files for this application, with the default values. Will throw an
     * exception if it could not create such files.
     *
     * @throws FileNotFoundException the exception if an error happens
     */
    public static void checkLoggingConfig() throws FileNotFoundException {
        String output = "log4j.rootLogger=INFO, stdout, SERVER_LOG\n" +
                "log4j.appender.stdout.threshold=info\n" +
                "log4j.appender.stdout=org.apache.log4j.ConsoleAppender\n" +
                "log4j.appender.stdout.Target=System.out\n" +
                "log4j.appender.stdout.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd'T'HH:mm:ss.SSS} %-5p [%c] - %m%n\n" +
                "\n" +
                "# Create new logger information for error\n" +
                "log4j.logger.ErrorLogger=ERROR, error, ERROR_FILE\n" +
                "log4j.additivity.ErrorLogger=false\n" +
                "\n" +
                "# Set settings for the error logger\n" +
                "log4j.appender.error=org.apache.log4j.ConsoleAppender\n" +
                "log4j.appender.error.Target=System.err\n" +
                "log4j.appender.error.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.error.layout.ConversionPattern=%d{yyyy-MM-dd'T'HH:mm:ss.SSS} %-5p [%c] - %m%n\n" +
                "\n" +
                "# Define the file appender for errors\n" +
                "log4j.appender.ERROR_FILE=org.apache.log4j.FileAppender\n" +
                "log4j.appender.ERROR_FILE.File=error.log\n" +
                "log4j.appender.ERROR_FILE.ImmediateFlush=true\n" +
                "log4j.appender.ERROR_FILE.Threshold=debug\n" +
                "log4j.appender.ERROR_FILE.Append=true\n" +
                "log4j.appender.ERROR_FILE.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.ERROR_FILE.layout.conversionPattern=%d{yyyy-MM-dd'T'HH:mm:ss.SSS} - [%c] - %m%n\n" +
                "\n" +
                "# Define the file appender for server output\n" +
                "log4j.appender.SERVER_LOG=org.apache.log4j.FileAppender\n" +
                "log4j.appender.SERVER_LOG.File=server.log\n" +
                "log4j.appender.SERVER_LOG.ImmediateFlush=true\n" +
                "log4j.appender.SERVER_LOG.Threshold=debug\n" +
                "log4j.appender.SERVER_LOG.Append=true\n" +
                "log4j.appender.SERVER_LOG.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.SERVER_LOG.layout.conversionPattern=%d{yyyy-MM-dd'T'HH:mm:ss.SSS} - [%c] - %m%n\n";

        File loggingConfig = new File("log4j.properties");

        if (!loggingConfig.exists()) {
            PrintWriter writer = new PrintWriter(loggingConfig.getAbsoluteFile());
            writer.write(output);
            writer.flush();
            writer.close();
        }

        //Change the path where the logger property should be read from
        PropertyConfigurator.configure(loggingConfig.getAbsolutePath());
    }
}
