package com.sanger.sesaga.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.sanger.sesaga.dao.RoleDao;

import com.sanger.sesaga.entity.Role;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;

	@Override
	public List<Role> findAll() {
		return roleDao.findAllRoles();
	}

}
