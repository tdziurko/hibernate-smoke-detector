package pl.softwaremill.smokedetector;

public class SqlCommandWithCount implements Comparable<SqlCommandWithCount>{

    private SqlCommand command;
    private Integer count;

    public SqlCommandWithCount(SqlCommand command, int count) {
        this.command = command;
        this.count = count;
    }


    @Override
    public int compareTo(SqlCommandWithCount o) {
        return count.compareTo(o.count);
    }

    public SqlCommand getCommand() {
        return command;
    }

    public Integer getCount() {
        return count;
    }
}
