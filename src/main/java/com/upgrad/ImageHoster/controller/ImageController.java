package com.upgrad.ImageHoster.controller;

import com.upgrad.ImageHoster.model.Comment;
import com.upgrad.ImageHoster.model.Image;
import com.upgrad.ImageHoster.model.Tag;
import com.upgrad.ImageHoster.model.User;
import com.upgrad.ImageHoster.service.CommentService;
import com.upgrad.ImageHoster.service.ImageService;
import com.upgrad.ImageHoster.service.TagService;
import com.upgrad.ImageHoster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;


@Controller
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    /**
     * This controller method returns all the images that have been
     * uploaded to the website
     *
     * @param model used to pass data to the view for rendering
     *
     * @return the homepage view
     */
    @RequestMapping("/")
    public String listImages(Model model) {
        List<Image> images = imageService.getAll();
        model.addAttribute("images", images);

        return "home";
    }

    /**
     * This controller method renders an image upload form
     *
     * @param session a HTTP session that tells us if the current user
     *                is logged in
     *
     * @return the image upload form view
     */
    @RequestMapping("/images/upload")
    public String uploadImage(HttpSession session) {
        // retrieves the current user from the HTTP session
        User currUser = (User) session.getAttribute("currUser");

        // currUser is null means that the user is not logged in
        // therefore redirect the user back to the home page
        if (currUser == null) {
            return "redirect:/";
        } else {
            return "images/upload";
        }
    }

    /**
     * This controller method displays an image edit form, so the user
     * can update the image's description and uploaded file
     *
     * @param id    id of the image that we want to edit
     * @param model used to pass data to the view for rendering
     *
     * @return the image edit form view
     */
    // changing path variable to id instead of title of an image
    @RequestMapping("/images/{id}/edit")
    public String editImage(@PathVariable String id, Model model) {
        Image image = imageService.getByIdWithJoin(Integer.valueOf(id));
        String tags = convertTagsToString(image.getTags());

        model.addAttribute("image", image);
        model.addAttribute("tags", tags);

        return "images/edit";
    }

    /**
     * This controller method retrieves the data that the user entered
     * into the image uploader form, and creates an image
     *
     * @param title       title of the uploaded image
     * @param description description of the uploaded image
     * @param file        the image to be uploaded
     * @param tags        tags (i.e. categories) for the images
     * @param session     HTTP session that tells us if the user is logged in
     *
     * @return view for the uploaded image
     *
     * @throws IOException
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("title") String title,
                         @RequestParam("description") String description,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam("tags") String tags,
                         HttpSession session) throws IOException {
        User currUser = (User) session.getAttribute("currUser");

        // if the user is not logged in, redirect to the home page
        if (currUser == null) {
            return "redirect:/";
        } else {
            List<Tag> imageTags = findOrCreateTags(tags);
            String uploadedImageData = convertUploadedFileToBase64(file);

            Image newImage = new Image(title, description, uploadedImageData, currUser, imageTags);
            int id = imageService.save(newImage);

            // redirecting to an image based on image id
            return "redirect:/images/" + id;
        }
    }

    /**
     * This controller shows a specific image
     *
     * @param id    the id of the image that we want to retrieve
     * @param model used to pass data to the view for rendering
     *
     * @return view for the image that was requested
     */
    // changing path variable to id instead of title of an image
    @RequestMapping("/images/{id}")
    public String showImage(@PathVariable String id, Model model) {
        Image image = imageService.getByIdWithJoin(Integer.valueOf(id));
        image.setNumView(image.getNumView() + 1);
        imageService.update(image);

        model.addAttribute("user", image.getUser());
        model.addAttribute("image", image);
        model.addAttribute("tags", image.getTags());
        model.addAttribute("comments", image.getComments());

        return "images/image";
    }

    /**
     * This method deletes a specific image from the database
     *
     * @param id id of the image that we want to delete
     *
     * @return redirects the user to the homepage view
     */
    // changing path variable to id instead of title of an image
    @RequestMapping("/images/{id}/delete")
    public String deleteImage(@PathVariable String id) {
        Image image = imageService.getByIdWithJoin(Integer.valueOf(id));
        deleteCommentsByImage(image);
        imageService.deleteById(image);


        return "redirect:/";
    }

    /**
     * This controller method updates the image that we wanted to edit
     *
     * @param title       title of the image that we want to edit
     * @param description the updated description for the image
     * @param file        the updated file for the image
     * @param tags        the updated tags for the image
     *
     * @return the view for the updated image
     *
     * @throws IOException
     */
    @RequestMapping(value = "/editImage", method = RequestMethod.POST)
    // reading id param from the request
    public String edit(@RequestParam("id") String id,
                       @RequestParam("title") String title,
                       @RequestParam("description") String description,
                       @RequestParam("file") MultipartFile file,
                       @RequestParam("tags") String tags) throws IOException {
        Image image = imageService.getById(Integer.valueOf(id));
        List<Tag> imageTags = findOrCreateTags(tags);
        String updatedImageData = convertUploadedFileToBase64(file);

        image.setTitle(title);
        image.setDescription(description);
        if (!updatedImageData.isEmpty())
            image.setImageFile(updatedImageData);
        else
            image.setImageFile(image.getImageFile());
        image.setTags(imageTags);
        imageService.update(image);

        // changing path variable to id instead of title of an image
        return "redirect:/images/" + id;
    }

    /**
     * This help function converts an image file to base64 encoding, which
     * makes it easier for us to store the image data in the datbase
     *
     * @param file the file that we want to convert to base64 encoding
     *
     * @return base64 encoding of the file that was passed into this function
     *
     * @throws IOException
     */
    private String convertUploadedFileToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    /**
     * This is a helper function that checks if a tag has been created and
     * saved into the DB. If the tag has not saved DB, this function
     * will create the tag
     *
     * @param tagNames a String that represents the tags, and tagNames
     *                 can be comma delimited to represent multiple Tags
     *                 i.e. "cat, young, kitty" to represent the tags:
     *                 "cat", "young", and "kitty"
     *
     * @return Tag objects stored in a List
     */
    private List<Tag> findOrCreateTags(String tagNames) {
        // converts a comma delimited String into a String Tokenizer
        StringTokenizer st = new StringTokenizer(tagNames, ",");
        List<Tag> tags = new ArrayList<Tag>();

        // for each token in the Tokenizer
        while (st.hasMoreTokens()) {
            // trim any white spaces before or after the String token
            String tagName = st.nextToken().trim();
            // check if the associated Tag has been created and stored in the database
            Tag tag = tagService.getByName(tagName);

            // if the associated Tag has not been created, create the tag
            // and store it in the database
            if (tag == null) {
                Tag newTag = new Tag(tagName);
                tag = tagService.createTag(newTag);
            }

            tags.add(tag);
        }

        return tags;
    }

    /**
     * This method takes a List of Tag objects and convert it back
     * to its comma delimited String. This method is needed for the
     * image edit form
     *
     * @param tags a List of Tag objects
     *
     * @return A comma delimited, String version of the Tag objects
     */
    private String convertTagsToString(List<Tag> tags) {
        String tagString = "";

        for (int i = 0; i <= tags.size() - 2; i++) {
            tagString += tags.get(i).getName() + ", ";
        }

        Tag lastTag = tags.get(tags.size() - 1);
        tagString += lastTag.getName();

        return tagString;
    }

    /**
     * This method takes an image and deletes its associated comments. This method is needed while deleting image.
     * @param image as Image object
     */
    public void deleteCommentsByImage(Image image) {
        List<Comment> comments = image.getComments();

        for (Comment comment : comments) {
            commentService.deleteComment(comment.getId());
        }
    }
}
