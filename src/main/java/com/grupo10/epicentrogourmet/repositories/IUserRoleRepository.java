package com.grupo10.epicentrogourmet.repositories;

import com.grupo10.epicentrogourmet.entities.RoleType;
import com.grupo10.epicentrogourmet.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository("userRoleRepository")
public interface IUserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByRol(RoleType rol);

}