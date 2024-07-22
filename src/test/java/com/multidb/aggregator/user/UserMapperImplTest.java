package com.multidb.aggregator.user;

import com.multidb.aggregator.config.DbContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserMapperImplTest {
    @Mock
    private ResultSet resultSet;
    @Mock
    private DbContext dbContext;

    private UserMapperImpl userMapper;

    @BeforeEach
    public void init() {
        userMapper = new UserMapperImpl(dbContext);
    }

    @Test
    public void mapperShouldMapResultSetToUser() throws SQLException {
        String dbId = "db1";
        Map<String, String> columnMapping = Map.of(
                UserFields.ID.getFieldName(), "some_id_column",
                UserFields.USERNAME.getFieldName(), "some_username_column",
                UserFields.NAME.getFieldName(), "some_name_colum",
                UserFields.SURNAME.getFieldName(), "some_surname_column");
        when(dbContext.getColumnMapping()).thenReturn(Map.of(dbId, columnMapping));
        when(dbContext.getCurrentDbId()).thenReturn(dbId);

        User expectedUser = User.builder()
                .id("24")
                .username("lakers")
                .name("Kobe")
                .surname("Bryant")
                .build();

        when(resultSet.getString(columnMapping.get(UserFields.ID.getFieldName()))).thenReturn(expectedUser.getId());
        when(resultSet.getString(columnMapping.get(UserFields.USERNAME.getFieldName())))
                .thenReturn(expectedUser.getUsername());
        when(resultSet.getString(columnMapping.get(UserFields.NAME.getFieldName()))).thenReturn(expectedUser.getName());
        when(resultSet.getString(columnMapping.get(UserFields.SURNAME.getFieldName())))
                .thenReturn(expectedUser.getSurname());

        User actualUser = userMapper.mapRow(resultSet, 1);

        assertEquals(expectedUser, actualUser);
    }
}
