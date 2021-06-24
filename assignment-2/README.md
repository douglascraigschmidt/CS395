# Overview

This assignment will give you practice developing an application
with JPA. You will implement a simple API for course registration.

# Instructions

You will implement an application that uses JPA. The API provides functions to
register students for courses. You are free to modify any of the provided code 
except for the tests in RegistrationApplicationTests. You can add any
additional models that you want. 

The specification for the API is contained in the api-swagger.yml file. 
You can view this file by going to: https://editor.swagger.io and then
selecting File->Import File and choosing the api-swagger.yml file on your
machine. 

You should use the RegistrationApplicationTests as the source of truth for
the expected behavior. If the behavior is not covered by the tests, you are
welcome to use whatever behavior you want as long as the tests pass.

# Your Swagger API

The project is set up to generate a Swagger API for your application.
If you run your application, you can view this Swagger API at: http://localhost:8080/swagger-ui/

# Learning More JPA

You can implement this assignment with basic JPA persistence concepts. However,
there are some additional JPA concepts that you may find helpful to learn:

  1. JPA Relationships: You may find @ManyToOne helpful for this assignment. 
     You can read more here: https://www.baeldung.com/hibernate-one-to-many
  2. Repository Query Methods: Query methods in your repository will greatly
     simplify the implementation. 
     You can read more here: https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html
     
You will not be graded on the efficiency of your architecture. If you don't choose
the best architecture or need to resort to some use of non-JPA filtering of results,
that is OK. However, you MUST use JPA to store the basic course, student, and registration
data in a persistent format. In addition, at a minimum, you must use ID-based lookups
or equivalent methods where possible.

### Reference Documentation
For further reference, please consider the following sections:

* [JPA Relationships](https://www.baeldung.com/hibernate-one-to-many)
* [JPA Repository Query Methods](https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html)
* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.1/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.1/gradle-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.5.1/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.5.1/reference/htmlsingle/#boot-features-jpa-and-spring-data)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
