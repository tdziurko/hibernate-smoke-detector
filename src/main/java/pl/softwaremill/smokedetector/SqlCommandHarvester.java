package pl.softwaremill.smokedetector;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.apache.commons.lang.StringUtils;

public class SqlCommandHarvester {

    private SqlCommand currentCommand;
    private Multiset<SqlCommand> commandsInTime = HashMultiset.create();

    public void processCommandFragment(String fragment, boolean newCommand) {

        if(newCommand) {
            flushLastCommand();
            currentCommand = new SqlCommand();
            currentCommand.setExecutionTime(SingleLogLineAnalyser.extractDateFromFirstSqlLine(fragment));
            int lastCharToIgnoreAsNotSqlCommand = fragment.indexOf(")");

            String cleanFragment = StringUtils.substring(fragment, lastCharToIgnoreAsNotSqlCommand + 1);
            if(cleanFragment.trim().isEmpty() == false) {
                currentCommand.appendFragment(cleanFragment + "\n");
            }
        } else {
            currentCommand.appendFragment(fragment + "\n");
        }
    }

    public void flushLastCommand() {
        if(currentCommand != null) {
            commandsInTime.add(currentCommand);
            currentCommand = null;
        }
    }

    @VisibleForTesting
    protected SqlCommand getCurrentCommand() {
        return currentCommand;
    }

    public Multiset<SqlCommand> getCommandsInTime() {
        flushLastCommand();
        return commandsInTime;
    }
}
