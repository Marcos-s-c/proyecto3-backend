package com.sistema.venus.repo;

import com.sistema.venus.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByEmail(String username);

    @Query("SELECT u.user_id FROM User u WHERE u.email = :email")
    Long findIdByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.user_id = :userId")
    User findUserByUser_id(Long userId);
   /** @Query(nativeQuery = true, value="SELECT email, has_device, phone, active, user_id, name FROM users where rol ='USER'")
    List<User> listUsers();**/


   @Query("SELECT u.user_id, u.name, u.phone, u.hasDevice, u.email, u.active FROM User u WHERE u.rol = 'USER'")
   List<User> findUsers();
    @Modifying
    @Query("UPDATE User u SET u.email = :email, u.name = :name, u.phone = :phone WHERE u.email = :email")
    int actualizar(@Param("email") String email, @Param("name") String name, @Param("phone") String phone);

}
