package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.Role;
import com.smartcampus.smartcampus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdentityNumber(String identityNumber);

    boolean existsByIdentityNumber(String identityNumber);

    @Query("SELECT r FROM Role r JOIN UserRole ur ON r.id = ur.roleId WHERE ur.userId = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "INSERT INTO user_role (user_id, role_id, is_default) VALUES (:userId, :roleId, 1)", nativeQuery = true)
    void saveUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    long countByStatus(String status);

    @Query("SELECT u FROM User u ORDER BY u.id")
    List<User> findAllUsers();

    @Query("SELECT r FROM Role r")
    List<Role> findAllRoles();
}