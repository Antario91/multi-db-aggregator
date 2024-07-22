package com.multidb.aggregator.user;

import com.multidb.aggregator.config.DbContext;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Component
public class UserMapperImpl implements UserMapper {
    private final DbContext dbContext;

    public UserMapperImpl(DbContext dbContext) {
        this.dbContext = dbContext;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, String> columnMapping = dbContext.getColumnMapping().get(dbContext.getCurrentDbId());
        return User.builder()
                .id(rs.getString(columnMapping.get(UserFields.ID.getFieldName())))
                .username(rs.getString(columnMapping.get(UserFields.USERNAME.getFieldName())))
                .name(rs.getString(columnMapping.get(UserFields.NAME.getFieldName())))
                .surname(rs.getString(columnMapping.get(UserFields.SURNAME.getFieldName())))
                .build();
    }
}
