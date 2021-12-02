package lab3.com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RemindRepository extends JpaRepository<Remind, Long> {

    //search queries
    @Query("SELECT r FROM Remind r WHERE r.RemindID = ?1")
    lab3.com.example.demo.Remind findByRemindID(Long RemindID);

    @Query("SELECT r FROM Remind r WHERE r.PollID = ?1")
    List<Remind> findByPollID(Long PollID);
}
