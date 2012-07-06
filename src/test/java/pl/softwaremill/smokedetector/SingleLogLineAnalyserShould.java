package pl.softwaremill.smokedetector;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

public class SingleLogLineAnalyserShould {

    @Test
    public void noticeNewCommandStart() {

        // given
        String line = "14:10:44,714 DEBUG [org.hibernate.SQL]";

        // when
        boolean result = SingleLogLineAnalyser.isItStartOfNewCommand(line);

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void notNoticeNewCommandStartForPartialDate() {

        // given
        String line = "14:10 DEBUG [org.hibernate.SQL]";

        // when
        boolean result = SingleLogLineAnalyser.isItStartOfNewCommand(line);

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void notNoticeNewCommandStartForSqlCommandFragment() {

        // given
        String line = "cessionofs0_1_.base_document_status as base3_7_29_,";

        // when
        boolean result = SingleLogLineAnalyser.isItStartOfNewCommand(line);

        // then
        assertThat(result).isFalse();
    }
}
