package com.multidb.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "db")
@Getter
@Setter
@ToString
public class DatasourceConfig {
    private List<DataSource> dataSources;
}
