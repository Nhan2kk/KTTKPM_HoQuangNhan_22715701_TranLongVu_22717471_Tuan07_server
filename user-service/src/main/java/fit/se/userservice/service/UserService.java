package fit.se.userservice.service;

import fit.se.userservice.entity.User;
import fit.se.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // Lấy tất cả user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Tìm theo ID
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    // Xóa user
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    // Sửa user
    public User updateUser(Integer id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setFullName(userDetails.getFullName());
            user.setEmail(userDetails.getEmail());
            user.setPhone(userDetails.getPhone());
            user.setRole(userDetails.getRole());
            user.setIsActive(userDetails.getIsActive());
            // Không nên cho sửa username và password ở đây để bảo mật
            return userRepository.save(user);
        }).orElse(null);
    }

    // Các hàm register, login đã viết ở bước trước giữ nguyên...
}