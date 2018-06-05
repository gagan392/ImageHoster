package com.upgrad.ImageHoster.controller;

import com.upgrad.ImageHoster.model.Comment;
import com.upgrad.ImageHoster.model.Image;
import com.upgrad.ImageHoster.model.User;
import com.upgrad.ImageHoster.service.CommentService;
import com.upgrad.ImageHoster.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class CommentController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/image/{id}/comments/create", method = RequestMethod.POST)
    public String createCommentByImageId(@PathVariable("id") String id,
                                         @RequestParam("comment") String text,
                                         HttpSession session) {

        User currUser = (User) session.getAttribute("currUser");
        Image image = imageService.getById(Integer.valueOf(id));
        Comment newComment = new Comment(text, currUser);
        newComment.setImage(image);
        commentService.saveComment(newComment);

        return "redirect:/images/" + id;
    }
}
