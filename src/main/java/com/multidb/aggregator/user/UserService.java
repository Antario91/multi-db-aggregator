package com.multidb.aggregator.user;

import com.multidb.aggregator.config.DbContext;
import com.multidb.aggregator.exception.UsersNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    private final DbContext dbContext;
    private final UserMapper userMapper;

    @Autowired
    public UserService(DbContext dbContext, UserMapper userMapper) {
        this.dbContext = dbContext;
        this.userMapper = userMapper;
    }

    public List<User> aggregateUsers(Map<String, String> filters) {
        log.info("Following filters were provided. Filter: {}", filters);

        UserValidator.validateFilters(filters);

        List<User> foundUsers = dbContext.getUserJdbcTemplates().entrySet().stream().map(jdbcTemplateEntry -> {
            String dbId = jdbcTemplateEntry.getKey();
            dbContext.setCurrentDbId(dbId);

            Map<String, String> columnMapping = dbContext.getColumnMapping().get(dbId);
            String userQuery = UserQuerySupplier.getUserQuery(
                    filters,
                    columnMapping,
                    dbContext.getTableMapping().get(dbId));
            Map<String, String> namedParameters = UserQuerySupplier.getNamedParameters(filters, columnMapping);

            log.info("Query: {} will be executed with named parameters: {} on DB: {}", userQuery, namedParameters, dbId);

            List<User> users = jdbcTemplateEntry.getValue().query(userQuery, namedParameters, userMapper);

            log.info("Users: {} were found in DB: {}", users, dbId);

            return users;
        }).flatMap(Collection::stream).toList();

        if (foundUsers.isEmpty()) {
            throw new UsersNotFoundException();
        }

        return foundUsers;
    }
}
