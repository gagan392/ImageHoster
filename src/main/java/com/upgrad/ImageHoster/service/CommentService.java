package com.upgrad.ImageHoster.service;

import com.upgrad.ImageHoster.model.Comment;

public interface CommentService {

    Comment saveComment(Comment comment);
    void deleteComment(int id);

}
