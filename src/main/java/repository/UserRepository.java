package repository;

import domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> getOne(Long id);

}
