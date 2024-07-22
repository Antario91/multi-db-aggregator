package com.multidb.aggregator.user;

import java.util.*;
import java.util.stream.Collectors;

public class UserQuerySupplier {
    public static final String SELECT_ALL_USERS = "select * from %s";
    public static final String SELECT_USER_WITH_FILTER = "select * from %s where";
    public static final String LIKE_OPERATOR_VALUE = "%%%s%%";

    public static String getUserQuery(
            Map<String, String> fieldFilters,
            Map<String, String> columnMapping,
            String tableName) {
        if (fieldFilters.isEmpty()) {
            return String.format(SELECT_ALL_USERS, tableName);
        }

        Map<String, List<String>> filtersByColumn = groupFilterValuesByColumnName(fieldFilters, columnMapping);

        Iterator<Map.Entry<String, List<String>>> filtersIterator = filtersByColumn.entrySet().iterator();
        String targetQuery = String.format(SELECT_USER_WITH_FILTER, tableName);
        do {
            Map.Entry<String, List<String>> filter = filtersIterator.next();
            String columnName = filter.getKey();
            List<String> filterValues = filter.getValue();
            targetQuery = filterValues.size() > 1
                    ? handleDuplicateColumnFilter(targetQuery, columnName, filterValues)
                    : handleSingleColumnFilter(targetQuery, columnName);

            if (filtersIterator.hasNext()) {
                targetQuery = targetQuery.concat(" and");
            }
        } while (filtersIterator.hasNext());
        return targetQuery;
    }

    public static Map<String, String> getNamedParameters(Map<String, String> fieldFilters, Map<String, String> columnMapping) {
        Map<String, List<String>> filtersByColumn = groupFilterValuesByColumnName(fieldFilters, columnMapping);

        Map<String, String> namedParameters = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : filtersByColumn.entrySet()) {
            List<String> filterValues = entry.getValue();
            if (filterValues.size() == 1) {
                namedParameters.put(
                        entry.getKey(),
                        String.format(LIKE_OPERATOR_VALUE, filterValues.getFirst().toLowerCase()));
            } else {
                handleSeveralFilterValues(entry, filterValues, namedParameters);
            }
        }

        return namedParameters;
    }

    private static Map<String, List<String>> groupFilterValuesByColumnName(
            Map<String, String> fieldFilters, Map<String, String> columnMapping) {
        return fieldFilters.entrySet().stream().collect(Collectors.groupingBy(
                entry -> columnMapping.get(entry.getKey()),
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    private static String handleDuplicateColumnFilter(String targetQuery, String columnName,
                                                      List<String> filterValues) {
        targetQuery = targetQuery.concat("(");
        Iterator<String> filtersIterator = filterValues.iterator();
        int namedParameterIncrementer = 0;
        do {
            filtersIterator.next();
            targetQuery = targetQuery
                    .concat(" LOWER(")
                    .concat(columnName)
                    .concat(") like :")
                    .concat(columnName + namedParameterIncrementer++);
            if (filtersIterator.hasNext()) {
                targetQuery = targetQuery.concat(" or");
            }
        } while (filtersIterator.hasNext());
        targetQuery = targetQuery.concat(")");
        return targetQuery;
    }

    private static String handleSingleColumnFilter(String targetQuery, String columnName) {
        return targetQuery
                .concat(" LOWER(")
                .concat(columnName)
                .concat(") like :")
                .concat(columnName);
    }

    private static void handleSeveralFilterValues(
            Map.Entry<String, List<String>> columnToFilterValues,
            List<String> filterValues,
            Map<String, String> namedParameters) {
        String columnName = columnToFilterValues.getKey();
        for (int i = 0; i < filterValues.size(); i++) {
            namedParameters.put(
                    columnName + i,
                    String.format(LIKE_OPERATOR_VALUE, filterValues.get(i).toLowerCase()));
        }
    }
}