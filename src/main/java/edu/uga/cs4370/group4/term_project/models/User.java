package edu.uga.cs4370.group4.term_project.models;

public class User {

    private int id;
    private String email;
    private String uname;
    private String password;
    private String createdAt;   

    // ---------- Constructors ----------
    public User() { }

    public User(int id, String email, String uname, String password, String createdAt) {
        this.id = id;
        this.email = email;
        this.uname = uname;
        this.password = password;
        this.createdAt = createdAt;   
    }

    public User(String email, String uname, String password) {
        this.email = email;
        this.uname = uname;
        this.password = password;
    }

    // ---------- Getters & Setters ----------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
