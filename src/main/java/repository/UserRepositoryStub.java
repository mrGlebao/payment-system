package repository;

import domain.User;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserRepositoryStub implements UserRepository {

    private static final Map<Long, User> users = Stream.of(
            new User(1L, "Anna", BigDecimal.TEN),
            new User(2L, "Belle", BigDecimal.TEN),
            new User(3L, "Clint", BigDecimal.TEN)
    ).collect(Collectors.toMap(User::getId, Function.identity()));

    @Override
    public Optional<User> getOne(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
