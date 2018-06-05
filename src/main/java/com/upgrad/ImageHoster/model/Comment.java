package com.upgrad.ImageHoster.model;

import javax.persistence.*;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Comment has ManyToOne relationship with User entity, as a user can give multiple comments but not single comment by multiple user.
    // User entity should be loaded along with each comment. Since there are multiple users in the system, this helps to show the username of user who commented the text. Hence choosing fetchType as EAGER.
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column
    private String text;

    // Comment has ManyToOne relationship with an Image entity, as image can have multiple comments but not single comment on multiple images.
    // Image entity is not necessary to be loaded along with each comment. Hence choosing fetchType as LAZY.
    // This needs a manually step of loading all the comments of an image into the hibernate system.
    @ManyToOne(fetch = FetchType.LAZY)
    private Image image;

    public Comment() {
    }

    public Comment(String text, User user) {
        this.text = text;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
