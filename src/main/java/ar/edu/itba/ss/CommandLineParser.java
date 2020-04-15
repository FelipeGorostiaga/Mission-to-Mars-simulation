package ar.edu.itba.ss;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class CommandLineParser {

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "Show help menu");
        options.addOption("t", "time", true, "Time to run simulation");
        options.addOption("f", "frames", true, "Frames per second for simulation");
        options.addOption("dt", "delta-time", true, "Time interval value");
        options.addOption("df", "file", true, "Dynamic file location");
        return options;
    }

    static Configuration parseCommandLine(String[] args) {
        Options options = createOptions();
        double time = 0, dt = 0, fps = 0;
        String dynamicFilePath = null;
        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) printHelp(options);
            if (cmd.hasOption("f")) fps = Double.parseDouble(cmd.getOptionValue("f"));
            if (cmd.hasOption("t")) time = Double.parseDouble(cmd.getOptionValue("t"));
            if (cmd.hasOption("dt")) dt = Double.parseDouble(cmd.getOptionValue("dt"));
            if (cmd.hasOption("df")) dynamicFilePath = cmd.getOptionValue("df");
        } catch (Exception e) {
            System.out.println("Invalid command format");
            printHelp(options);
        }
        return new Configuration(time, dt, fps, dynamicFilePath);
    }


    private static void printHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Mission to Mars!", options);
        System.exit(0);
    }



}
