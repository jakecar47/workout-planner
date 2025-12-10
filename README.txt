Members and contributions:
Jake Caruana
 - Figma web design draft
 - UI design (mustache templates)
 - Implemented weekly-plan database table for the home page and backend logic for weekly-plan table using:
    - WorkoutAssignController.java
    - WorkoutController.java
    - WeeklyPlanService.java
    - WeeklyPlan.java
    - WorkoutService.java
    - WeeklyPlanRepository.java
 - Implemented backend logic for goals using:
    - Goal.java
    - GoalService.java
    - GoalController.java
    - ProfileController.java
    - GoalRepository.java
 - workout.mustache search bar implementation
 - style.css

Daniel Fairchild
 - Spring Boot app initializer configuration 
 - queries.sql writeup
 - pom.xml comments/review
 - data.sql implementation
 - mustache nav_bar fragment
 - mustache workout .html
 - WorkoutController.java implementation
 - WorkoutService.java implementation
 - ER Model design

Barrett Giesbrecht
 - Prelim.pdf and db_design.pdf
 - Gathered and consolidated all SQL queries for queries.sql (with Daniel)
 - Full responsibility for loading the dataset, including:
    - Downloading dataset
    - Editing/cleaning it to fit the schema
    - Adding CSVs into resources/-
    - Implementing DataLoader to load data into MySQL on startup (in datasource.txt)
 - Stored passwords securely as well as writing the documentation on it (security.txt)
 - Implemented most of the Exercises Page, including:
    - Search bar functionality
    - Rendering logic
 - Added a fully functional Logout button + correct session termination
 - Various small UI/UX changes in style.css
 - Additional debugging and backend support throughout the project

Reshi Manivannan
 - 

How to run:
1. Create Docker Container with (if not done already):
docker run -d --name workout-mysql -e MYSQL_ROOT_PASSWORD=mysqlpass -e MYSQL_DATABASE=workout_app -p 33306:3306 mysql:8.0

2. On any machine that has Java installed, simply run: .\mvnw spring-boot:run --% -Dspring-boot.run.arguments="--server.port=8081"

3. Open the browser and navigate to the following URL:
http://localhost:8081/

Technologies used:
 - Java
 - Apache Maven
 - Docker Desktop
 - Spring Boot Starter Mustache
 - Spring Boot Starter Security
 - Spring Boot Starter Web
 - MySQL Connector/J
 - Spring Boot Starter JDBC
 - Spring Boot Starter Test
 - Spring Security Test
 - Spring Boot Maven Plugin
 - SQL
 - Jakarta Servlet API
 - Maven / Maven Wrapper
 - Git / GitHub
 - VSCode

JDBC Connection:
name:workout_app
username:root
password:mysqlpass

Test Account 1:
username: john
password: pass123

Test Account 2:
username: sarah
password: pass123

Test Account 3:
username: alex
password: pass123

External Libraries:
Instructor permission email subject line: Permission to use Apache Commons CSV
Library: org.apache.commons:commons-csv