package pl.softwaremill.smokedetector;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) throws IOException {

        Preconditions.checkArgument(args.length >= 1 || args.length <= 2, "Please provide one or two parameters.\n" +
                " - 1st parameter: path to file with Hibernate log (required)\n" +
                " - 2nd parameter: \"unique\" (optional) if you want to each command appear in the report only once");

        String filePath = args[0];
        if(args.length == 2 && args[1].equals("unique")) {
            boolean useExecutionTimeInComparison = false;
            SqlCommandComparator.setUseExecutionTimeInComparison(useExecutionTimeInComparison);
        }
        File file = new File(filePath);
        BufferedReader reader = Files.newReader(file, Charset.defaultCharset());

        SqlCommandHarvester harvester = new SqlCommandHarvester();

        String line;
        while((line = reader.readLine()) != null) {
            boolean newCommand = SingleLogLineAnalyser.isItStartOfNewCommand(line);
            harvester.processCommandFragment(line, newCommand);
        }

        ReportPrinter reportPrinter = new ReportPrinter(harvester.getCommandsInTime());

        reportPrinter.print();
    }
}
