package lab3.com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    //search query for VoteID
    @Query("SELECT v FROM Vote v WHERE v.VoteID = ?1")
    lab3.com.example.demo.Vote findByVoteID(Long VoteID);

    //search query for SlotID
    @Query("SELECT v FROM Vote v WHERE v.SlotID = ?1")
    List<lab3.com.example.demo.Vote> findBySlotID(Long SlotID);
}
