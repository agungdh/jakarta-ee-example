package id.my.agungdh.belajarjakartaee.repository;

import id.my.agungdh.belajarjakartaee.entity.User;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Query;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    @Query("where lower(name) like lower(:pattern)")
    Stream<User> findByNameLike(String pattern);
}
