package com.grupo10.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;
import com.grupo10.epicentrogourmet.entities.UserRole;

public interface IUserRoleService {
    List<UserRole> getAll();
    Optional<UserRole> findById(Long id);
    UserRole insertOrUpdate(UserRole role);
    boolean remove(Long id);
}