package com.sanger.sesaga.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.sanger.sesaga.entity.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public List<User> findAll() {
		Session currentSession = entityManager.unwrap(Session.class);
		List<User> users = currentSession.createQuery("from User", User.class).getResultList();

		return users;

	}

	@Override
	public void updateNotPasswordNotRole(User user) {
		Session currentSession = entityManager.unwrap(Session.class);
		if (user.getId() != null && user.getId() != 0) {
			currentSession.createQuery(
					"update User set firstName =:fistName, lastName=:lastName, userName =:userName, email =:email where id =:id")
					.setParameter("fistName", user.getFirstName()).setParameter("lastName", user.getLastName())
					.setParameter("userName", user.getUserName()).setParameter("email", user.getEmail())
					.setParameter("id", user.getId()).executeUpdate();
		}

	}

	@Override
	public void updateNotPassword(User user) {
		Session currentSession = entityManager.unwrap(Session.class);
		User userEdited = currentSession.get(User.class, user.getId());
		userEdited.setEmail(user.getEmail());
		userEdited.setFirstName(user.getFirstName());
		userEdited.setLastName(user.getLastName());
		userEdited.setRoles(user.getRoles());
		userEdited.setUserName(user.getUserName());
		currentSession.merge(userEdited);
	}

	@Override
	public void updatePassword(User user) {
		Session currentSession = entityManager.unwrap(Session.class);
		User userEdited = currentSession.get(User.class, user.getId());
		userEdited.setPassword(user.getPassword());
		currentSession.merge(userEdited);
	}

	@Override
	public void validate(Long id) {
		Session currentSession = entityManager.unwrap(Session.class);

		User userEdited = currentSession.get(User.class, id);
		userEdited.setEnable(true);
		currentSession.merge(userEdited);
	}

}
