package trinity.play2learn.backend.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import trinity.play2learn.backend.user.models.User;

@Repository
public interface IUserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
