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

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional(TxType.REQUIRED)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // <-- penting untuk proxy CDI
public class UserService {

    private static final Logger log = Logger.getLogger(UserService.class.getName());

    @Inject
    UserRepository repo;

    @Inject
    UserMapper mapper;

    public UserDTO create(UserDTO dto) {
        if (repo.existsByEmailIgnoreCase(dto.email())) {
            throw new RuntimeException("Email already exists: " + dto.email());
        }
        User saved = repo.save(mapper.toEntity(dto));
        log.info(() -> "Created user id=" + saved.getId());
        return mapper.toDTO(saved);
    }

    @Transactional(TxType.SUPPORTS)
    public List<UserDTO> getAll() {
        try (var s = repo.findAll()) {
            var list = s.map(mapper::toDTO).toList();
            log.fine(() -> "Fetched users count=" + list.size());
            return list;
        }
    }

    @Transactional(TxType.SUPPORTS)
    public UserDTO getById(Long id) {
        var dto = repo.findById(id).map(mapper::toDTO).orElse(null);
        if (dto == null) {
            log.fine(() -> "User not found id=" + id);
        } else {
            log.fine(() -> "Fetched user id=" + id);
        }
        return dto;
    }

    public UserDTO update(Long id, UserDTO dto) {
        return repo.findById(id).map(existing -> {
            repo.findByEmailIgnoreCase(dto.email())
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(u -> { throw new RuntimeException("Email already exists: " + dto.email()); });

            // MapStruct: ignore nulls sesuai konfigurasi mapper
            mapper.updateEntityFromDto(dto, existing);

            var updated = repo.save(existing);
            log.info(() -> "Updated user id=" + updated.getId());
            return mapper.toDTO(updated);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (repo.findById(id).isPresent()) {
            repo.deleteById(id);
            log.info(() -> "Deleted user id=" + id);
            return true;
        }
        log.fine(() -> "Delete skipped, user not found id=" + id);
        return false;
    }
}
