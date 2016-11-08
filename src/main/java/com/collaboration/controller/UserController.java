package com.collaboration.controller;
import java.io.File;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.collaboration.model.Blogs;
import com.collaboration.model.Comments;
import com.collaboration.model.Posts;
import com.collaboration.model.UserProfiles;
import com.collaboration.services.BlogService;
import com.collaboration.services.CommentService;
import com.collaboration.services.PostService;
import com.collaboration.services.UserProfileService;
import com.collaboration.services.UserServices;

@Controller
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UserProfileService userProfileService;
	
	@Autowired
	private UserServices userService;
	
	@Autowired
	private BlogService blogService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private PostService postService;
	
	@RequestMapping(value ="/user/blogs")
	public String userBlogs(Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserProfiles profile = this.userService.getByUsername(username).getUserProfile();
		model.addAttribute("profile", profile);				
		return "user/blogs";		
	}
	
	
	@RequestMapping(value = "/user/view/blog/{id}")
	public String userBlog(@PathVariable("id") String blogId, Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserProfiles profile = this.userService.getByUsername(username).getUserProfile();
		model.addAttribute("profile", profile);
		model.addAttribute("blogId", blogId);				
		return "user/blog";
	}		
	
	
	@RequestMapping(value = "/user/blog/add")
	public String addBlog(Model model) { 		
		String op = "Create";
		Blogs blog = new Blogs();
		blog.setNewBlog(true);
		model.addAttribute("operation", op);
		model.addAttribute("blog", blog);
		return "user/cuBlog";				
	}
	
	@RequestMapping(value = "/user/blog/edit/{id}")
	public String editForum(@PathVariable(value = "id") String id, Model model) {
		String op = "Edit";
		Blogs blog = this.blogService.get(id);
		model.addAttribute("operation", op);
		model.addAttribute("blog", blog);
		return "user/cuBlog";
	}
	
	
	@RequestMapping(value = "/user/blog/save", method = RequestMethod.POST)
	public String saveForum(@Valid @ModelAttribute("blog") Blogs blog, BindingResult results, Model model) {
		if(results.hasErrors()) {			
			return "user/cuBlog";
		}		
		logger.debug(blog.toString());
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserProfiles profile = this.userService.getByUsername(username).getUserProfile();
		blog.setUserId(profile.getId());		
		this.blogService.add(blog);
		return "redirect:/user/blogs/";		
	}
		

	@RequestMapping(value = "/user/comment/save/{blogId}", method = RequestMethod.POST)
	public String saveComment(@PathVariable("blogId") String blogId, @Valid @ModelAttribute("comment") Comments comment, BindingResult results, Model model) {
		if(results.hasErrors()) {			
			return "blog/viewBlog";
		}		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserProfiles profile = this.userService.getByUsername(username).getUserProfile();
		comment.setUserId(profile.getId());		
		comment.setBlogId(blogId);
		comment.setCommentedAt(new Date());
		this.commentService.add(comment);
		return "redirect:/view/blog/"+blogId;		
	}

	
	@RequestMapping(value = "/user/post/save/{forumId}", method = RequestMethod.POST)
	public String savePost(@PathVariable("forumId") String forumId, @Valid @ModelAttribute("forumPost") Posts post, BindingResult results, Model model) {
		if(results.hasErrors()) {			
			return "forum/viewForum";
		}		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserProfiles profile = this.userService.getByUsername(username).getUserProfile();
		post.setUserId(profile.getId());		
		post.setForumId(forumId);
		post.setPostedAt(new Date());
		this.postService.add(post);
		return "redirect:/public/view/forum/"+forumId;		
	}	
	
	
	@RequestMapping(value = "/user/edit/{userId}")
	public String editUser(@PathVariable("userId") String userId, Model model) {
		UserProfiles profile = this.userService.get(userId).getUserProfile();
		model.addAttribute("profile", profile);
		model.addAttribute("operation", "Edit");
		return "user/editProfile";		
	}		
	
	
	@RequestMapping(value = "/user/profile/save", method = RequestMethod.POST)
	public String saveProfile(@Valid @ModelAttribute("profile") UserProfiles profile, BindingResult results, Model model) { 
		if(results.hasErrors()) {
			return "user/editProfile";
		}
		
		// call to upload the file
		if(!(profile.getFile().getOriginalFilename().equals(""))) {
			profile.setAvatar(uploadImage(profile.getFile(), profile.getId()));
		}
		
		profile.setModifiedAt(new Date());
		this.userProfileService.add(profile);		
				
		return "redirect:/user/dashboard";
		
	}
	
	
	// helper to upload the file
	protected String uploadImage(MultipartFile multipartFile, String userId) {
		
		String folderToUpload = "/resources/images/profile/";
		
		//1. get the filename
		String filename = multipartFile.getOriginalFilename();
		
		// 2. get the real path and create the directory
		// if it does not exists
		String realPath = request.getServletContext().getRealPath(folderToUpload);
		if(!new File(realPath).exists()) {
			new File(realPath).mkdirs();
		}
						
		// 3. transfer the file
		String filePath = realPath + "\\" + userId + filename.substring(filename.lastIndexOf("."));
		File destination = new File(filePath);
		try { multipartFile.transferTo(destination);	}
		catch(Exception ex) {}
		
		// 4. Return the filename
		return userId + filename.substring(filename.lastIndexOf("."));		
		
	}
		
	
	@RequestMapping(value = "/user/remove/blog/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void removeUserBlog(@PathVariable("id") String blogId, Principal principal) {		
		Blogs blog = this.blogService.get(blogId);

		// First remove all the comments associated with the blog
		List<Comments> comments  = this.commentService.getComments(blogId);
		if(comments !=null) {
			for(Comments comment: comments) {
				this.commentService.remove(comment);
			}
		}		
		// remove the blog
		this.blogService.remove(blog);	
	}		
	
	
}
