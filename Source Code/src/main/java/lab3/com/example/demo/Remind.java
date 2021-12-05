package lab3.com.example.demo;

import javax.persistence.*;

@Entity
@Table(name = "remind")
public class Remind {
    //Model in MVC.

    //PK
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long RemindID;

    @Column(nullable = false)
    public Long PollID;

    @Column(length = 45)
    public String Email;

    public Long getRemindID() {
        return RemindID;
    }

    public void setRemindID(Long remindID) {
        RemindID = remindID;
    }

    public Long getPollID() {
        return PollID;
    }

    public void setPollID(Long pollID) {
        PollID = pollID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
