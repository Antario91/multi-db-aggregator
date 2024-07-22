package com.multidb.aggregator.user;

import com.multidb.aggregator.exception.UsersNotFoundException;
import com.multidb.aggregator.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            operationId = "aggregateUsers",
            summary = "Aggregate users from all data sources",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "id", description = "User ID"),
                    @Parameter(in = ParameterIn.QUERY, name = "username", description = "User alias"),
                    @Parameter(in = ParameterIn.QUERY, name = "name", description = "User name"),
                    @Parameter(in = ParameterIn.QUERY, name = "surname", description = "User surname")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All aggregated users",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid filters supplied"),
            @ApiResponse(responseCode = "404", description = "Users not found")})
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> aggregateUsers(@RequestParam @Parameter(hidden = true) Map<String, String> requestParams) {
        return userService.aggregateUsers(requestParams);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(UsersNotFoundException.class)
    public ResponseEntity<String> handleUsersNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no users found based on request");
    }
}
