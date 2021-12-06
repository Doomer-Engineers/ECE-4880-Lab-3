package lab3.com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Long> {

    //search query for username
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    lab3.com.example.demo.User findByUsername(String username);

    //search query for id
    @Query("SELECT u FROM User u WHERE u.id = ?1")
    lab3.com.example.demo.User findByID(Long username);
}
