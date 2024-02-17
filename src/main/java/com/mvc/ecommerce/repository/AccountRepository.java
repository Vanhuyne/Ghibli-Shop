package com.mvc.ecommerce.repository;

import com.mvc.ecommerce.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    // find user by username , password and activated = true
    Account findByUsernameAndPasswordAndActivatedTrue(String username, String password);

    Account findByUsernameAndPassword(String username, String password);

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.username = :username AND a.password = :password")
    boolean existsByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    Page<Account> findAll(Pageable pageable);

    Page<Account> findByActivatedTrue(Pageable pageable);

}