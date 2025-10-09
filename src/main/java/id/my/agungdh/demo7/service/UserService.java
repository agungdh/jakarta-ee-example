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
        // Cek duplikat email
        if (repo.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email already exists: " + dto.email());
        }

        User user = mapper.toEntity(dto);
        User saved = repo.save(user);
        return mapper.toDTO(saved);
    }

    public List<UserDTO> getAll() {
        return mapper.toDTOs(repo.findAll());
    }

    public UserDTO getById(Long id) {
        return repo.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }

    public UserDTO update(Long id, UserDTO dto) {
        return repo.findById(id)
                .map(existing -> {
                    // Cek email bentrok dengan user lain
                    if (repo.findByEmail(dto.email())
                            .filter(other -> !other.getId().equals(id))
                            .isPresent()) {
                        throw new RuntimeException("Email already exists: " + dto.email());
                    }
                    mapper.updateEntityFromDto(dto, existing);
                    User updated = repo.save(existing);
                    return mapper.toDTO(updated);
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
