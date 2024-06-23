## Social-Media

This is repository for Social Media Application that is being developed by team rutugandha

# 1 Setup

1. For each service follow their application.properties and/or application.yml file for setup required.
2. If changing host and port in Service registry then make the changes in application.properties file of Config server and git repo used in config server.

# 2 About

# 2.1 Techonlogies used for each Service

# 2.1.1 Service Registry

1. Spring Boot 2.X + jdk 11
2. Spring Cloud 2021.0.7
3. Netflix Eureka Server

# 2.1.2 Config-Server

1. SpringBoot 2.X + jdk 11
2. Spring Cloud 2021.0.8
3. Spring Cloud Config Server

# 2.1.3 Api-Gateway

1. SpringBoot 2.X + jdk 11
2. Spring Cloud Gateway
3. Spring webflux
4. JWT

# 2.1.4 Identity-Service

1. SpringBoot 3.X + jdk 17
2. Spring Security
3. JWT
4. Spring Data JPA
5. Java Mail and Spring Framework's email sending support

# 2.1.5 User-Service

1. SpringBoot 2.X + jdk 11
2. Spring Data Neo4j
3. Amazon Web Services SDK for Java

# 2.1.6 Post-Service

1. SpringBoot 2.X + jdk 11
2. Spring Data MongoDB
3. Amazon Web Services SDK for Java

# 2.2 Functionalities Provided

# 2.2.1 User registration and validation

1. User Registration through valid email id ( google mail id)
2. User validation through email used
3. Password reset through email.

# 2.2.2 Interaction between users

1. Adding other users as friend.
2. Removing a friend from list
3. See all received friend requests
4.Delete sent/received request
5. Search other users via username
6. See other User's Profile ( With only public post for non-friend users , public+private posts for friends)
7. Like/dislike posts by friends

# 2.2.3 User Profile

1. Make changes to own user info.
2. Make changes to profile like add new post, delete post.
3. Change/remove profile picture.
