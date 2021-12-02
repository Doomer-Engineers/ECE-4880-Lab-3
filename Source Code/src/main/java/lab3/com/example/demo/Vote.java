package lab3.com.example.demo;

import javax.persistence.*;

@Entity
@Table(name = "votes")
public class Vote {
    //Model in MVC

    //PK
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long VoteID;

    @Column(nullable = false)
    public Long SlotID;

    @Column(length = 45)
    public String Email;

    public Long getVoteID() {
        return VoteID;
    }

    public void setVoteID(Long voteID) {
        VoteID = voteID;
    }

    public Long getSlotID() {
        return SlotID;
    }

    public void setSlotID(Long slotID) {
        SlotID = slotID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
