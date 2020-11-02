package com.sanger.sesaga.web.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.sanger.sesaga.config.Utility;
import com.sanger.sesaga.entity.Role;
import com.sanger.sesaga.entity.User;
import com.sanger.sesaga.service.RoleService;
import com.sanger.sesaga.service.UserService;
import com.sanger.sesaga.service.VerificationTokenService;
import com.sanger.sesaga.web.dto.UserDto;
import com.sanger.sesaga.web.error.UserAlreadyExistException;
import com.sanger.sesaga.web.error.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private VerificationTokenService verificationTokenService;

	@Autowired
	private MessageSource messages;

	private final int initPage = 1;
	private final int initPerPage = 5;
	private final String initSort = "id";
	private final String initOrderBy = "asc";
	private final String initFilter = "";

	@GetMapping("/list")
	public String listUsers(Model model) {
		return listByPage(initPage, initPerPage, initSort, initOrderBy, initFilter, model);
	}

	@GetMapping("/list/page/{pageNumber}")
	public String listByPage(@PathVariable("pageNumber") Integer pageNo, @Param("pageSize") Integer pageSize,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("filter") String filter,
			Model model) {

		Page<User> page = userService.getAllUsers(pageNo, pageSize, sortField, sortDir, filter);

		List<User> users = page.getContent();

		model.addAttribute("users", users);
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);

		String filterBy = filter == null ? "" : filter;

		model.addAttribute("filterBy", filterBy);

		String reverseSort = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("reverseSort", reverseSort);

		model.addAttribute("init", (pageSize * pageNo) - pageSize);
		model.addAttribute("finish", pageSize * pageNo);

		return "users-control";
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
		User user = userService.findById(id);
		userService.deleteUser(user);
		redirectAttributes.addFlashAttribute("messageSuccess",
				messages.getMessage("user.deleted", null, Locale.getDefault()));
		return "redirect:/users/list";
	}

	@GetMapping("/add")
	public String showMyLoginPage(Model theModel) {
		theModel.addAttribute("rolesList", roleService.findAll());
		theModel.addAttribute("user", new User());

		return "user-form";
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {

		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}

	public boolean saveUser(@Valid @ModelAttribute("user") UserDto user, Errors errors, Model theModel,
			boolean updatePassword) {

		if (errors.hasErrors()) {
			List<Role> roles = roleService.findAll();
			theModel.addAttribute("rolesList", roles);
			return false;
		}

		try {
			if (updatePassword) {
				userService.save(user, true);
			} else {
				userService.save(user, false);
			}

			return true;

		} catch (UserAlreadyExistException e) {
			List<Role> roles = roleService.findAll();
			theModel.addAttribute("messageError", e.getMessage());
			theModel.addAttribute("rolesList", roles);

			return false;
		}

	}

	@PostMapping("/save")
	public String processRegistrationForm(@Valid @ModelAttribute("user") UserDto userDto, Errors errors, Model model,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {
		boolean result = saveUser(userDto, errors, model, true);
		if (result) {
			if (userDto.getId() == null) {
				String url = Utility.getSiteURL(request);
				verificationTokenService.createVerificationTokenForUser(userDto.getUserName());
				verificationTokenService.sendEmailVerification(userDto.getUserName(), url, userDto.getPassword());
				redirectAttributes.addFlashAttribute("messageSuccess",
						"User save, email with the instructions sent to the user");
				return "redirect:/users/list";
			} else {
				redirectAttributes.addFlashAttribute("messageSuccess", "User updated");
				return "redirect:/users/list";
			}

		} else {
			return "user-form";
		}

	}

	@PostMapping("/save-acount")
	public String saveAccount(@Valid @ModelAttribute("user") UserDto user, Errors errors, Model model,
			RedirectAttributes redirectAttributes) {
		boolean result = saveUser(user, errors, model, false);
		if (result) {
			redirectAttributes.addFlashAttribute("messageSuccess", "Acount updated");
			return "redirect:/";
		} else {
			return "update-acount";
		}

	}

	@GetMapping("/edit/{id}")
	public String edit(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
		User user = new User();
		try {
			user = userService.findById(id);
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("messageError", "User not found");
			return "redirect:/";
		}
		List<Role> roles = roleService.findAll();
		model.addAttribute("rolesList", roles);
		model.addAttribute("user", user);

		return "user-form";
	}

	@GetMapping("/update-acount/{userName}")
	public String updateAcount(Model model, @PathVariable String userName, RedirectAttributes redirectAttributes) {
		User user = new User();
		try {
			user = userService.findByUserName(userName);
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("messageError", "User not found");
			return "redirect:/";
		}

		model.addAttribute("user", user);
		return "update-acount";
	}

	@GetMapping("/update-password/{userName}")
	public String updatePassword(Model model, @PathVariable String userName) {
		model.addAttribute("user", userService.findByUserName(userName));

		return "update-password";
	}

	@PostMapping("/save-password")
	public String updatePassword(@ModelAttribute("user") User user,
			@RequestParam(name = "repeat-password", required = true) String passwordRepeat,
			@RequestParam(name = "current-password", required = true) String currentPassword, Model theModel,
			RedirectAttributes redirectAttributes) {

		if (!passwordRepeat.equals(user.getPassword())) {
			theModel.addAttribute("messageError", "Password not mismatch");
			return "update-password";
		}

		Boolean currentPasswordValid = userService.checkIfValidOldPassword(user.getId(), currentPassword);

		if (!currentPasswordValid) {
			theModel.addAttribute("messageError", "invalid current password");
			return "update-password";
		}

		userService.changeUserPassword(user, user.getPassword());
		redirectAttributes.addFlashAttribute("messageSuccess", "Password updated");
		return "redirect:/";
	}

}
