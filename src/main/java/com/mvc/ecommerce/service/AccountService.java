package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account findByUsernameAndPassword(String username, String password);

    boolean authenticate(String username, String password);

    Page<Account> getUsersByPage(Pageable pageable);

    Account registerUser(Account user);

    Account getUserByUsername(String username);

    void updateProfilePicture(Account user, MultipartFile profilePicture);

    void updateProfileInfo(Account user);

    Account findByUsername(String username);

    Optional<Account> getAccountByEmail(String email);

    void deleteUser(String username);

    List<Account> getAllUsers();

}
