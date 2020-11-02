package com.sanger.sesaga.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String showHome(Model model) {
		model.addAttribute("module", "home");
		return "index";
	}

}
