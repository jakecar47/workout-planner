-- creates a new exercise
INSERT INTO exercises (name, target_muscle, description, image_path)
            VALUES (?, ?, ?, ?)

-- retrieves all exercises ordered by name
SELECT id, name, target_muscle, description, image_path
            FROM exercises
            ORDER BY name ASC

-- search exercises by name
SELECT id, name, target_muscle, description, image_path
            FROM exercises
            WHERE LOWER(name) LIKE ?
            ORDER BY name ASC

-- retrieves exercise by id
SELECT id, name, target_muscle, description, image_path
            FROM exercises
            WHERE id = ?

-- updates an existing exercise
UPDATE exercises
            SET name = ?, target_muscle = ?, description = ?, image_path = ?
            WHERE id = ?

-- deletes an exercise by id
DELETE FROM exercises WHERE id = ?

-- create a new goal
INSERT INTO goals (description, user_id, exercise_id) VALUES (?, ?, ?)

-- retrieves all goals for a user
SELECT id, description, user_id, exercise_id, created_at  +
            FROM goals WHERE user_id = ? ORDER BY created_at DESC

-- gets a goal by id
SELECT id, description, user_id, exercise_id, created_at
            FROM goals WHERE id = ?

-- updates an existing goal
UPDATE goals SET description = ?, exercise_id = ? WHERE id = ?

-- deletes a goal by id
DELETE FROM goals WHERE id = ?

-- registers a user
INSERT INTO users (email, uname, password) VALUES (?, ?, ?)

-- retrieves a user by username
SELECT id, email, uname, password, created_at FROM users WHERE uname = ?

-- retrieves a user by id
SELECT id, email, uname, password, created_at FROM users WHERE id = ?

-- add exercise to workout
INSERT INTO workout_exercises
            (workout_id, exercise_id, time, sets, reps)
            VALUES (?, ?, ?, ?, ?)

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
            ORDER BY e.name ASC

-- update workout exercise
UPDATE workout_exercises
            SET time = ?, sets = ?, reps = ?
            WHERE workout_id = ? AND exercise_id = ?

-- deletes an exercise from a workout
DELETE FROM workout_exercises
            WHERE workout_id = ? AND exercise_id = ?

-- deletes all exercises from a workout
DELETE FROM workout_exercises
            WHERE workout_id = ?

-- creates a workout
INSERT INTO workouts (name, user_id, description, startTime, endTime) VALUES (?, ?, ?, ?, ?)

-- gets a workout for a user
SELECT id, name, user_id, description, startTime, endTime, created_at
            FROM workouts
            WHERE user_id = ?
            ORDER BY created_at DESC

-- gets a workout by id
SELECT id, name, user_id, description, startTime, endTime, created_at
            FROM workouts
            WHERE id = ?

-- updates an existing workout
UPDATE workouts
            SET name = ?, description = ?, startTime = ?, endTime = ?
            WHERE id = ?

-- deletes a workout by id
DELETE FROM workouts WHERE id = ?
