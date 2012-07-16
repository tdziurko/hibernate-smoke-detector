package pl.softwaremill.smokedetector;


/**
 * Class to compare SqlCommand before they are put in the Collection. If user wants only unique commands to be included
 * in the report

  */
public class SqlCommandComparator {

    private static boolean useExecutionTimeInComparison = true;

    public static void setUseExecutionTimeInComparison(boolean useExecutionTimeInComparison) {
        SqlCommandComparator.useExecutionTimeInComparison = useExecutionTimeInComparison;
    }

    public static boolean equals(SqlCommand first, Object other) {
        if (first == other) return true;
        if (!(other instanceof SqlCommand)) return false;

        SqlCommand that = (SqlCommand) other;

        if (first.getCommandString() != null ? !first.getCommandString().equals(that.getCommandString()) : that.getCommandString() != null) return false;
        if(useExecutionTimeInComparison) {
            if (first.getExecutionTime() != null ? !first.getExecutionTime().equals(that.getExecutionTime()) : that.getExecutionTime() != null) return false;
        }

        return true;
    }

    public static int hashCode(SqlCommand command) {
        int result = (command.getCommandString() != null ? command.getCommandString().hashCode() : 0);
        if(useExecutionTimeInComparison) {
            result = 31 * result + (command.getExecutionTime() != null ? command.getExecutionTime().hashCode() : 0);
        }
        return result;
    }


}
