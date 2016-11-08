package com.collaboration.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.collaboration.model.Categories;
import com.collaboration.model.Forums;
import com.collaboration.services.CategoryService;
import com.collaboration.services.ForumService;

@Controller
public class AdminController {

	
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private ForumService forumService;
	
	
	@Autowired
	private CategoryService categoryService;

	@RequestMapping(value = "/admin/forums/view")
	public String viewForum(@RequestParam(value = "operation", required = false) String operation, Model model) {
		if(operation!=null) {
			model.addAttribute("operation", "Updated the forum record!");
		}
		return "admin/forums";
	}
	
	
	@RequestMapping(value = "/admin/forum/add")
	public String newForum(Model model) {
		String op = "Create";
		Forums forum = new Forums();
		forum.setNewForum(true);
		model.addAttribute("categories", this.categoryService.getCategories());
		model.addAttribute("operation", op);
		model.addAttribute("forum", forum);
		return "admin/cuForum";
	}
		
	@RequestMapping(value = "/admin/forum/edit/{id}")
	public String editForum(@PathVariable(value = "id") String id, Model model) {
		String op = "Edit";
		Forums forum = this.forumService.get(id);
		model.addAttribute("categories", this.categoryService.getCategories());
		model.addAttribute("operation", op);
		model.addAttribute("forum", forum);
		return "admin/cuForum";
	}

	
	@RequestMapping(value = "/admin/forum/save", method = RequestMethod.POST)
	public String saveForum(@Valid @ModelAttribute("forum") Forums forum, BindingResult results, Model model) {
		if(results.hasErrors()) {			
			return "admin/cuForum";
		}		
		logger.debug(forum.toString());
		this.forumService.add(forum);
		return "redirect:/admin/forums/view?operation=success";		
	}
	
	@RequestMapping(value = "/admin/categories/view")
	public String newCategory(Model model) {
		Categories c = new Categories();
		model.addAttribute("category", c);
		return "category/categories";
	}

	@RequestMapping(value = "/admin/category/save", method = RequestMethod.POST)
	public String saveCategory(@Valid @ModelAttribute("category") Categories cat, BindingResult results, Model model) {
		if(results.hasErrors()) {			
			return "admin/cuForum";
		}		
		logger.debug(cat.toString());
		this.categoryService.add(cat);
		return "redirect:/admin/categories/view?operation=success";		
	}
}

