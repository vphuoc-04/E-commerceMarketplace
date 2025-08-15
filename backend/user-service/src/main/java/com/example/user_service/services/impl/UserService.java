package com.example.user_service.services.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.common_lib.dtos.UserDto;
import com.example.common_lib.services.BaseService;
import com.example.user_service.entities.User;
import com.example.user_service.repositories.UserRepository;
import com.example.user_service.services.interfaces.UserServiceInterface;

@Service
public class UserService extends BaseService implements UserServiceInterface, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<User> paginate(Map<String, String[]> parameters) {
        int page = parameters.containsKey("page") ? Integer.parseInt(parameters.get("page")[0]) : 1;
        int perpage = parameters.containsKey("perpage") ? Integer.parseInt(parameters.get("perpage")[0]) : 10;
        String sortParam = parameters.containsKey("sort") ? parameters.get("sort")[0] : null;
        Sort sort = createSort(sortParam);

        Pageable pageable = PageRequest.of(page - 1, perpage, sort);

        return userRepository.findAll(pageable);
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setEmail(user.getEmail());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setMiddleName(user.getMiddleName());
            userDto.setPhone(user.getPhone());
            return userDto;
        }
        return null;
    }

    @Override
    public UserDto getUserById(Long id, String accessToken) {
        return getUserById(id);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return null;
        }
        return convertToDto(user);
    }

    @Override
    public boolean validateUserCredentials(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String userIdStr) throws org.springframework.security.core.userdetails.UsernameNotFoundException {
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("Invalid user id: " + userIdStr);
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found with id: " + userId);
        }
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("USER"))
        );
    }

    private UserDto convertToDto(User user) {
        com.example.common_lib.dtos.UserDto dto = new com.example.common_lib.dtos.UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setMiddleName(user.getMiddleName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setImg(user.getImg());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
