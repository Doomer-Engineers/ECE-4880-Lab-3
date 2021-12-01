package lab3.com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PollRepository extends JpaRepository<Poll, Long> {
    //search queries
    @Query("SELECT p FROM Poll p WHERE p.PollID = ?1")
    lab3.com.example.demo.Poll findByPollID(Long PollID);

    @Query("SELECT p FROM Poll p WHERE p.UserID = ?1")
    List<lab3.com.example.demo.Poll> findByUserID(Long UserID);


}
