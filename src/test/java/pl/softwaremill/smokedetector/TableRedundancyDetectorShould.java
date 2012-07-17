package pl.softwaremill.smokedetector;

import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class TableRedundancyDetectorShould {

    TableRedundancyDetector detector = new TableRedundancyDetector();

    @Test
    public void returnNullWhenFromKeywordNotFound() {
        // Given
        final String query =" select * ";

        // When
        String extractedFragment = detector.extractTablesDeclaration(query);

        // Then
        assertThat(extractedFragment).isNull();
    }

    @Test
    public void extractTablesDeclarationFromQuery() {
        // Given
        String query =" select * from footballers f left outer join matches m on f.last_match_id = m.id where f.id = ?";

        // When
        String extractedFragment = detector.extractTablesDeclaration(query);

        // Then
        assertThat(extractedFragment).isEqualTo("footballers f left outer join matches m on f.last_match_id = m.id");
    }

    @Test
    public void extractTablesDeclarationFromQueryWithoutWhere() {
        // Given
        String query =" select * from footballers f left outer join matches m on f.last_match_id = m.id";

        // When
        String extractedFragment = detector.extractTablesDeclaration(query);

        // Then
        assertThat(extractedFragment).isEqualTo("footballers f left outer join matches m on f.last_match_id = m.id");
    }

    @Test
    public void extractTablesDeclarationFromQueryWithOrderBy() {
        // Given
        String query =" select * from footballers f left outer join matches m on f.last_match_id = m.id order by f.name limit 10";

        // When
        String extractedFragment = detector.extractTablesDeclaration(query);

        // Then
        assertThat(extractedFragment).isEqualTo("footballers f left outer join matches m on f.last_match_id = m.id");
    }

    @Test
    public void extractTablesDeclarationFromQueryWithLimit() {
        // Given
        String query =" select * from footballers f left outer join matches m on f.last_match_id = m.id limit 10";

        // When
        String extractedFragment = detector.extractTablesDeclaration(query);

        // Then
        assertThat(extractedFragment).isEqualTo("footballers f left outer join matches m on f.last_match_id = m.id");
    }

    @Test
    public void extractTablesDeclarationFromQueryWithFetchFirst() {
        // Given
       String query =" select * from footballers f left outer join matches m on f.last_match_id = m.id fetch first 10";

        // When
        String extractedFragment = detector.extractTablesDeclaration(query);

        // Then
        assertThat(extractedFragment).isEqualTo("footballers f left outer join matches m on f.last_match_id = m.id");
    }

    @Test
    public void extractOneTablesInFromSection() {
        // Given
        String tablesDeclaration ="footballers f left outer join matches m on f.last_match_id = m.id";

        // When
        List<String> tablesInFromSection = detector.extractTablesInFromSection(tablesDeclaration);

        // Then
        assertThat(tablesInFromSection).isNotEmpty().hasSize(1);
        assertThat(tablesInFromSection).contains("footballers");
    }

    @Test
    public void extractTwoTablesInFromSection() {
        // Given
        String tablesDeclaration ="footballers f, referees r left outer join matches m on f.last_match_id = m.id";

        // When
        List<String> tablesInFromSection = detector.extractTablesInFromSection(tablesDeclaration);

        // Then
        assertThat(tablesInFromSection).isNotEmpty().hasSize(2);
        assertThat(tablesInFromSection).contains("footballers", "referees");
    }

    @Test
    public void extractTwoTablesInFromSectionWithJoinFragmentBetween() {
        // Given
        String tablesDeclaration ="footballers f left outer join matches m on f.last_match_id = m.id, referees r ";

        // When
        List<String> tablesInFromSection = detector.extractTablesInFromSection(tablesDeclaration);

        // Then
        assertThat(tablesInFromSection).isNotEmpty().hasSize(2);
        assertThat(tablesInFromSection).contains("footballers", "referees");
    }

    @Test
    public void extractThreeTablesInFromSection() {
        // Given
        String tablesDeclaration ="footballers f left outer join matches m on f.last_match_id = m.id, referees r, managers mm";

        // When
        List<String> tablesInFromSection = detector.extractTablesInFromSection(tablesDeclaration);

        // Then
        assertThat(tablesInFromSection).isNotEmpty().hasSize(3);
        assertThat(tablesInFromSection).contains("footballers", "referees", "managers");
    }

    @Test
    public void extractOneTablesInJoinSection() {
        // Given
        String tablesDeclaration ="footballers f left outer join matches m on f.last_match_id = m.id";

        // When
        List<String> tablesInJoinSection = detector.extractTablesInJoinSection(tablesDeclaration);

        // Then
        assertThat(tablesInJoinSection).isNotEmpty().hasSize(1);
        assertThat(tablesInJoinSection).contains("matches");
    }

    @Test
    public void extractTwoTablesInJoinSection() {
        // Given
        String tablesDeclaration ="footballers f left outer join matches m on f.last_match_id = m.id left join referees r on m.referee_id=r.id";

        // When
        List<String> tablesInJoinSection = detector.extractTablesInJoinSection(tablesDeclaration);

        // Then
        assertThat(tablesInJoinSection).isNotEmpty().hasSize(2);
        assertThat(tablesInJoinSection).contains("matches", "referees");
    }

    @Test
    public void extractTwoTablesInJoinSectionWiithFromTableBetween() {
        // Given
        String tablesDeclaration ="footballers f left outer join matches m on f.last_match_id = m.id, managers mm left join referees r on m.referee_id=r.id";

        // When
        List<String> tablesInJoinSection = detector.extractTablesInJoinSection(tablesDeclaration);

        // Then
        assertThat(tablesInJoinSection).isNotEmpty().hasSize(2);
        assertThat(tablesInJoinSection).contains("matches", "referees");
    }

}
