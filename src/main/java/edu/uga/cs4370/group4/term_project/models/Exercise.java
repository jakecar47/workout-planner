package edu.uga.cs4370.group4.term_project.models;

public class Exercise {

    private int id;
    private String name;
    private String targetMuscle;
    private String description;
    private String imagePath; 

    // ---------- Constructors ----------

    public Exercise() { }

    // Constructor used by services & repositories
    public Exercise(int id, String name, String targetMuscle, String description) {
        this.id = id;
        this.name = name;
        this.targetMuscle = targetMuscle;
        this.description = description;
        this.imagePath = null;
    }

    // Full constructor 
    public Exercise(int id, String name, String targetMuscle,
                    String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.targetMuscle = targetMuscle;
        this.description = description;
        this.imagePath = imagePath;
    }

    // Constructor used during creation
    public Exercise(String name, String targetMuscle,
                    String description, String imagePath) {
        this.name = name;
        this.targetMuscle = targetMuscle;
        this.description = description;
        this.imagePath = imagePath;
    }

    // ---------- Getters & Setters ----------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTargetMuscle() { return targetMuscle; }
    public void setTargetMuscle(String targetMuscle) { this.targetMuscle = targetMuscle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
