package pl.softwaremill.smokedetector;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class SqlCommand {

    private Date executionTime;
    private String command;
    private List<String> redundantTables = Lists.newArrayList();

    public SqlCommand() {
        command = "";
    }

    public Date getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Date executionTime) {
        this.executionTime = executionTime;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandString() {
        return command;
    }

    public void appendFragment(String fragment) {
        this.command = command + fragment;
    }

    public void setRedundantTables(List<String> redundantTables) {
        this.redundantTables = redundantTables;
    }

    public List<String> getRedundantTables() {
        return redundantTables;
    }

    @Override
    public boolean equals(Object o) {
        return SqlCommandComparator.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SqlCommandComparator.hashCode(this);
    }
}
