USE workout_app;

SET FOREIGN_KEY_CHECKS = 0;

-- ==========================
-- USERS
-- ==========================
INSERT INTO users (email, uname, password) VALUES
('john@example.com', 'john', '$2a$10$0jexVrmb4tBmDdYcvLNeXeChudiALhS9PTwsLJMFPXV.gQsrD6iHW'),
('sarah@example.com', 'sarah', '$2a$10$V8qpG30QGkS7NF2SCCBdDO.JWCPVcERHfQqejO.NpP1CYILNFK/ou'),
('alex@example.com', 'alex', '$2a$10$AlW8dxharaMhyjgNULMd5OgP50ceJtBsmCTjEPmtnfk1BLvbUmayG');
-- the lion
-- ==========================
-- WORKOUTS (VALID USERS)
-- ==========================
INSERT INTO workouts (id, user_id, name, description, startTime, endTime, created_at) VALUES
(1, 1, 'Chest Day', 'Bench-focused upper body session', '2025-12-01 08:00:00', '2025-12-01 09:30:00', '2025-12-01 07:45:00'),
(2, 1, 'Leg Day', 'Lower-body strengthening routine', '2025-12-02 10:00:00', '2025-12-02 11:45:00', '2025-12-02 09:50:00'),
(3, 1, 'Back Day', 'All pulling movements', '2025-12-03 17:00:00', '2025-12-03 18:30:00', '2025-12-03 16:45:00'),
(4, 2, 'Full Body A', 'Balanced workout', '2025-12-04 06:30:00', '2025-12-04 08:00:00', '2025-12-04 06:15:00'),
(5, 2, 'Full Body B', 'Alternate full-body session', '2025-12-05 18:30:00', '2025-12-05 20:00:00', '2025-12-05 18:15:00'),
(6, 3, 'Arms and Shoulders', 'Upper arm isolation', '2025-12-06 14:00:00', '2025-12-06 15:30:00', '2025-12-06 13:45:00'),
(8, 1, 'Leg Day Alternate', 'Secondary leg workout', '2025-12-02 10:00:00', '2025-12-02 11:45:00', '2025-12-02 09:50:00'),
(11, 3, 'Shoulder Press Focus', 'Overhead pressing emphasis', '2025-12-05 18:00:00', '2025-12-05 19:15:00', '2025-12-05 17:45:00'),
(13, 1, 'Arm Isolation Day', 'Biceps and triceps focus', '2025-12-06 14:00:00', '2025-12-06 15:30:00', '2025-12-06 13:45:00'),
(16, 2, 'Upper Body Dips', 'Tricep and chest emphasis', '2025-12-06 11:00:00', '2025-12-06 12:30:00', '2025-12-06 10:45:00'),
(17, 1, 'Core and Mobility', 'Core strengthening and flexibility', '2025-12-07 09:00:00', '2025-12-07 10:00:00', '2025-12-07 08:45:00'),
(20, 2, 'Active Recovery Cardio', 'Light cardio and stretching', '2025-12-07 09:30:00', '2025-12-07 10:30:00', '2025-12-07 09:15:00');

-- ============================================================
-- EXERCISES TABLE DATA
-- This table is populated automatically by the DataLoader.java
-- using the dataset located in:
-- src/main/resources/datasets/exercises_prepared.csv
--
-- The dataset contains 1300+ rows, so it is not inserted manually.
-- The DataLoader handles INSERT / UPSERT at application startup.
-- ============================================================

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
(6, 13, NULL, 4, 10),
(13, 1, NULL, 4, 8),
(13, 12, NULL, 3, 12),
(13, 13, NULL, 4, 10),
(16, 16, NULL, 4, 8),
(16, 1, NULL, 3, 10),
(17, 17, NULL, 3, 30),
(17, 18, NULL, 3, 15),
(20, 20, NULL, 1, 15);



-- This would work if the .csv could load here instead of after app start
-- ==========================
-- WORKOUT_EXERCISES (BASE)
-- ==========================
-- -- Workout 1 — Chest Day
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 1, id, NULL, 4, 8 FROM exercises WHERE name = 'barbell bench press';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 1, id, NULL, 3, 15 FROM exercises WHERE name = 'dumbbell fly';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 1, id, NULL, 3, 12 FROM exercises WHERE name = 'weighted tricep dips';


-- -- Workout 2 — Leg Day
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 2, id, NULL, 5, 5 FROM exercises WHERE name = 'barbell wide squat';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 2, id, NULL, 4, 12 FROM exercises WHERE name = 'lever leg extension';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 2, id, NULL, 3, 10 FROM exercises WHERE name = 'lever lying leg curl';


-- -- Workout 3 — Back Day
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 3, id, NULL, 5, 5 FROM exercises WHERE name = 'barbell deadlift';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 3, id, NULL, 4, 8 FROM exercises WHERE name = 'pull up (neutral grip)';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 3, id, NULL, 3, 12 FROM exercises WHERE name = 'cable seated row';


