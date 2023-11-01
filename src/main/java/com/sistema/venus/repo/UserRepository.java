package com.sistema.venus.repo;

import com.sistema.venus.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByEmail(String username);

    @Query("SELECT u.user_id FROM User u WHERE u.email = :email")
    public String findIdByEmail(@Param("email") String email);

    //select user_id from users where email =: 'gabriela@gmail.com'
}
