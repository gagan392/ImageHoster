package com.upgrad.ImageHoster.common;

import com.upgrad.ImageHoster.model.Comment;
import org.hibernate.Query;
import org.hibernate.Session;

public class CommentManager extends SessionManager {

    public Comment saveComment(final Comment comment) {
        Session session = openSession();
        session.save(comment);
        commitSession(session);
        return comment;
    }

    public void deleteById(final int id) {
        Session session = openSession();
        Query query = session.createQuery("Delete from " + Comment.class.getName() + " where id=:id");
        query.setParameter("id", id);
        query.executeUpdate();
        commitSession(session);
    }

}
