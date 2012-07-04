package pl.softwaremill.smokedetector;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;

public class SqlCommandHarvesterShould {

    private SqlCommandHarvester harvester;

    @BeforeMethod
    public void setup() {
        harvester = new SqlCommandHarvester();
    }

    @Test
    public void setSqlDateProperly() {

        // when
        harvester.processCommandFragment("2012-06-22 15:53:45,215 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6) ", true);

        // then
        Date date = harvester.getCurrentCommand().getExecutionTime();
        DateTime jodaDate = new DateTime(2012, 06, 22, 15, 53, 45, 0);

        assertThat(date).isEqualTo(jodaDate.toDate());
    }

    @Test
    public void appendToCurrentCommand() {

        // given
        harvester.processCommandFragment("2012-06-22 15:53:45,215 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6) ", true);

        // when
        String commandFragment = "fragment";
        harvester.processCommandFragment(commandFragment, false);

        // then
        assertThat(harvester.getCurrentCommand().getCommandString()).isEqualTo(commandFragment + "\n");
        assertThat(harvester.getCommandsInTime().size()).isEqualTo(1);
    }

    @Test
    public void appendToNewCommand() {

        // given
        harvester.processCommandFragment("2012-06-22 15:53:45,215 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6) ", true);

        // when
        String commandFragment = "2012-07-26 00:01:02,215 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6)";
        harvester.processCommandFragment(commandFragment, true);

        // then
        assertThat(harvester.getCurrentCommand().getCommandString()).isEqualTo("");
        assertThat(harvester.getCommandsInTime().size()).isEqualTo(2);
    }

    @Test
    public void createSqlCommandWithNewLines() {

        // when
        harvester.processCommandFragment("2012-06-22 15:53:45,215 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6) ", true);
        harvester.processCommandFragment("select *", false);
        harvester.processCommandFragment("from table", false);
        harvester.processCommandFragment("where id = ?", false);
        harvester.flushLastCommand();

        // then
        assertThat(harvester.getCommandsInTime().size()).isEqualTo(1);
        assertThat(harvester.getCommandsInTime().iterator().next().getCommandString()).isEqualTo("select *\nfrom table\nwhere id = ?\n");
    }

    @Test
    public void countSameCommandExecutedInTheSameSecondAsOne() {

        // when
        harvester.processCommandFragment("2012-06-22 15:53:45,215 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6) ", true);
        harvester.processCommandFragment("select *", false);
        harvester.processCommandFragment("from table", false);
        harvester.processCommandFragment("2012-06-22 15:53:45,987 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6) ", true);
        harvester.processCommandFragment("select *", false);
        harvester.processCommandFragment("from table", false);

        // then
        SqlCommand command = harvester.getCommandsInTime().iterator().next();
        assertThat(harvester.getCommandsInTime().count(command)).isEqualTo(2);
        assertThat(harvester.getCommandsInTime().elementSet().size()).isEqualTo(1);
    }

    @Test
    public void countDifferentCommandsExecutedInTheSameSecondSeparately() {

        // when
        harvester.processCommandFragment("2012-06-22 15:53:45,215 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6) ", true);
        harvester.processCommandFragment("select *", false);
        harvester.processCommandFragment("from table", false);
        harvester.processCommandFragment("2012-06-22 15:53:45,987 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6) ", true);
        harvester.processCommandFragment("select *", false);
        harvester.processCommandFragment("from table2", false);

        // then
        Iterator<SqlCommand> iterator = harvester.getCommandsInTime().iterator();
        SqlCommand commandOne = iterator.next();
        SqlCommand commandTwo = iterator.next();

        assertThat(harvester.getCommandsInTime().count(commandOne)).isEqualTo(1);
        assertThat(harvester.getCommandsInTime().count(commandTwo)).isEqualTo(1);
    }

}
