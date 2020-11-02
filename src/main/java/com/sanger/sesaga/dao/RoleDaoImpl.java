package com.sanger.sesaga.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.sanger.sesaga.entity.Role;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl implements RoleDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public Role findRoleByName(String theRoleName) {

		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);

		// now retrieve/read from database using name
		Query<Role> theQuery = currentSession.createQuery("from Role where name=:roleName", Role.class);
		theQuery.setParameter("roleName", theRoleName);
		Role theRole = null;

		try {
			theRole = theQuery.getSingleResult();
		} catch (Exception e) {
			theRole = null;
		}

		return theRole;
	}

	@Override
	public List<Role> findAllRoles() {
		Session currentSession = entityManager.unwrap(Session.class);
		List<Role> roles = currentSession.createQuery("from Role", Role.class).getResultList();

		return roles;

	}

}
