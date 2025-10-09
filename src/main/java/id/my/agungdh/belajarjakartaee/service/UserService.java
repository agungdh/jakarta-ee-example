package id.my.agungdh.belajarjakartaee.service;

import id.my.agungdh.belajarjakartaee.dto.UserDTO;
import id.my.agungdh.belajarjakartaee.entity.User;
import id.my.agungdh.belajarjakartaee.mapper.UserMapper;
import id.my.agungdh.belajarjakartaee.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Transactional(TxType.REQUIRED)
@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // <-- penting untuk proxy CDI
public class UserService {

    @Inject
    UserRepository repo;

    @Inject
    UserMapper mapper;

    public UserDTO create(UserDTO dto) {
        if (repo.existsByEmailIgnoreCase(dto.email())) {
            throw new RuntimeException("Email already exists: " + dto.email());
        }
        User saved = repo.save(mapper.toEntity(dto));
        log.debug("Created user id={}", saved.getId());
        return mapper.toDTO(saved);
    }

    @Transactional(TxType.SUPPORTS)
    public List<UserDTO> getAll() {
        try (var s = repo.findAll()) {
            return s.map(mapper::toDTO).toList();
        }
    }

    @Transactional(TxType.SUPPORTS)
    public UserDTO getById(Long id) {
        return repo.findById(id).map(mapper::toDTO).orElse(null);
    }

    public UserDTO update(Long id, UserDTO dto) {
        return repo.findById(id).map(existing -> {
            repo.findByEmailIgnoreCase(dto.email())
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(u -> { throw new RuntimeException("Email already exists: " + dto.email()); });
            mapper.updateEntityFromDto(dto, existing);
            var updated = repo.save(existing);
            log.debug("Updated user id={}", updated.getId());
            return mapper.toDTO(updated);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (repo.findById(id).isPresent()) {
            repo.deleteById(id);
            log.debug("Deleted user id={}", id);
            return true;
        }
        return false;
    }
}
