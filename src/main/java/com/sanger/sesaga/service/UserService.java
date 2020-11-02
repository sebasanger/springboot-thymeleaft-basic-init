package com.sanger.sesaga.service;

import java.util.List;

import com.sanger.sesaga.entity.User;
import com.sanger.sesaga.web.dto.UserDto;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

	User findByUserName(String userName);

	User findById(Long id);

	User findByEmail(String email);

	User save(UserDto user, boolean sabeRole);

	List<User> findAll();

	Page<User> getAllUsers(Integer pageNo, Integer pageSize, String sortBy, String sortDir, String fitler);

	void deleteUser(User user);

	void changeUserPassword(User user, String password);

	boolean checkIfValidOldPassword(Long id, String password);

}
