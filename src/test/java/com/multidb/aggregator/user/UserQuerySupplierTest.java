package com.multidb.aggregator.user;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static com.multidb.aggregator.user.UserQuerySupplier.LIKE_OPERATOR_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserQuerySupplierTest {
    @Test
    public void getUserQuery_getQueryWithoutFilters() {
        Map<String, String> columnMapping = Map.of(
                UserFields.ID.getFieldName(), "some_id_column",
                UserFields.USERNAME.getFieldName(), "some_username_column",
                UserFields.NAME.getFieldName(), "some_name_colum",
                UserFields.SURNAME.getFieldName(), "some_surname_column");
        String tableName = "user_table";
        String expectedQuery = "select * from " + tableName;

        String actualQuery = UserQuerySupplier.getUserQuery(Collections.emptyMap(), columnMapping, tableName);

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void getUserQuery_getQueryForSingleFilter() {
        Map<String, String> columnMapping = Map.of(
                UserFields.ID.getFieldName(), "some_id_column",
                UserFields.USERNAME.getFieldName(), "some_username_column",
                UserFields.NAME.getFieldName(), "some_name_colum",
                UserFields.SURNAME.getFieldName(), "some_surname_column");
        String tableName = "user_table";
        Map<String, String> fieldFilters = Map.of(UserFields.ID.getFieldName(), "some_Id");
        String expectedQuery = String.format(
                "select * from %s where LOWER(%s) like :%s",
                tableName,
                columnMapping.get(UserFields.ID.getFieldName()),
                columnMapping.get(UserFields.ID.getFieldName()));

        String actualQuery = UserQuerySupplier.getUserQuery(fieldFilters, columnMapping, tableName);

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void getUserQuery_getQueryForDuplicateFilter() {
        String duplicateColumn = "some_duplicate_column";
        Map<String, String> columnMapping = Map.of(
                UserFields.ID.getFieldName(), duplicateColumn,
                UserFields.USERNAME.getFieldName(), duplicateColumn,
                UserFields.NAME.getFieldName(), "some_name_colum",
                UserFields.SURNAME.getFieldName(), "some_surname_column");
        String tableName = "user_table";
        Map<String, String> fieldFilters = Map.of(
                UserFields.ID.getFieldName(), "some_Id",
                UserFields.USERNAME.getFieldName(), "some_username",
                UserFields.NAME.getFieldName(), "some_username");
        String expectedQuery = "select * from user_table " +
                "where LOWER(some_name_colum) like :some_name_colum " +
                "and( LOWER(some_duplicate_column) like :some_duplicate_column0 " +
                "or LOWER(some_duplicate_column) like :some_duplicate_column1)";

        String actualQuery = UserQuerySupplier.getUserQuery(fieldFilters, columnMapping, tableName);

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void getNamedParameters_getParametersForSingleFilter() {
        Map<String, String> columnMapping = Map.of(
                UserFields.ID.getFieldName(), "some_id_column",
                UserFields.USERNAME.getFieldName(), "some_username_column",
                UserFields.NAME.getFieldName(), "some_name_colum",
                UserFields.SURNAME.getFieldName(), "some_surname_column");
        Map<String, String> fieldFilters = Map.of(
                UserFields.ID.getFieldName(), "some_id",
                UserFields.USERNAME.getFieldName(), "some_username");
        Map<String, String> expectedNamedParameters = Map.of(
                columnMapping.get(UserFields.ID.getFieldName()), String.format(LIKE_OPERATOR_VALUE, fieldFilters.get(
                        UserFields.ID.getFieldName())),
                columnMapping.get(UserFields.USERNAME.getFieldName()), String.format(LIKE_OPERATOR_VALUE, fieldFilters.get(
                        UserFields.USERNAME.getFieldName())));

        Map<String, String> actualParameters = UserQuerySupplier.getNamedParameters(fieldFilters, columnMapping);

        assertEquals(expectedNamedParameters, actualParameters);
    }

    @Test
    public void getNamedParameters_getParametersForDuplicateFilter() {
        String duplicateColumn = "some_duplicate_column";
        Map<String, String> columnMapping = Map.of(
                UserFields.ID.getFieldName(), duplicateColumn,
                UserFields.USERNAME.getFieldName(), duplicateColumn,
                UserFields.NAME.getFieldName(), "some_name_colum",
                UserFields.SURNAME.getFieldName(), "some_surname_column");
        Map<String, String> fieldFilters = Map.of(
                UserFields.ID.getFieldName(), "some_id",
                UserFields.USERNAME.getFieldName(), "some_username",
                UserFields.NAME.getFieldName(), "some_name");
        Map<String, String> expectedNamedParameters = Map.of(
                columnMapping.get(UserFields.ID.getFieldName()) + "0",
                String.format(LIKE_OPERATOR_VALUE, fieldFilters.get(UserFields.USERNAME.getFieldName())),
                columnMapping.get(UserFields.USERNAME.getFieldName()) + "1",
                String.format(LIKE_OPERATOR_VALUE, fieldFilters.get(UserFields.ID.getFieldName())),
                columnMapping.get(UserFields.NAME.getFieldName()),
                String.format(LIKE_OPERATOR_VALUE, fieldFilters.get(UserFields.NAME.getFieldName())));

        Map<String, String> actualParameters = UserQuerySupplier.getNamedParameters(fieldFilters, columnMapping);

        assertEquals(expectedNamedParameters, actualParameters);
    }
}
