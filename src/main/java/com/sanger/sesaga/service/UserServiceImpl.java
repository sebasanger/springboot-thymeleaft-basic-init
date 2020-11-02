package com.sanger.sesaga.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

import com.sanger.sesaga.dao.PasswordResetTokenRepository;
import com.sanger.sesaga.dao.RoleDao;
import com.sanger.sesaga.dao.UserDao;
import com.sanger.sesaga.dao.UserRepository;
import com.sanger.sesaga.dao.VerificationTokenRepository;
import com.sanger.sesaga.entity.PasswordResetToken;
import com.sanger.sesaga.entity.Role;
import com.sanger.sesaga.entity.User;
import com.sanger.sesaga.entity.VerificationToken;
import com.sanger.sesaga.web.dto.UserDto;
import com.sanger.sesaga.web.error.UserAlreadyExistException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Page<User> getAllUsers(Integer pageNo, Integer pageSize, String sortBy, String sortDir, String filter) {
		Sort sort = Sort.by(sortBy);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		if (filter != null) {
			return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(filter, filter,
					pageable);
		}
		return userRepository.findAll(pageable);
	}

	@Override
	public User findByUserName(String userName) {
		User user = userRepository.findByUserName(userName);
		if (user == null) {
			throw new UsernameNotFoundException("User with this username not found");
		}
		return user;
	}

	@Override
	public User save(UserDto userDto, boolean saveRole) {
		if (emailExists(userDto)) {
			throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
		}
		if (userNameExists(userDto)) {
			throw new UserAlreadyExistException("There is an account with that username: " + userDto.getUserName());
		}

		final User user = new User();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setUserName(userDto.getUserName());
		user.setEmail(userDto.getEmail());
		user.setId(userDto.getId());
		user.setEnable(userDto.isEnable());
		if (userDto.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		}

		List<Role> rolesSearch = userDto.getRoles();
		List<Role> roles = new ArrayList<>();
		if (rolesSearch != null) {
			for (Role role : rolesSearch) {
				roles.add(roleDao.findRoleByName(role.getName()));
			}
			user.setRoles(roles);
		}

		if (user.getId() == null || user.getId() == 0) {
			userRepository.save(user);
		} else if (saveRole) {
			userDao.updateNotPassword(user);
		} else {
			userDao.updateNotPasswordNotRole(user);
		}

		return user;
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = userRepository.findByUserName(userName);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
				mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	@Override
	public void deleteUser(User user) {
		final VerificationToken verificationToken = verificationTokenRepository.findByUser(user);

		if (verificationToken != null) {
			verificationTokenRepository.delete(verificationToken);
		}

		final PasswordResetToken passwordToken = passwordResetTokenRepository.findByUser(user);

		if (passwordToken != null) {
			passwordResetTokenRepository.delete(passwordToken);
		}
		userRepository.delete(user);

	}

	@Override
	public User findById(Long id) {
		User user = userRepository.findById(id).get();
		if (user == null) {
			throw new UsernameNotFoundException("User with this id not found");
		}
		return user;
	}

	@Override
	public List<User> findAll() {
		return userDao.findAll();
	}

	@Override
	public void changeUserPassword(User user, String password) {
		user.setPassword(passwordEncoder.encode(password));
		userDao.updatePassword(user);
	}

	@Override
	public boolean checkIfValidOldPassword(Long id, String oldPassword) {
		User user = findById(id);
		return passwordEncoder.matches(oldPassword, user.getPassword());
	}

	// ------------------------------------------------------------------------------------//

	private boolean emailExists(UserDto user) {
		User userExist = userRepository.findByEmail(user.getEmail());
		return userExist != null && user.getId() != userExist.getId();
	}

	private boolean userNameExists(UserDto user) {
		User userExist = userRepository.findByUserName(user.getUserName());
		return userExist != null && user.getId() != userExist.getId();
	}

	@Override
	public User findByEmail(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User with this email not found");
		}
		return user;
	}

}
