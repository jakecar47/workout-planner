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
-- EXERCISES (20 base exercises)
-- ==========================
INSERT INTO exercises (id, name, target_muscle, description) VALUES
(1, 'Bench Press', 'Chest', 'Barbell bench press for upper body strength'),
(2, 'Incline Bench Press', 'Chest', 'Targets upper chest'),
(3, 'Push Ups', 'Chest', 'Bodyweight chest exercise'),
(4, 'Deadlift', 'Back', 'Full compound movement'),
(5, 'Barbell Row', 'Back', 'Mid-back focus'),
(6, 'Pull Ups', 'Back', 'Bodyweight pulling exercise'),
(7, 'Lat Pulldown', 'Back', 'Vertical pull exercise'),
(8, 'Squat', 'Legs', 'Primary lower body compound lift'),
(9, 'Leg Press', 'Legs', 'Lower-body machine press'),
(10, 'Lunges', 'Legs', 'Leg unilateral movement'),
(11, 'Shoulder Press', 'Shoulders', 'Overhead press movement'),
(12, 'Lateral Raise', 'Shoulders', 'Isolates lateral deltoid'),
(13, 'Bicep Curl', 'Arms', 'Isolates biceps'),
(14, 'Hammer Curl', 'Arms', 'Neutral-grip biceps variation'),
(15, 'Tricep Pushdown', 'Arms', 'Cable tricep extension'),
(16, 'Dips', 'Arms', 'Bodyweight triceps exercise'),
(17, 'Plank', 'Core', 'Isometric abdominal hold'),
(18, 'Crunches', 'Core', 'Abdominal flexion exercise'),
(19, 'Russian Twist', 'Core', 'Rotational core movement'),
(20, 'Mountain Climbers', 'Core/Cardio', 'Dynamic cardio movement');

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
INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps) VALUES
(1, 1, NULL, 4, 8),
(1, 3, NULL, 3, 15),
(1, 13, NULL, 3, 12),
(2, 8, NULL, 5, 5),
(2, 9, NULL, 4, 12),
(2, 10, NULL, 3, 10),
(3, 4, NULL, 5, 5),
(3, 5, NULL, 4, 8),
(3, 7, NULL, 3, 12),
(4, 1, NULL, 3, 10),
(4, 8, NULL, 3, 10),
(4, 14, NULL, 3, 12),
(5, 6, NULL, 3, 6),
(5, 12, NULL, 4, 12),
(6, 11, NULL, 3, 10),
(6, 13, NULL, 4, 10);

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

-- ==========================
-- DUMMY WORKOUTS FOR LARGE DATASET (50–150)
-- ==========================
INSERT INTO workouts (id, user_id, name, description, startTime, endTime, created_at)
SELECT
    seq.num,
    1,
    CONCAT('Auto Workout ', seq.num),
    'Synthetic dataset workout',
    DATE_ADD('2025-12-01', INTERVAL FLOOR(RAND()*30) DAY),
    DATE_ADD('2025-12-01', INTERVAL FLOOR(RAND()*30) DAY),
    NOW()
FROM (
    SELECT @row := @row + 1 AS num
    FROM information_schema.tables, (SELECT @row := 49) r
    LIMIT 101
) seq;

-- ==========================
-- LARGE DATASET (1,200 ROWS) 
-- ==========================
INSERT IGNORE INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
SELECT DISTINCT
    FLOOR(RAND()*101) + 50 AS workout_id,
    FLOOR(RAND()*20) + 1 AS exercise_id,
    FLOOR(RAND()*120) AS time,
    FLOOR(RAND()*5) + 1 AS sets,
    FLOOR(RAND()*15) + 5 AS reps
FROM information_schema.columns
LIMIT 1200;

SET FOREIGN_KEY_CHECKS = 1;
