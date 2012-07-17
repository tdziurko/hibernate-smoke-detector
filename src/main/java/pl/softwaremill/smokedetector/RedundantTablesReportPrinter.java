package pl.softwaremill.smokedetector;

import com.google.common.base.Joiner;

import java.util.Set;

public class RedundantTablesReportPrinter {

    private Set<SqlCommand> commands;

    public RedundantTablesReportPrinter(Set<SqlCommand> commands) {
        this.commands = commands;
    }

    public void print() {

        System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        System.out.println("Queries that may have redundant table names present both in 'where' and 'join' section of sql\n");

        for (SqlCommand command : commands) {
            if(command.getRedundantTables().size() > 0) {
                System.out.println("Redundant tables: " + Joiner.on(", ").join(command.getRedundantTables()));
                System.out.println("Time: " + SingleLogLineAnalyser.formatter.format(command.getExecutionTime()));
                System.out.println("Command:");
                System.out.println(command.getCommandString());
                System.out.println("------------------------------------------------");
            }
        }
    }
}
