package pl.softwaremill.smokedetector;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {

        validateInputParameters(args);
        BufferedReader reader = getReaderForLogFile(args);

        Multiset<SqlCommand> extractedCommands = harvestSqlCommands(reader);

        NumberOfExecutionsReportPrinter numberOfExecutionsPrinter = new NumberOfExecutionsReportPrinter(extractedCommands);
        numberOfExecutionsPrinter.print();

        Set<SqlCommand> uniqueCommands = removeDuplicatedCommands(extractedCommands);

        TableRedundancyDetector tableRedundancyDetector = new TableRedundancyDetector();
        tableRedundancyDetector.detectProblems(uniqueCommands);

        RedundantTablesReportPrinter redundantTablesReportPrinter = new RedundantTablesReportPrinter(uniqueCommands);
        redundantTablesReportPrinter.print();
    }

    private static void validateInputParameters(String[] args) {
        Preconditions.checkArgument(args.length >= 1 || args.length <= 2, "Please provide one or two parameters.\n" +
                " - 1st parameter: path to file with Hibernate log (required)\n" +
                " - 2nd parameter: \"unique\" (optional) if you want to each command appear in the report only once");
    }

    private static BufferedReader getReaderForLogFile(String[] args) throws FileNotFoundException {
        String filePath = args[0];
        if(args.length == 2 && args[1].equals("unique")) {
            boolean useExecutionTimeInComparison = false;
            SqlCommandComparator.setUseExecutionTimeInComparison(useExecutionTimeInComparison);
        }
        File file = new File(filePath);
        return Files.newReader(file, Charset.defaultCharset());
    }

    private static Multiset<SqlCommand> harvestSqlCommands(BufferedReader reader) throws IOException {
        SqlCommandHarvester harvester = new SqlCommandHarvester();

        String line;
        while((line = reader.readLine()) != null) {
            boolean newCommand = SingleLogLineAnalyser.isItStartOfNewCommand(line);
            harvester.processCommandFragment(line, newCommand);
        }
        return harvester.getCommandsInTime();
    }

    private static Set<SqlCommand> removeDuplicatedCommands(Multiset<SqlCommand> commands) {
        Set<SqlCommand> commandsWithoutDuplicates = Sets.newHashSet();

        SqlCommandComparator.setUseExecutionTimeInComparison(false);

        for (SqlCommand command : commands) {
            commandsWithoutDuplicates.add(command);
        }

        return commandsWithoutDuplicates;
    }
}
