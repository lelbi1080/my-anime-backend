package be.ben.service.dao;

import be.ben.repository.Manga;
import be.ben.repository.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserService extends JpaRepository<User, Integer> {

    @Query(value = "select * from User  WHERE BINARY `userName` = :userName",nativeQuery = true)
    public User findByuserName(String userName);
}
