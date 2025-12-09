package edu.uga.cs4370.group4.term_project.config;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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

        ClassPathResource csvFile = new ClassPathResource("datasets/exercises_prepared_portable.csv");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvFile.getInputStream())
        );

        // ⭐ Modern CSVFormat builder (no deprecated methods)
        CSVFormat format = CSVFormat.DEFAULT
                .builder()
                .setHeader()                 // same as withFirstRecordAsHeader
                .setQuote('"')               // same as withQuote
                .setEscape('\\')             // same as withEscape
                .setSkipHeaderRecord(true)   // skip the header line
                .build();

        CSVParser parser = format.parse(reader);

        String sql = """
            INSERT INTO exercises (name, target_muscle, image_path, description)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                target_muscle = VALUES(target_muscle),
                image_path = VALUES(image_path),
                description = VALUES(description)
        """;

        for (CSVRecord record : parser) {

            String name          = record.get("name");
            String target_muscle = record.get("target_muscle");
            String image_path    = record.get("image_path");
            String description   = record.get("description");

            jdbc.update(sql, name, target_muscle, image_path, description);
        }
    }
}