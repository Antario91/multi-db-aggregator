# How to build application #
Multistage build is implemented in app.

1. run `docker build -t my-username/multi-db-aggregate-users:0.0.1-snapshot . --progress=plain --no-cache`
It will create image that can be run with `docker compose`
2. run `docker compose up`
It will run `main application`, `two Postgres DBs`, and `adminer` in order to check data in DBs

# OpenAPI #
There is OpenAPI 3.0 implemented. 
The following common links are available:
- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/v3/api-docs

# Data in DBs #
There are predefined data in DBs. You can check it at [initPostrgesA.sql](initPostrgesA.sql) and [initPostrgesB.sql](initPostrgesB.sql)

# Useful links #
The following links works with app and predefined data
- `curl -X GET "http://localhost:8080/users"`
- `curl -X GET "http://localhost:8080/users?id=userd&username=userg"`
- `curl -X GET "http://localhost:8080/users?username=userc"`
- `curl -X GET "http://localhost:8080/users?id=b27ec9b1-c32d-4b81-b1ff-74c450aaffb4"`
