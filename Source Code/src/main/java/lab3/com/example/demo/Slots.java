package lab3.com.example.demo;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "slots")
public class Slots {
    //Model in MVC

    //PK
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long SlotID;

    @Column(nullable = false)
    private Long PollID;

    @Column(nullable = false, length = 45)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date StartTime;

    @Column(nullable = false, length = 45)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date EndTime;

    @Column(nullable = false)
    private Integer VotesPer;

    public Long getSlotID() {
        return SlotID;
    }

    public void setSlotID(Long slotID) {
        SlotID = slotID;
    }

    public Long getPollID() {
        return PollID;
    }

    public void setPollID(Long pollID) {
        PollID = pollID;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }

    public Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }

    public Integer getVotesPer() {
        return VotesPer;
    }

    public void setVotesPer(Integer votesPer) {
        VotesPer = votesPer;
    }


}
