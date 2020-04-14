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
        return options;
    }


    static Configuration parseCommandLine(String[] args) {
        Options options = createOptions();
        double time = 5;
        double dt = 0.01;
        double fps = 1;
        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) printHelp(options);
            if (cmd.hasOption("h")) time = Double.parseDouble(cmd.getOptionValue("t"));
            if (cmd.hasOption("")) time = Double.parseDouble(cmd.getOptionValue("t"));
            if (cmd.hasOption("f")) time = Double.parseDouble(cmd.getOptionValue("f"));


        } catch (Exception e) {
            System.out.println("Invalid command format");
            printHelp(options);
        }
        return new Configuration(time, dt, fps);
    }


    private static void printHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Mission to Mars!", options);
        System.exit(0);
    }



}
