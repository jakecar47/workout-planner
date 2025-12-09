package edu.uga.cs4370.group4.term_project.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class WeeklyPlan {

    private Integer id;
    private int userId;
    private String day;
    private Integer workoutId;
    private String notes;
    private LocalDateTime createdAt;

    public WeeklyPlan() {}

    public WeeklyPlan(Integer id, int userId, String day, Integer workoutId, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.day = day;
        this.workoutId = workoutId;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Integer workoutId) {
        this.workoutId = workoutId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeeklyPlan that = (WeeklyPlan) o;
        return userId == that.userId &&
               workoutId == that.workoutId &&
               Objects.equals(id, that.id) &&
               Objects.equals(day, that.day) &&
               Objects.equals(notes, that.notes) &&
               Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, day, workoutId, notes, createdAt);
    }

    @Override
    public String toString() {
        return "WeeklyPlan{" +
                "id=" + id +
                ", userId=" + userId +
                ", day='" + day + '\'' +
                ", workoutId=" + workoutId +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}