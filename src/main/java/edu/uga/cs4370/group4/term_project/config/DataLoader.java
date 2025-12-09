package edu.uga.cs4370.group4.term_project.config;

import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class DataLoader implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    public DataLoader(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) throws Exception {

        ClassPathResource csvFile = new ClassPathResource("datasets/exercises_prepared.csv");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {

            // skip header
            String line = br.readLine();

            String sql = """
                INSERT INTO exercises (name, target_muscle, image_path, description)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                target_muscle = VALUES(target_muscle),
                image_path = VALUES(image_path),
                description = VALUES(description)
            """;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);

                jdbc.update(sql,
                        parts[0],    // name
                        parts[1],    // target_muscle
                        parts[2],    // image_path
                        parts[3]     // description
                );
            }
        }
    }
}