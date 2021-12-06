package lab3.com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SlotsRepository extends JpaRepository<Slots, Long>{
    //search query for SlotID
    @Query("SELECT s FROM Slots s WHERE s.SlotID = ?1")
    lab3.com.example.demo.Slots findBySlotID(Long SlotID);

    //search query for PollID
    @Query("SELECT s FROM Slots s WHERE s.PollID = ?1")
    List<lab3.com.example.demo.Slots> findByPollID(Long PollID);
}
