A full-stack workout planning application built using **Spring Boot**, **Java**, **MySQL**, and **Docker**.  
The application allows users to create accounts, manage workout routines, and store fitness data securely using a relational database architecture.
 
## Tech Stack

### Backend
- Java
- Spring Boot
- Maven
- RESTful APIs

### Frontend
- HTML
- CSS
- Mustache Templates (server-side rendering)

### Database
- MySQL
- Docker (Containerized database)

### Version Control
- Git & GitHub

---

## Features

- User authentication (registration & login)
- Create, update, and delete workout plans
- Store workout routines in a relational database
- Secure backend API handling business logic
- Dockerized MySQL database for consistent development environments
- Structured MVC architecture (Controller → Service → Repository)

---

## System Architecture

The project follows a layered MVC architecture:

- **HTML Frontend** handles user interaction and form submissions.
- **Spring Boot Backend** processes business logic and exposes RESTful endpoints.
- **MySQL Database** stores user credentials and workout data.
- **Docker** ensures consistent database setup and deployment across environments.

---

How to run:
 1. Create Docker Container with (if not done already):\
docker run -d --name workout-mysql -e MYSQL_ROOT_PASSWORD=mysqlpass -e MYSQL_DATABASE=workout_app -p 33306:3306 mysql:8.0

 2.1 On any Windows machine that has Java installed, simply run: .\mvnw spring-boot:run --% -Dspring-boot.run.arguments="--server.port=8081"\

 2.2 On MacOS, run: mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"

 3. Open the browser and navigate to the following URL:
 http://localhost:8081/

---

JDBC Connection:\
name:workout_app\
username:root\
password:mysqlpass\

Test Account 1:\
username: john\
password: pass123\

Test Account 2:\
username: sarah\
password: pass123\

Test Account 3:\
username: alex\
password: pass123\

External Libraries:
Instructor permission email subject line: Permission to use Apache Commons CSV
Library: org.apache.commons:commons-csv
