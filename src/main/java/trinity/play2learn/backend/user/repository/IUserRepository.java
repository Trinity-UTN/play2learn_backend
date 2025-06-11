package trinity.play2learn.backend.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.user.models.User;

public interface IUserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
