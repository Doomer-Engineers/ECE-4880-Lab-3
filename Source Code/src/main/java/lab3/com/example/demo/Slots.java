package lab3.com.example.demo;


import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "slots")
public class Slots {
    //Model in MVC.

    //PK.
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long SlotID;

    @Column(nullable = false)
    private Long PollID;

    @Column(nullable = false, length = 45)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private java.util.Date StartTime;

    @Column(nullable = false, length = 45)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private java.util.Date EndTime;

    @Column(nullable = false)
    private Integer VotesPer;

    @Column(nullable = false)
    private boolean reserved = false;

    @Column(nullable = false)
    private boolean full = false;

    @Column(length = 45)
    public String email;

    //getters and setters for Slots
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

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }
}
