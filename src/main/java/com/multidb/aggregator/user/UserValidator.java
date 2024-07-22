package com.multidb.aggregator.user;

import com.multidb.aggregator.exception.ValidationException;
import io.micrometer.common.util.StringUtils;

import java.util.Map;
import java.util.Set;

public class UserValidator {
    public static final Set<String> validFilters = Set.of(UserFields.ID.getFieldName(),
            UserFields.USERNAME.getFieldName(), UserFields.NAME.getFieldName(), UserFields.SURNAME.getFieldName());

    public static void validateFilters(Map<String, String> filters) {
        if (filters == null) {
            throw new ValidationException("Filters must be not null");
        }
        if (!validFilters.containsAll(filters.keySet())) {
            throw new ValidationException(String.format(
                    "Provided filters are not valid. Available filters: %s", validFilters));
        }
    }

    public static void validateColumnConsistency(Map<String, Map<String, String>> columnMapping) {
        columnMapping.forEach(
                (dbId, colMapping) -> colMapping.forEach(
                        (fieldName, columnName) -> {
                            if (StringUtils.isEmpty(columnName)) {
                                throw new ValidationException(String.format(
                                        "Column mapping is inconsistent for Datasource %s. Column name for %s field " +
                                                "is empty. Check DB mapping in config file", dbId, fieldName));
                            }
                        }));
    }

    public static void validateTableConsistency(Map<String, String> tableMapping) {
        tableMapping.forEach((dbId, tableName) -> {
            if (StringUtils.isEmpty(tableName)) {
                throw new ValidationException(String.format(
                        "Table mapping is inconsistent for Datasource %s. Table name is absent. " +
                                "Check DB mapping in config file", dbId));
            }
        });
    }
}
