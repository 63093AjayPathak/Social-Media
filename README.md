## Social-Media

This is repository for Social Media Application that is being developed by team rutugandha

# 1 Setup

For each service follow their application.properties and/or application.yml file for setup required.
If changing host and port in Service registry then make the changes in application.properties file of Config server and git repo used in config server.

# 2 About

# 2.1 Techonlogies used for each Service

# 2.1.1 Service Registry

Spring Boot 2.X + jdk 11
Spring Cloud 2021.0.7
Netflix Eureka Server

# 2.1.2 Config-Server

SpringBoot 2.X + jdk 11
Spring Cloud 2021.0.8
Spring Cloud Config Server

# 2.1.3 Api-Gateway

SpringBoot 2.X + jdk 11
Spring Cloud Gateway
Spring webflux
JWT

# 2.1.4 Identity-Service

SpringBoot 3.X + jdk 17
Spring Security
JWT
Spring Data JPA
Java Mail and Spring Framework's email sending support

# 2.1.5 User-Service

SpringBoot 2.X + jdk 11
Spring Data Neo4j
Amazon Web Services SDK for Java

# 2.1.6 Post-Service

SpringBoot 2.X + jdk 11
Spring Data MongoDB
Amazon Web Services SDK for Java

# 2.2 Functionalities Provided

# 2.2.1 User registration and validation

User Registration through valid email id ( google mail id)
User validation through email used
Password reset through email.

# 2.2.2 Interaction between users

Adding other users as friend.
Removing a friend from list
See all received friend requests
Delete sent/received request
Search other users via username
See other User's Profile ( With only public post for non-friend users , public+private posts for friends)
Like/dislike posts by friends

# 2.2.3 User Profile

Make changes to own user info.
Make changes to profile like add new post, delete post.
Change/remove profile picture.
