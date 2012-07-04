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
            if(count > 1) {
                sortedCommands.add(new SqlCommandWithCount(sqlCommand, count));
            }
        }

        Collections.sort(sortedCommands);
        Collections.reverse(sortedCommands);
    }

    public void print() {
        System.out.println("Report contains all sql commands that were executed more than one time in the same second\n");

        for (SqlCommandWithCount command : sortedCommands) {
            System.out.println("Number of executions: " + command.getCount());
            System.out.println("Command:");
            System.out.println(command.getCommand().getCommandString());
            System.out.println("Time: " + command.getCommand().getExecutionTime());
            System.out.println("------------------------------------------------");
        }
    }


}
