# Build Reactive Rest APIs with Spring and MongoDB

# Requirements
    Install mongodb on docker
    Java: JDK 1.8
    Maven Build

# MongoDB

### Install mongodb on docker

```
docker-compose up
```

Check mongodb status
```
docker ps
```

Connect to mongodb
```
mongo 127.0.0.1:27017
```

Check config mongodb
src/main/resource/application.properties


# Integration Tests
An integration test based on Spring's WebTestClient
Testing api with basicAuthentication

# Usage
[Create application from Spring Initializr](http://start.spring.io/)
Add dependencies: Reactive Web, Reactive MongoDB, Security


Spring Security on your class path and then spring security is automatically configured with a default user and generated password

User: user
Password was generated in Console

```
Using default security password: 32fd0ac8-ba12-4978-ae28-0cea0dfd636e
```

### Security Config
Config username and password in SecurityConfig.java


Clean
```
mvn clean
```

Build
```
mvn package
```

Test
```
mvn test
```

Run
```
mvn spring-boot:run
```
