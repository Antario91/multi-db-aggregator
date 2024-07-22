package com.multidb.aggregator.user;

import com.multidb.aggregator.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidatorTest {
    @Test
    public void validateFilters_shouldHandleNullFilters() {
        assertThrows(ValidationException.class, () -> UserValidator.validateFilters(null));
    }

    @Test
    public void validateFilters_shouldHandleUnknownFilters() {
        Map<String, String> filters = Stream.concat(Stream.of(
                                UserValidator.validFilters.toArray(new String[0])),
                        Stream.of("SomeUnknownFilter"))
                .collect(Collectors.toMap(Function.identity(), o -> "someFilterValue"));

        assertThrows(ValidationException.class, () -> UserValidator.validateFilters(filters));
    }

    @Test
    public void validateColumnConsistency_shouldCheckEmptyColumns() {
        Map<String, Map<String, String>> columnMapping = Map.of(
                "dba", Map.of("field1", "column1", "field2", "column2"),
                "dbb", Map.of("field1", "column1", "field2", "")
        );

        assertThrows(ValidationException.class, () -> UserValidator.validateColumnConsistency(columnMapping));
    }

    @Test
    public void validateTableConsistency_shouldCheckEmptyColumns() {
        Map<String, String> tableMapping = Map.of("dba", "table1", "dbb", "");

        assertThrows(ValidationException.class, () -> UserValidator.validateTableConsistency(tableMapping));
    }
}
