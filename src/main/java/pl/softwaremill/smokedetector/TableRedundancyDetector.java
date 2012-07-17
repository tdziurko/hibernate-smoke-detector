package pl.softwaremill.smokedetector;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TableRedundancyDetector {

    static final String NO_FROM_EXCEPTION_MESSAGE = "No 'from' key word found. Ignoring query \n%s";
    public static final String FROM_KEY_WORD = " from ";

    public void detectProblems(Set<SqlCommand> commandsInTime) {

        for (SqlCommand sqlCommand : commandsInTime) {
            detectProblem(sqlCommand);
        }
    }

    SqlCommand detectProblem(SqlCommand sqlCommand) {
        String tablesDeclaration = extractTablesDeclaration(sqlCommand.getCommandString());
        if(StringUtils.isEmpty(tablesDeclaration)) {
            return sqlCommand;
        }

        List<String> tablesUsedInFromSection = extractTablesInFromSection(tablesDeclaration);
        List<String> tablesUsedInJoins = extractTablesInJoinSection(tablesDeclaration);

        tablesUsedInFromSection.retainAll(tablesUsedInJoins);
        sqlCommand.setRedundantTables(tablesUsedInFromSection);

        return sqlCommand;
    }

    String extractTablesDeclaration(String query) {

        String cleanedQuery = removeRedundantWhitespaces(query);

        int fromIndex = StringUtils.lastIndexOf(cleanedQuery, FROM_KEY_WORD);
        if(fromIndex < 0) {
            return null;
        }

        int endIndex = findEndOfTablesDeclaration(cleanedQuery, fromIndex);

        return StringUtils.substring(cleanedQuery, fromIndex + FROM_KEY_WORD.length(), endIndex);
    }

    List<String> extractTablesInFromSection(String tablesDeclaration) {
        List<String> tablesInFromSection = Lists.newArrayList();

        int endOfFirstTable = StringUtils.indexOf(tablesDeclaration, " ");
        if(endOfFirstTable == -1) {
            endOfFirstTable = tablesDeclaration.length();
        }
        String firstTable = tablesDeclaration.substring(0, endOfFirstTable);
        tablesInFromSection.add(firstTable);

        if(tablesDeclaration.contains(", ") == false) {
            return tablesInFromSection;
        }

        // rest of the table names used in 'from section' are comma-separated (but might be separated with joins as well)
        // so we extract them by taking first word located just after each comma

        Iterator<String> iterator = Splitter.on(",").omitEmptyStrings().trimResults()
                .split(tablesDeclaration.substring(endOfFirstTable, tablesDeclaration.length()))
                .iterator();
        // we look for text after comma so first element in iterator should be ignored as it lies on the left from comma.
        iterator.next();

        while(iterator.hasNext()) {
            String tableCandidate = iterator.next();
            String tableName = StringUtils.substringBefore(tableCandidate, " ");
            tablesInFromSection.add(tableName);
        }

        return tablesInFromSection;
    }

    List<String> extractTablesInJoinSection(String tablesDeclaration) {
        List<String> tablesInJoinSection = Lists.newArrayList();

        if(tablesDeclaration.contains(" join ") == false) {
            return tablesInJoinSection;
        }


        // we look for table name next to every join in sql command fragment
        Iterator<String> iterator = Splitter.on("join ").omitEmptyStrings()
                .trimResults()
                .split(tablesDeclaration)
                .iterator();

        //we need table name after join, not before
        iterator.next();

        while(iterator.hasNext()) {
            String tableCandidate = iterator.next();
            String tableName = StringUtils.substringBefore(tableCandidate, " ");
            tablesInJoinSection.add(tableName);
        }

        return tablesInJoinSection;
    }

    private String removeRedundantWhitespaces(String query) {
        String cleanedQuery = query
                .replaceAll("\n", " ")
                .replaceAll("\t", " ")
                .replaceAll("\\s\\s+", " ").trim();

        return cleanedQuery;
    }

    private int findEndOfTablesDeclaration(String cleanedQuery, int fromIndex) {
        int endIndex = StringUtils.indexOfAny(cleanedQuery.substring(fromIndex), new String[]{" where ", " order by ", " limit ", " fetch first "});

        if (endIndex == -1) {
            endIndex = cleanedQuery.length();
        }
        return endIndex + fromIndex;
    }

}
