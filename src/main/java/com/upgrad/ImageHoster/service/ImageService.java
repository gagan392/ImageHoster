package com.upgrad.ImageHoster.service;

import com.upgrad.ImageHoster.model.Comment;
import com.upgrad.ImageHoster.model.Image;

import java.util.List;

public interface ImageService{
    List<Image> getAll();
    List<Image> getByTag(String tagName);
    // changed methodname and input param to return image on id
    Image getById(int id);
    Image getByIdWithJoin(int id);
    void deleteById(Image image);
    int save(Image image);
    void update(Image image);
}