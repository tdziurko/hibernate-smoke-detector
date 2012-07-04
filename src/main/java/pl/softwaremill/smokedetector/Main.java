package pl.softwaremill.smokedetector;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) throws IOException {

        Preconditions.checkArgument(args.length == 1, "Please provide only one parameter: path to file with Hibernate log");

        String filePath = args[0];
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
