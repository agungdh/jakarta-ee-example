package id.my.agungdh.demo7.repository;

import id.my.agungdh.demo7.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Stateless
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public Optional<User> findByEmail(String email) {
        return em.createQuery("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.id", User.class)
                .getResultList();
    }

    public void delete(Long id) {
        User u = em.find(User.class, id);
        if (u != null) em.remove(u);
    }
}
