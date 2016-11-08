package com.collaboration.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.collaboration.model.Comments;
import com.collaboration.model.Posts;

@Controller
public class PageController {

	private static final Logger logger = LoggerFactory.getLogger(PageController.class);
		
	
	@RequestMapping(value = {"/home", "/"})
	public String home() {				
		return "guest/home";
	}

	@RequestMapping(value = "/contact")
	public String contact() {
		return "guest/contact";
	}

	@RequestMapping(value = "/about")
	public String about() {
		return "guest/about";
	}
	

	@RequestMapping(value = "/all/blogs")
	public String listBlog() {
		return "blog/listBlog";
	}		

	@RequestMapping(value = "/view/blog/{id}")
	public String viewBlog(@PathVariable("id")String blogId, Model model) {
		Comments comment = new Comments();
		model.addAttribute("comment", comment);
		model.addAttribute("blogId", blogId);
		return "blog/viewBlog";
	}		
	
	
	@RequestMapping(value = "/public/forums")
	public String forums() {
		return "forum/listForum";
	}		

	@RequestMapping(value = "/public/view/forum/{forumId}")
	public String viewPublicForum(@PathVariable("forumId") String forumId, Model model) {
		model.addAttribute("forumId", forumId);
		model.addAttribute("forumPost", new Posts());
		return "forum/viewForum";
	}
	
	
	
	@RequestMapping(value = "/user")
	public String user() {
		return "user/index";
	}		

	
	@RequestMapping(value = "/admin")
	public String admin() {
		return "admin/index";
	}		
	
	@RequestMapping(value = "/viewForum/{forumId}")
	public String viewForum(@PathVariable("forumId") String forumId, Model model) {
		model.addAttribute("forumId", forumId);
		return "admin/forum";
	}

	
	

}
