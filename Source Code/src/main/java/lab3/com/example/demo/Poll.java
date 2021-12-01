package lab3.com.example.demo;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "poll")
public class Poll {
    //Model in MVC

    //PK
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long PollID;

    @Column(nullable = false)
    public Long UserID;

    @Column(nullable = false, length = 45)
    public String Title;

    @Column(length = 45)
    public String Location;

    @Column(length = 45)
    public String Notes;

    @Column(nullable = false, length = 45)
    public String TimeZone;

    @Column(nullable = false)
    public Integer VotesPer;

    @Column(nullable = false, length = 45)
    @Temporal(TemporalType.TIMESTAMP)
    public java.util.Date Deadline;

    public Long getPollID() {
        return PollID;
    }

    public void setPollID(Long pollID) {
        PollID = pollID;
    }

    public Long getUserID() {
        return UserID;
    }

    public void setUserID(Long userID) {
        UserID = userID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getTimeZone() {
        return TimeZone;
    }

    public void setTimeZone(String timeZone) {
        TimeZone = timeZone;
    }

    public Integer getVotesPer() {
        return VotesPer;
    }

    public void setVotesPer(Integer votesPer) {
        VotesPer = votesPer;
    }

    public Date getDeadline() {
        return Deadline;
    }

    public void setDeadline(Date deadline) {
        Deadline = deadline;
    }
}