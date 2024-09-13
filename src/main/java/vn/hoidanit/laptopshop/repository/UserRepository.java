package vn.hoidanit.laptopshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.hoidanit.laptopshop.domain.User;

//crud: create, read, update, delete
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User hoidanit);

    List<User> findOneByEmail(String email);

    User findUserById(long id);

    boolean existsByEmail(String email);

    User findByEmail(String email);
}
