package pl.softwaremill.smokedetector;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

public class SingleLogLineAnalyserShould {

    @Test
    public void noticeNewCommandStart() {

        // given
        String line = "2012-06-22 15:53:45,215 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6)";

        // when
        boolean result = SingleLogLineAnalyser.isItStartOfNewCommand(line);

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void notNoticeNewCommandStartForPartialDate() {

        // given
        String line = "2012-06-22 DEBUG [org.hibernate.SQL] (http-127.0.0.1-8280-6)";

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
