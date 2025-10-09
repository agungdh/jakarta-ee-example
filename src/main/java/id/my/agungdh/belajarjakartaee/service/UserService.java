package id.my.agungdh.belajarjakartaee.service;

import id.my.agungdh.belajarjakartaee.dto.UserDTO;
import id.my.agungdh.belajarjakartaee.entity.User;
import id.my.agungdh.belajarjakartaee.mapper.UserMapper;
import id.my.agungdh.belajarjakartaee.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class UserService {

    // JANGAN final, JANGAN pakai @RequiredArgsConstructor/@AllArgsConstructor
    @Inject
    private UserRepository repo;

    @Inject
    private UserMapper mapper;

    public UserService() { } // optional, no-arg constructor

    public UserDTO create(UserDTO dto) {
        if (repo.existsByEmailIgnoreCase(dto.email())) {
            throw new RuntimeException("Email already exists: " + dto.email());
        }
        User saved = repo.save(mapper.toEntity(dto));
        return mapper.toDTO(saved);
    }

    public List<UserDTO> getAll() {
        try (var s = repo.findAll()) {
            return s.map(mapper::toDTO).toList();
        }
    }

    public UserDTO getById(Long id) {
        return repo.findById(id).map(mapper::toDTO).orElse(null);
    }

    public UserDTO update(Long id, UserDTO dto) {
        return repo.findById(id).map(existing -> {
            repo.findByEmailIgnoreCase(dto.email())
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(u -> { throw new RuntimeException("Email already exists: " + dto.email()); });

            mapper.updateEntityFromDto(dto, existing);
            return mapper.toDTO(repo.save(existing));
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (repo.findById(id).isPresent()) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}
