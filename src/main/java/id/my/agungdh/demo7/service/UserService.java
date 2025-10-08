package id.my.agungdh.demo7.service;

import id.my.agungdh.demo7.dto.UserDTO;
import id.my.agungdh.demo7.entity.User;
import id.my.agungdh.demo7.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class UserService {

    @Inject
    private UserRepository repo;

    public UserDTO create(UserDTO dto) {
        // Cek duplikat email
        if (repo.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email already exists: " + dto.email());
        }

        User user = new User(dto.name(), dto.email());
        User saved = repo.save(user);
        return new UserDTO(saved.getId(), saved.getName(), saved.getEmail());
    }

    public List<UserDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail()))
                .toList();
    }

    public UserDTO getById(Long id) {
        return repo.findById(id)
                .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail()))
                .orElse(null);
    }

    public UserDTO update(Long id, UserDTO dto) {
        return repo.findById(id)
                .map(u -> {
                    // cek jika email baru sudah digunakan user lain
                    if (repo.findByEmail(dto.email())
                            .filter(existing -> !existing.getId().equals(id))
                            .isPresent()) {
                        throw new RuntimeException("Email already exists: " + dto.email());
                    }
                    u.setName(dto.name());
                    u.setEmail(dto.email());
                    User updated = repo.save(u);
                    return new UserDTO(updated.getId(), updated.getName(), updated.getEmail());
                })
                .orElse(null);
    }

    public boolean delete(Long id) {
        if (repo.findById(id).isPresent()) {
            repo.delete(id);
            return true;
        }
        return false;
    }
}
