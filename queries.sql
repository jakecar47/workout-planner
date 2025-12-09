-- upserts an exercise (inserts if not exists, updates if exists)
INSERT INTO exercises (name, target_muscle, image_path, description)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                target_muscle = VALUES(target_muscle),
                image_path = VALUES(image_path),
                description = VALUES(description);

-- creates a new exercise
INSERT INTO exercises (name, target_muscle, description, image_path)
            VALUES (?, ?, ?, ?);

-- retrieves all exercises ordered by name
SELECT id, name, target_muscle, description, image_path
            FROM exercises
            ORDER BY name ASC;

-- search exercises by name
SELECT id, name, target_muscle, description, image_path
            FROM exercises
            WHERE LOWER(name) LIKE ?
            ORDER BY name ASC;

-- retrieves exercise by id
SELECT id, name, target_muscle, description, image_path
            FROM exercises
            WHERE id = ?;

-- updates an existing exercise
UPDATE exercises
            SET name = ?, target_muscle = ?, description = ?, image_path = ?
            WHERE id = ?;

-- deletes an exercise by id
DELETE FROM exercises WHERE id = ?;

-- create a new goal
INSERT INTO goals (description, user_id, exercise_id) VALUES (?, ?, ?);

-- retrieves all goals for a user ordered by completion status and creation date
SELECT id, user_id, description, exercise_id, created_at
            FROM goals WHERE user_id = ? ORDER BY COALESCE(completed, 0) DESC, created_at DESC;

-- retrieves all goals for a user ordered by creation date
SELECT id, description, user_id, exercise_id, created_at
            FROM goals
            WHERE user_id = ?
            ORDER BY created_at DESC;

-- toggles goal completion status
UPDATE goals SET completed = NOT IFNULL(completed, 0) WHERE id = ? AND user_id = ?;

-- gets a goal by id
SELECT id, description, user_id, exercise_id, created_at, completed
            FROM goals WHERE id = ?;

-- updates an existing goal
UPDATE goals SET description = ?, exercise_id = ? WHERE id = ?;

-- deletes a goal by id
DELETE FROM goals WHERE id = ?;

-- registers a user
INSERT INTO users (email, uname, password) VALUES (?, ?, ?);

-- retrieves a user by email
SELECT * FROM users WHERE email = ?;

-- retrieves a user by id
SELECT * FROM users WHERE id = ?;

-- retrieves user id by username
SELECT id FROM users WHERE uname = ?;

-- retrieves user id by email
SELECT id FROM users WHERE email = ?;

-- retrieves a user by username
SELECT id, email, uname, password, created_at FROM users WHERE uname = ?;

-- retrieves a user by id
SELECT id, email, uname, password, created_at FROM users WHERE id = ?;

-- updates an existing workout
UPDATE workouts
SET name = ?, description = ?, startTime = ?, endTime = ?
WHERE id = ?;

-- schedules or updates a workout in the weekly plan
INSERT INTO weekly_plan (user_id, day, workout_id, notes)
VALUES (?, ?, ?, ?)
ON DUPLICATE KEY UPDATE workout_id = VALUES(workout_id),
                        notes = VALUES(notes), created_at = CURRENT_TIMESTAMP;

-- removes a workout from the weekly plan
DELETE FROM weekly_plan
WHERE user_id = ? AND day = ?;

-- retrieves a workout by id
SELECT * FROM workouts WHERE id = ?;

-- retrieves all workouts for a user ordered by day of the week
SELECT id, user_id, day, workout_id, notes, created_at
            FROM weekly_plan
            WHERE user_id = ?
            ORDER BY FIELD(day,'MON','TUE','WED','THU','FRI','SAT','SUN'), created_at DESC;

-- retrieves a workout for a user by day
SELECT id, user_id, day, workout_id, notes, created_at
            FROM weekly_plan
            WHERE user_id = ? AND day = ?
            LIMIT 1;

-- retrieves the weekly plan for a user
SELECT * FROM weekly_plan WHERE user_id = ?;

-- retrieves user id by username
SELECT id FROM users WHERE username = ?;

-- add exercise to workout
INSERT INTO workout_exercises
            (workout_id, exercise_id, time, sets, reps)
            VALUES (?, ?, ?, ?, ?);

-- retrieves all exercises for a workout
SELECT
                we.workout_id,
                we.exercise_id,
                e.name AS exercise_name,
                we.time,
                we.sets,
                we.reps
            FROM workout_exercises we
            JOIN exercises e ON we.exercise_id = e.id
            WHERE we.workout_id = ?
            ORDER BY e.name ASC;

-- update workout exercise
UPDATE workout_exercises
            SET time = ?, sets = ?, reps = ?
            WHERE workout_id = ? AND exercise_id = ?;

-- deletes an exercise from a workout
DELETE FROM workout_exercises
            WHERE workout_id = ? AND exercise_id = ?;

-- deletes all exercises from a workout
DELETE FROM workout_exercises
            WHERE workout_id = ?;

-- creates a workout
INSERT INTO workouts (name, user_id, description, startTime, endTime) VALUES (?, ?, ?, ?, ?);

-- creates a workout without start and end time
INSERT INTO workouts (name, user_id, description) VALUES (?, ?, ?);

-- gets a workout for a user
SELECT id, name, user_id, description, startTime, endTime, created_at
            FROM workouts
            WHERE user_id = ?
            ORDER BY created_at DESC;

-- gets a workout by id
SELECT id, name, user_id, description, startTime, endTime, created_at
            FROM workouts
            WHERE id = ?;

-- deletes a workout by id
DELETE FROM workouts WHERE id = ?;

-- retrieves all workouts for a user ordered by creation date
SELECT * FROM workouts WHERE user_id = ? ORDER BY created_at DESC;

-- searches workouts by name for a user
SELECT * FROM workouts WHERE LOWER(name) LIKE ? ORDER BY created_at DESC;

-- retrieves all workouts ordered by creation date
SELECT * FROM workouts ORDER BY created_at DESC;