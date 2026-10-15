package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.RoleType;
import com.grupo10.epicentrogourmet.entities.UserRole;
import org.hibernate.internal.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("userRoleRepository")
public interface IUserRoleRepository extends JpaRepository<UserRole, Serializable> {
    public abstract Optional<UserRole> findByRol(RoleType rol);
}