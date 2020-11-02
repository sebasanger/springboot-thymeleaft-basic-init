package com.sanger.sesaga.dao;

import java.util.List;

import com.sanger.sesaga.entity.Role;

public interface RoleDao {

	public Role findRoleByName(String theRoleName);

	public List<Role> findAllRoles();

}
