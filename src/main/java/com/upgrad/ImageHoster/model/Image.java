package com.upgrad.ImageHoster.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


@Entity
@Table(name = "Image")
public class Image implements Serializable{
    // These annotations auto-increments the id column for us whenever
    // a new Image object is stored into the database
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String title;

    @Column
    private String description;

    // Text is a Postgres specific column type that allows you to save
    // text based data that will be longer than 256 characters
    @Column(columnDefinition="TEXT")
    private String imageFile; // this is a base64 encoded version of the image

    @Column
    private int numView;

    @Column
    private LocalDate uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // These  annotations creates a join table for many-to-many relationships
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="Image_Tag",
        joinColumns = { @JoinColumn(name = "image_id")},
        inverseJoinColumns = { @JoinColumn(name = "tag_id")})
    private List<Tag> tags = new ArrayList<Tag>();

    // Image has OneToMany relationship with an Comment entity, as image can have multiple comments but not single comment on multiple images.
    // List of Comment entities is not necessary to be loaded, along with an Image entity, for all type of CRUD changes on Image entity. Hence choosing fetchType as LAZY.
    // This needs a manually step of loading all the comments of an image into the hibernate system.
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "image")
    private List<Comment> comments = new ArrayList<Comment>();

    public Image() { }

    public Image(String title, String description, String imageFile, User user, List<Tag> tags) {
        this.description = description;
        this.title = title;
        this.imageFile = imageFile;
        this.numView = 0;
        this.user = user;
        this.uploadDate = LocalDate.now();
        this.tags = tags;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageFile() {
        return this.imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getDescription() { return this.description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumView() {
        return numView;
    }

    public void setNumView(int numView) {
        this.numView = numView;
    }

    public String getUploadDate() {
        return uploadDate.toString();
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setUser(User user) { this.user = user; }

    public User getUser() { return this.user; }

    public List<Tag> getTags() { return tags; }

    public void setTags(List<Tag> tags) { this.tags = tags; }

    public void setId(int id) {
        this.id = id;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
