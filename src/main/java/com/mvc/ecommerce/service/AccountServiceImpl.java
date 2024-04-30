package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.exceptions.NotFoundException;
import com.mvc.ecommerce.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.mvc.ecommerce.utils.Constant.UPLOAD_DIR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {


    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account findByUsernameAndPassword(String username, String password) {
        String passwordEncoded = passwordEncoder.encode(password);
        Account account = accountRepository.findByUsernameAndPasswordAndActivatedTrue(username, passwordEncoded);
        return account;
    }

    @Override
    public boolean authenticate(String username, String password) {
        boolean accountExists = accountRepository.existsByUsernameAndPassword(username, password);
        return accountExists;
    }

    @Override
    public Page<Account> getUsersByPage(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Account registerUser(Account user) {
        // Validate username uniqueness
        Optional<Account> existingUser = accountRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Validate email uniqueness
        Optional<Account> existingEmail = accountRepository.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAdmin(false);
        user.setActivated(true);
        // Save the user to the database
        return accountRepository.save(user);
    }

    @Override
    public Account getUserByUsername(String username) {
        Optional<Account> accountOptional = accountRepository.findByUsername(username);

        if (accountOptional.isPresent()) {
            // Convert the Account entity to a User object (assuming you have a User class)
            Account account = accountOptional.get();

            Account user = new Account();
            user.setUsername(account.getUsername());
            user.setAddress(account.getAddress());
            user.setPassword(account.getPassword());
            user.setFullname(account.getFullname());
            user.setPhoto(account.getPhoto());
            user.setEmail(account.getEmail());
            user.setActivated(account.getActivated());
            user.setAdmin(account.getAdmin());
            // Set other fields as needed
            return user;
        }

        return null;
    }

    @Override
    @Transactional
    public void updateProfilePicture(Account user, MultipartFile profilePicture) {
        // Validate if the file is not empty
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                // Generate a unique filename for the profile picture
                String fileName = StringUtils.cleanPath(profilePicture.getOriginalFilename());
                String fileExtension = StringUtils.getFilenameExtension(fileName);
                String newFileName = System.currentTimeMillis() + "-" + fileName;

                // Save the file to the designated directory
                String resourcePath = System.getProperty("user.dir") + "/src/main/resources/static/" + UPLOAD_DIR;

                Path uploadPath = Paths.get(resourcePath);
                Files.createDirectories(uploadPath);

                try (var inputStream = profilePicture.getInputStream()) {
                    Path filePath = uploadPath.resolve(newFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // Retrieve the existing account by username
                Optional<Account> accountOptional = accountRepository.findByUsername(user.getUsername());

                if (accountOptional.isPresent()) {
                    Account existingAccount = accountOptional.get();

                    // Update the user's photo field with the new file path
                    existingAccount.setPhoto("/" + UPLOAD_DIR + newFileName);

                    // Save the updated user entity to the database
                    accountRepository.save(existingAccount);
                } else {
                    throw new NotFoundException("Account not found");
                }

            } catch (Exception e) {
                // Handle file processing exception (e.g., log the error, provide feedback to the user)
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateProfileInfo(Account updatedUser) {
        Optional<Account> user = accountRepository.findByUsername(updatedUser.getUsername());

        if (user.isPresent()) {
            Account existingAccount = user.get();
            existingAccount.setUsername(updatedUser.getUsername());
            existingAccount.setEmail(updatedUser.getEmail());
            existingAccount.setFullname(updatedUser.getFullname());
            existingAccount.setPassword(updatedUser.getPassword());
            existingAccount.setAddress(updatedUser.getAddress());
            existingAccount.setAdmin(updatedUser.getAdmin());
            existingAccount.setActivated(updatedUser.getActivated());
            accountRepository.save(existingAccount);
        } else {
            // Handle the case where the user is not found
            throw new IllegalArgumentException("User not found");
        }
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(String username) {
        // find user by username
        Optional<Account> user = accountRepository.findByUsername(username);

        // check if user exists
        if (user.isPresent()) {
            // set user to inactive
            Account existingAccount = user.get();
            existingAccount.setActivated(false);

            // save user
            accountRepository.save(existingAccount);
        } else {
            throw new NotFoundException("User not found");
        }

    }

    @Override
    public List<Account> getAllUsers() {
        return accountRepository.findAll();
    }

    @Override
    public Account updateUser(Account user) {
// find user by username
        Optional<Account> existingUser = accountRepository.findByUsername(user.getUsername());

        // check if user exists
        if (existingUser.isPresent()) {
            // update user
            Account existingAccount = existingUser.get();
            existingAccount.setUsername(user.getUsername());
            existingAccount.setEmail(user.getEmail());
            existingAccount.setFullname(user.getFullname());
            existingAccount.setPassword(passwordEncoder.encode(user.getPassword()));
            existingAccount.setAddress(user.getAddress());
            existingAccount.setAdmin(false);
            existingAccount.setActivated(true);

            // save user
            return accountRepository.save(existingAccount);
        } else {
            throw new NotFoundException("User not found");
        }
    }


}

