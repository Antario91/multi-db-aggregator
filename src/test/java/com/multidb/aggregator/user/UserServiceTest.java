package com.multidb.aggregator.user;

import com.multidb.aggregator.config.DbContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private DbContext dbContext;
    @Mock
    private UserMapper userMapper;

    private MockedStatic<UserValidator> userValidator;

    private UserService userService;

    @BeforeEach
    public void init() {
        userValidator = mockStatic(UserValidator.class);
        userService = new UserService(dbContext, userMapper);
    }

    @AfterEach
    public void tearDown() {
        userValidator.close();
    }

    @Test
    public void aggregateUsers_shouldAggregateUsers() {
        NamedParameterJdbcTemplate jdbcTemplateA = mock(NamedParameterJdbcTemplate.class);
        NamedParameterJdbcTemplate jdbcTemplateB = mock(NamedParameterJdbcTemplate.class);

        Map<String, String> columnMappingA = Map.of(
                UserFields.ID.getFieldName(), "some_id_column",
                UserFields.USERNAME.getFieldName(), "some_username_column",
                UserFields.NAME.getFieldName(), "some_name_colum",
                UserFields.SURNAME.getFieldName(), "some_surname_column");

        String duplicateColumn = "some_duplicate_column";
        Map<String, String> columnMappingB = Map.of(
                UserFields.ID.getFieldName(), duplicateColumn,
                UserFields.USERNAME.getFieldName(), duplicateColumn,
                UserFields.NAME.getFieldName(), "some_name_colum",
                UserFields.SURNAME.getFieldName(), "some_surname_column");

        String dba = "dba";
        String dbb = "dbb";
        when(dbContext.getUserJdbcTemplates()).thenReturn(
                Map.of(dba, jdbcTemplateA, dbb, jdbcTemplateB));
        when(dbContext.getColumnMapping()).thenReturn(
                Map.of(dba, columnMappingA, dbb, columnMappingB));
        when(dbContext.getTableMapping()).thenReturn(
                Map.of(dba, "tableA", dbb, "tableB"));

        Map<String, String> fieldFilters = Map.of(
                UserFields.ID.getFieldName(), "some_Id",
                UserFields.USERNAME.getFieldName(), "some_username",
                UserFields.NAME.getFieldName(), "some_name",
                UserFields.SURNAME.getFieldName(), "some_surname");

        String userQueryB = "select * from tableB " +
                "where LOWER(some_name_colum) like :some_name_colum " +
                "and( LOWER(some_duplicate_column) like :some_duplicate_column0 " +
                "or LOWER(some_duplicate_column) like :some_duplicate_column1) " +
                "and LOWER(some_surname_column) like :some_surname_column";
        String userQueryA = "select * from tableA " +
                "where LOWER(some_name_colum) like :some_name_colum " +
                "and LOWER(some_id_column) like :some_id_column " +
                "and LOWER(some_username_column) like :some_username_column " +
                "and LOWER(some_surname_column) like :some_surname_column";

        when(jdbcTemplateA.query(
                userQueryA,
                UserQuerySupplier.getNamedParameters(fieldFilters, columnMappingA),
                userMapper)).thenReturn(
                List.of(User.builder()
                        .id("userA")
                        .username("userNameA")
                        .name("nameA")
                        .surname("surnameA")
                        .build()));
        when(jdbcTemplateB.query(
                userQueryB,
                UserQuerySupplier.getNamedParameters(fieldFilters, columnMappingB),
                userMapper)).thenReturn(
                List.of(User.builder()
                        .id("userB")
                        .username("userNameB")
                        .name("nameB")
                        .surname("surnameB")
                        .build()));

        userService.aggregateUsers(fieldFilters);

        userValidator.verify(() -> UserValidator.validateFilters(fieldFilters));

        verify(jdbcTemplateA).query(
                userQueryA,
                UserQuerySupplier.getNamedParameters(fieldFilters, columnMappingA),
                userMapper);
        verify(jdbcTemplateB).query(
                userQueryB,
                UserQuerySupplier.getNamedParameters(fieldFilters, columnMappingB),
                userMapper);
    }
}
