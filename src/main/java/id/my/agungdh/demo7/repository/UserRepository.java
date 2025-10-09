package id.my.agungdh.demo7.repository;

import id.my.agungdh.demo7.entity.User;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Query;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    // Biar kompatibel dengan pemanggilan lama & baru
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);

    // Lebih efisien buat validasi unik
    boolean existsByEmailIgnoreCase(String email);

    // Contoh JDQL opsional
    @Query("where lower(name) like lower(:pattern)")
    Stream<User> findByNameLike(String pattern);
}
