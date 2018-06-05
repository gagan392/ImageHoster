package com.upgrad.ImageHoster.service;

import com.upgrad.ImageHoster.model.Image;

import java.util.List;

public interface ImageService{
    List<Image> getAll();
    List<Image> getByTag(String tagName);
    // changed method name and input param to return image specific to image id
    Image getById(int id);
    // changed method name and input param to return image specific to image id
    Image getByIdWithJoin(int id);
    void deleteByTitle(Image image);
    void save(Image image);
    void update(Image image);
}