package id.my.agungdh.demo7.service;

import id.my.agungdh.demo7.dto.UserDTO;
import id.my.agungdh.demo7.entity.User;
import id.my.agungdh.demo7.mapper.UserMapper;
import id.my.agungdh.demo7.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class UserService {

    @Inject
    private UserRepository repo;

    @Inject
    private UserMapper mapper;

    public UserDTO create(UserDTO dto) {
        // pake existsBy... supaya ringkas & efisien
        if (repo.existsByEmailIgnoreCase(dto.email())) {
            throw new RuntimeException("Email already exists: " + dto.email());
        }
        User saved = repo.save(mapper.toEntity(dto));
        return mapper.toDTO(saved);
    }

    public List<UserDTO> getAll() {
        // Spec minta stream ditutup => pakai try-with-resources
        try (var stream = repo.findAll()) {
            return stream.map(mapper::toDTO).toList();
        }
    }

    public UserDTO getById(Long id) {
        return repo.findById(id).map(mapper::toDTO).orElse(null);
    }

    public UserDTO update(Long id, UserDTO dto) {
        return repo.findById(id)
                .map(existing -> {
                    // Cek bentrok email dengan user lain
                    repo.findByEmailIgnoreCase(dto.email())
                            .filter(other -> !other.getId().equals(id))
                            .ifPresent(u -> { throw new RuntimeException("Email already exists: " + dto.email()); });

                    mapper.updateEntityFromDto(dto, existing);
                    return mapper.toDTO(repo.save(existing));
                })
                .orElse(null);
    }

    public boolean delete(Long id) {
        if (repo.findById(id).isPresent()) {
            repo.deleteById(id); // <- jangan delete(id)
            return true;
        }
        return false;
    }
}
