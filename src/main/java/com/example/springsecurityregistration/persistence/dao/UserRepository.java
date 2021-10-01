package com.example.springsecurityregistration.persistence.dao;

import com.example.springsecurityregistration.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users"
            + " WHERE id = ("
            + "    SELECT user_id "
            + "      FROM token "
            + "     WHERE token = :token)",
            nativeQuery = true)
    User findUserByToken(@Param("token") String token);

    @Query(value = "SELECT password FROM users WHERE id = :id",
            nativeQuery = true)
    String getPasswordById(@Param("id") long id);

    @Query(value = "UPDATE users"
            + "   SET enabled = TRUE"
            + " WHERE id = :id",
            nativeQuery = true)
    void updateIsEnable(@Param("id") long userId);

    @Query(value = "UPDATE users"
            + "   SET password = :newPassword"
            + " WHERE id = :id",
            nativeQuery = true)
    void updatePassword(@Param("id") long id, @Param("newPassword") String newPassword);

}
