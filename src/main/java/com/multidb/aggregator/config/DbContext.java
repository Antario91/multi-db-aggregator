package com.multidb.aggregator.config;

import com.multidb.aggregator.user.UserValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@Getter
public class DbContext {
    private final Map<String, Map<String, String>> columnMapping;
    private final Map<String, String> tableMapping;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, NamedParameterJdbcTemplate> userJdbcTemplates;
    @Setter
    private String currentDbId;

    @Autowired
    public DbContext(DatasourceConfig datasourceConfig) {
        log.info("Initializing DbContext");

        columnMapping = createColumnMapping(datasourceConfig);
        UserValidator.validateColumnConsistency(columnMapping);
        log.info("columnMapping: {}", columnMapping);

        tableMapping = createTableMapping(datasourceConfig);
        UserValidator.validateTableConsistency(tableMapping);
        log.info("tableMapping: {}", tableMapping);

        userJdbcTemplates = createUserJdbcTemplates(datasourceConfig);
    }

    private Map<String, Map<String, String>> createColumnMapping(DatasourceConfig datasourceConfig) {
        return datasourceConfig.getDataSources().stream()
                .collect(Collectors.toMap(
                                DataSource::getName,
                                dataSource ->
                                        objectMapper
                                                .convertValue(
                                                        dataSource.getMapping(),
                                                        new TypeReference<Map<String, String>>() {
                                                        }
                                                )
                        )
                );
    }

    private Map<String, String> createTableMapping(DatasourceConfig datasourceConfig) {
        return datasourceConfig.getDataSources().stream()
                .collect(Collectors.toMap(DataSource::getName, DataSource::getTable));
    }

    private Map<String, NamedParameterJdbcTemplate> createUserJdbcTemplates(DatasourceConfig datasourceConfig) {
        return datasourceConfig.getDataSources().stream()
                .map(config -> {
                    DataSourceProperties dataSourceProperties = new DataSourceProperties();
                    dataSourceProperties.setName(config.getName());
                    dataSourceProperties.setUsername(config.getUser());
                    dataSourceProperties.setPassword(config.getPassword());
                    dataSourceProperties.setUrl(config.getUrl());
                    dataSourceProperties.setType(HikariDataSource.class);
                    dataSourceProperties.setDriverClassName("org.postgresql.Driver");
                    return dataSourceProperties;
                }).collect(
                        Collectors.toMap(
                                DataSourceProperties::getName,
                                dataSourceProperties ->
                                        new NamedParameterJdbcTemplate(dataSourceProperties
                                                .initializeDataSourceBuilder().build())
                        )
                );
    }
}
