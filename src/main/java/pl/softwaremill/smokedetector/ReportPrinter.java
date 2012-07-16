package pl.softwaremill.smokedetector;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import java.util.Collections;
import java.util.List;

public class ReportPrinter {
    private List<SqlCommandWithCount> sortedCommands;

    public ReportPrinter(Multiset<SqlCommand> commandsInTime) {
        sortedCommands = Lists.newArrayList();

        for (SqlCommand sqlCommand : commandsInTime.elementSet()) {
            int count = commandsInTime.count(sqlCommand);
            sortedCommands.add(new SqlCommandWithCount(sqlCommand, count));
        }

        Collections.sort(sortedCommands);
        Collections.reverse(sortedCommands);
    }

    public void print() {
        for (SqlCommandWithCount command : sortedCommands) {
            System.out.println("Number of executions: " + command.getCount());
            System.out.println("Time: " + SingleLogLineAnalyser.formatter.format(command.getCommand().getExecutionTime()));
            System.out.println("Command:");
            System.out.println(command.getCommand().getCommandString());
            System.out.println("------------------------------------------------");
        }
    }


}
