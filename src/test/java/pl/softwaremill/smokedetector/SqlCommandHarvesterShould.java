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
    public void setSqlExecutionTimeProperly() {

        // when
        harvester.processCommandFragment("15:53:45,215 DEBUG [org.hibernate.SQL]", true);

        // then
        DateTime date = new DateTime(harvester.getCurrentCommand().getExecutionTime());

        DateTime expectedTime = new DateTime(new Date().getTime()).withTime(15, 53, 45, 0);

        assertThat(date.getMillisOfDay()).isEqualTo(expectedTime.getMillisOfDay());
    }

    @Test
    public void appendToCurrentCommand() {

        // given
        harvester.processCommandFragment("15:53:45,215 DEBUG [org.hibernate.SQL]", true);

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
        harvester.processCommandFragment("15:53:45,215 DEBUG [org.hibernate.SQL]", true);

        // when
        String commandFragment = "00:01:02,215 DEBUG [org.hibernate.SQL]";
        harvester.processCommandFragment(commandFragment, true);

        // then
        assertThat(harvester.getCurrentCommand().getCommandString()).isEqualTo("");
        assertThat(harvester.getCommandsInTime().size()).isEqualTo(2);
    }

    @Test
    public void createSqlCommandWithNewLines() {

        // when
        harvester.processCommandFragment("15:53:45,215 DEBUG [org.hibernate.SQL]", true);
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
        harvester.processCommandFragment("15:53:45,215 DEBUG [org.hibernate.SQL]", true);
        harvester.processCommandFragment("select *", false);
        harvester.processCommandFragment("from table", false);
        harvester.processCommandFragment("15:53:45,987 DEBUG [org.hibernate.SQL]", true);
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
        harvester.processCommandFragment("15:53:45,215 DEBUG [org.hibernate.SQL]", true);
        harvester.processCommandFragment("select *", false);
        harvester.processCommandFragment("from table", false);
        harvester.processCommandFragment("15:53:45,987 DEBUG [org.hibernate.SQL]", true);
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
