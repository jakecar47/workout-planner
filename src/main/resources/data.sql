USE workout_app;

SET FOREIGN_KEY_CHECKS = 0;

-- ==========================
-- USERS
-- ==========================
INSERT INTO users (email, uname, password) VALUES
('john@example.com', 'john', '$2a$10$0jexVrmb4tBmDdYcvLNeXeChudiALhS9PTwsLJMFPXV.gQsrD6iHW'),
('sarah@example.com', 'sarah', '$2a$10$V8qpG30QGkS7NF2SCCBdDO.JWCPVcERHfQqejO.NpP1CYILNFK/ou'),
('alex@example.com', 'alex', '$2a$10$AlW8dxharaMhyjgNULMd5OgP50ceJtBsmCTjEPmtnfk1BLvbUmayG');

-- ==========================
-- EXERCISES (Loaded from dataset)
-- ==========================
DROP TABLE IF EXISTS staging_exercises;
CREATE TABLE staging_exercises (
    name VARCHAR(255),
    target_muscle VARCHAR(255),
    image_path VARCHAR(255),
    description TEXT
);

-- Load Kaggle file
-- NOTE: replace path with your absolute path to the CSV file
LOAD DATA LOCAL INFILE 'C:/DBMSTermProject/cs4370-term-project/uploads/exercises/exercises_prepared.csv'
INTO TABLE staging_exercises
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(name, target_muscle, image_path, description);

-- Upsert into main exercises table
INSERT INTO exercises (name, target_muscle, image_path, description)
SELECT name, target_muscle, image_path, description
FROM staging_exercises
ON DUPLICATE KEY UPDATE
    target_muscle = VALUES(target_muscle),
    image_path = VALUES(image_path),
    description = VALUES(description);

DROP TABLE IF EXISTS staging_exercises;


-- ==========================
-- WORKOUTS (VALID USERS)
-- ==========================
INSERT INTO workouts (id, user_id, name, description, startTime, endTime, created_at) VALUES
(1, 1, 'Chest Day', 'Bench-focused upper body session', '2025-12-01 08:00:00', '2025-12-01 09:30:00', '2025-12-01 07:45:00'),
(2, 1, 'Leg Day', 'Lower-body strengthening routine', '2025-12-02 10:00:00', '2025-12-02 11:45:00', '2025-12-02 09:50:00'),
(3, 1, 'Back Day', 'All pulling movements', '2025-12-03 17:00:00', '2025-12-03 18:30:00', '2025-12-03 16:45:00'),
(4, 2, 'Full Body A', 'Balanced workout', '2025-12-04 06:30:00', '2025-12-04 08:00:00', '2025-12-04 06:15:00'),
(5, 2, 'Full Body B', 'Alternate full-body session', '2025-12-05 18:30:00', '2025-12-05 20:00:00', '2025-12-05 18:15:00'),
(6, 3, 'Arms and Shoulders', 'Upper arm isolation', '2025-12-06 14:00:00', '2025-12-06 15:30:00', '2025-12-06 13:45:00');

-- ==========================
-- WORKOUT_EXERCISES (BASE)
-- ==========================
-- Workout 1 — Chest Day
INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 1, id, NULL, 4, 8 FROM exercises WHERE name = 'barbell bench press';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 1, id, NULL, 3, 15 FROM exercises WHERE name = 'dumbbell fly';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 1, id, NULL, 3, 12 FROM exercises WHERE name = 'weighted tricep dips';


-- Workout 2 — Leg Day
INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 2, id, NULL, 5, 5 FROM exercises WHERE name = 'barbell wide squat';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 2, id, NULL, 4, 12 FROM exercises WHERE name = 'lever leg extension';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 2, id, NULL, 3, 10 FROM exercises WHERE name = 'lever lying leg curl';


-- Workout 3 — Back Day
INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 3, id, NULL, 5, 5 FROM exercises WHERE name = 'barbell deadlift';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 3, id, NULL, 4, 8 FROM exercises WHERE name = 'pull up (neutral grip)';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 3, id, NULL, 3, 12 FROM exercises WHERE name = 'cable seated row';


-- Workout 4 — Full Body A
INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 4, id, NULL, 3, 10 FROM exercises WHERE name = 'barbell deadlift';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 4, id, NULL, 3, 10 FROM exercises WHERE name = 'barbell bench press';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 4, id, NULL, 3, 12 FROM exercises WHERE name = 'dumbbell goblet squat';


-- Workout 5 — Full Body B
INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 5, id, NULL, 3, 6 FROM exercises WHERE name = 'dumbbell lunge with bicep curl';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 5, id, NULL, 4, 12 FROM exercises WHERE name = 'dumbbell bent over row';


-- Workout 6 — Arms & Shoulders
INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 6, id, NULL, 3, 10 FROM exercises WHERE name = 'dumbbell seated shoulder press';

INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT 6, id, NULL, 4, 15 FROM exercises WHERE name = 'dumbbell seated bicep curl';

-- ==========================
-- GOALS
-- ==========================
INSERT INTO goals (user_id, description) VALUES
(1, 'Bench 185 lbs'),
(1, 'Run a mile under 7 minutes'),
(1, 'Deadlift 315 lbs'),
(2, 'Lose 10 pounds'),
(2, 'Do 10 pull-ups'),
(3, 'Increase squat to 225 lbs');

SET FOREIGN_KEY_CHECKS = 1;
