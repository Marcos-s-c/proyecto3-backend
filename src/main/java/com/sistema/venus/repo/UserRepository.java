package com.sistema.venus.repo;

import com.sistema.venus.domain.User;
import com.sistema.venus.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByEmail(String username);

    @Query("SELECT u.user_id FROM User u WHERE u.email = :email")
    Long findIdByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.user_id = :userId")
    User findUserByUser_id(Long userId);

    @Modifying
    @Query("UPDATE User u SET u.email = :email, u.name = :name, u.phone = :phone WHERE u.email = :email")
    int actualizar(@Param("email") String email, @Param("name") String name, @Param("phone") String phone);
    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.user_id = :user_id")
    int changeUserStatus(@Param("user_id") Long user_id, @Param("active") Boolean active);

    @Query("SELECT u FROM User u WHERE (u.name like %:userSearch% or u.email like %:userSearch% or u.phone like %:userSearch%) and u.rol = 'USER'")
    List<User> findUserByNameEmailOrPhone (@Param("userSearch") String userSearch);

    @Query("SELECT u FROM User u WHERE  u.rol = 'USER'")
    List<User> findUsers ();
}
