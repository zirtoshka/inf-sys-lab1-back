package org.zir.dragonieze.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String name);



    Optional<User> findByRole(Role role);
    List<User> findAllByRole(Role role);

}