-- -- Workout 4 — Full Body A
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 4, id, NULL, 3, 10 FROM exercises WHERE name = 'barbell deadlift';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 4, id, NULL, 3, 10 FROM exercises WHERE name = 'barbell bench press';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 4, id, NULL, 3, 12 FROM exercises WHERE name = 'dumbbell goblet squat';


-- -- Workout 5 — Full Body B
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 5, id, NULL, 3, 6 FROM exercises WHERE name = 'dumbbell lunge with bicep curl';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 5, id, NULL, 4, 12 FROM exercises WHERE name = 'dumbbell bent over row';


-- -- Workout 6 — Arms & Shoulders
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 6, id, NULL, 3, 10 FROM exercises WHERE name = 'dumbbell seated shoulder press';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 6, id, NULL, 4, 15 FROM exercises WHERE name = 'dumbbell seated bicep curl';


-- -- Workout 8 — Leg Day Alternate
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 8, id, NULL, 4, 6 FROM exercises WHERE name = 'barbell squat';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 8, id, NULL, 4, 10 FROM exercises WHERE name = 'leg press';


-- -- Workout 11 — Shoulder Press Focus
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 11, id, NULL, 4, 8 FROM exercises WHERE name = 'dumbbell seated shoulder press';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 11, id, NULL, 3, 12 FROM exercises WHERE name = 'lateral raise';


-- -- Workout 13 — Arm Isolation Day
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 13, id, NULL, 4, 10 FROM exercises WHERE name = 'dumbbell seated bicep curl';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 13, id, NULL, 3, 12 FROM exercises WHERE name = 'tricep pushdown';


-- -- Workout 16 — Upper Body Dips
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 16, id, NULL, 4, 8 FROM exercises WHERE name = 'dips';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 16, id, NULL, 3, 10 FROM exercises WHERE name = 'barbell bench press';


-- -- Workout 17 — Core and Mobility
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 17, id, NULL, 3, 30 FROM exercises WHERE name = 'plank';

-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 17, id, NULL, 3, 15 FROM exercises WHERE name = 'crunches';


-- -- Workout 20 — Active Recovery Cardio
-- INSERT INTO workout_exercises (workout_id, exercise_id, time, sets, reps)
-- SELECT 20, id, NULL, 1, 15 FROM exercises WHERE name = 'mountain climbers';

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

-- ==========================
-- WEEKLY_PLAN SAMPLE (MON -> SUN) for users 1, 2, 3
-- ==========================
INSERT INTO weekly_plan (user_id, day, workout_id, notes, created_at) VALUES
-- user 1 (john)
(1, 'MON', 1,  'Bench press focus — heavy sets',                          '2025-12-01 08:00:00'),
(1, 'TUE', 2,  'Leg day — squats & leg press',                           '2025-12-02 10:00:00'),
(1, 'WED', 3,  'Back: barbell row + lat pulldown',                      '2025-12-03 17:00:00'),
(1, 'THU', NULL, '',                       '2025-12-04 06:30:00'),
(1, 'FRI', 1,  'Chest Day: Bench press focus — heavy sets',              '2025-12-05 18:30:00'),
(1, 'SAT', 13, 'Biceps / arms: curls & hammer curls',                   '2025-12-06 14:00:00'),
(1, 'SUN', 17, 'Core & mobility: plank circuit',                       '2025-12-07 09:00:00'),

-- user 2 (sarah)
(2, 'MON', 4,  'Full Body A: Balanced workout',                         '2025-12-01 07:00:00'),
(2, 'TUE', 5,  'Full Body B: Alternate full-body session',              '2025-12-02 18:00:00'),
(2, 'WED', 6,  'Arms and Shoulders: Upper arm isolation',               '2025-12-03 17:30:00'),
(2, 'THU', 4,  'Full Body A: Balanced workout',                         '2025-12-04 06:00:00'),
(2, 'FRI', NULL, '',                    '2025-12-05 19:00:00'),
(2, 'SAT', 16, 'Dips + triceps work',                                    '2025-12-06 11:00:00'),
(2, 'SUN', 20, 'Active recovery — mountain climbers + light cardio',     '2025-12-07 09:30:00'),

-- user 3 (alex)
(3, 'MON', 6,  'Arms and Shoulders: Upper arm isolation',               '2025-12-01 06:00:00'),
(3, 'TUE', NULL,  '',                              '2025-12-02 12:00:00'),
(3, 'WED', 4,  'Full Body A: Balanced workout',                         '2025-12-03 16:00:00'),
(3, 'THU', 5,  'Full Body B: Alternate full-body session',              '2025-12-04 07:00:00'),
(3, 'FRI', 11, 'Shoulder press + accessory work',                       '2025-12-05 18:00:00'),
(3, 'SAT', 6,  'Arms and Shoulders: Upper arm isolation',               '2025-12-06 15:00:00'),
(3, 'SUN', 17, 'Core and mobility',                                      '2025-12-07 10:00:00');

SET FOREIGN_KEY_CHECKS = 1;
