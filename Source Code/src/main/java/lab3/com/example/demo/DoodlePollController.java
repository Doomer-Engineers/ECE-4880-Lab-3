package lab3.com.example.demo;

import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.dom4j.rule.Mode;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.ParameterOutOfBoundsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.util.DateUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Controller
public class DoodlePollController {
    //autowired repos here
    //need repos created
    @Autowired
    private UserRepository uRepo;
    @Autowired
    private PollRepository pRepo;
    @Autowired
    private SlotsRepository sRepo;
    @Autowired
    private VoteRepository vRepo;
    @Autowired
    private RemindRepository rRepo;

    //model attributes to be placed on page
    @ModelAttribute("user")
    public User userDto() {
        return new User();
    }

    @ModelAttribute("uvp")
    public ValidPassword pwDto() { return new ValidPassword(); }

    //HTTP requests and responses

    //default
    @GetMapping("")
    public String defaultRequest(){
        //this will redirect to doodle poll view
        //doodle pool view will then have sign up button
        //index page will be used for testing purposes only
        expirePoll();
        return "redirect:/find_poll";
    }

    @GetMapping("/index")
    public String getIndex(){
        //index page will be used for testing purposes only
        return "index";
    }

    //returns signup html view for GET request
    @GetMapping("/register")
    public String getRegister(){
        expirePoll();
        return "signup";
    }

    //once user submits view, the page will come here.
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user, Model model, @ModelAttribute("uvp") ValidPassword uvp){

        User checkUserValid = uRepo.findByUsername(user.getUsername());
        if(checkUserValid != null){
            model.addAttribute("inUse", user.getUsername());
            expirePoll();
            return "signup";
        }
        if(uvp.hasErrors(user.getPassword()) && user.getPassword() != null){
            model.addAttribute("errors", uvp.getErrors());
            expirePoll();
            return "signup";
        }
        if(!user.getPassword().equals(uvp.getCheckPW())){
            System.out.println(user.getPassword());
            System.out.println("yeet:" + uvp.getCheckPW());
            model.addAttribute("pwError", "Password fields do not match.");
            expirePoll();
            return "signup";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setStatus("active");
        user.setRole("general");
        uRepo.save(user);
        expirePoll();
        return "redirect:/find_poll";
    }

    //Might be nice later to get logged in user for verification
    public User getLoggedInUser(){
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return uRepo.findByUsername(username);
    }

    @GetMapping("/poll_display/{id}")
    public String viewPoll(Model model, @PathVariable(value = "id") Long id){
        Poll pollInfo = pRepo.findByPollID(id);
        if(pollInfo == null){
            expirePoll();
            return "redirect:/poll_find";
        }
        List<Slots> listSlots = sRepo.findByPollID(id);

        model.addAttribute("pollInfo",pollInfo);
        model.addAttribute("listSlots", listSlots);
        model.addAttribute("vote",new Vote());

        User user = getLoggedInUser();
        User pollOwner = uRepo.findByID(pollInfo.getUserID());
        if (user == pollOwner){
            model.addAttribute("object", new bullshit());
            expirePoll();
            return "EditPoll";
        }
        expirePoll();
        return "pollDisplay";
    }

    @PostMapping(("/poll_display/{id}/submit"))
    public String addSlot(@ModelAttribute("object") bullshit ob, @PathVariable(value = "id") Long id) throws ParseException {
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date startDate = sfd.parse(ob.getStart());
        int len = ob.getLen();
        int votes = ob.getVotes();
        for(int i=0; i<ob.getSlots(); i++){
            Slots newSlot = new Slots();
            newSlot.setPollID(id);
            newSlot.setStartTime(addMinutes(startDate,i*len));
            newSlot.setEndTime(addMinutes(startDate,(1+i)*len));
            newSlot.setVotesPer(votes);
            sRepo.save(newSlot);
        }
        expirePoll();

        return "redirect:/poll_display/" + id ;
    }
    public Date addMinutes(Date date, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        return calendar.getTime();
    }

    @GetMapping("/poll_display/{id}/delete")
    public String deleteSlot(@PathVariable(value = "id") Long id){
        Slots selSlot = sRepo.findBySlotID(id);
        Long pollID = selSlot.getPollID();

        User currUser = getLoggedInUser();
        User pollOwner = uRepo.findByID(pRepo.findByPollID(pollID).getUserID());
        if(currUser != pollOwner){ return "redirect:/polls";}
        sRepo.delete(selSlot);
        return "redirect:/poll_display/" + pollID;
    }

    @GetMapping("/polls")
    public String viewPollList(Model model){
        User user = getLoggedInUser();
        if (user == null){
            expirePoll();
            return "redirect:/find_poll";
        }
        List<Poll> polls = pRepo.findByUserID(user.getId());
        model.addAttribute("polls",polls);
        expirePoll();
        return "pollList";
    }

    @GetMapping("/create_poll")
    public String getCreatePoll(Model model){
        User user  = getLoggedInUser();
        if (user == null){
            return "redirect:/find_poll";
        }
        model.addAttribute("pollInput", new Poll());
        expirePoll();
        return "PollCreate";
    }

    @PostMapping("/create_poll")
    public String createPoll(Model model, @ModelAttribute("pollInput") Poll poll){
        model.addAttribute("pollInput", poll);
        User user = getLoggedInUser();
        poll.setUserID(user.getId());
        poll.setActive(false);
        poll.setExpired(false);
        Poll savedPoll = pRepo.save(poll);
        Long id = savedPoll.getPollID();
        expirePoll();
        return "redirect:/poll_display/" + id;
    }


    @GetMapping("/homepage")
    public String userHomepage (){
        User user  = getLoggedInUser();
        if (user == null){
            expirePoll();
            return "redirect:/find_poll";
        }
        expirePoll();
        return "userIndex";
    }

    @GetMapping("/find_poll")
    public String getPoll(Model model){
        Poll poll = new Poll();
        poll.setPollID(0L);
        model.addAttribute("poll", poll);
        expirePoll();
        return "findPoll";
    }

    @PostMapping("/find_poll/submit")
    public String postPoll(Model model, @ModelAttribute("poll") Poll poll){
        Poll checkPoll = pRepo.findByPollID(poll.getPollID());
        if (checkPoll == null || !checkPoll.isActive()){
            model.addAttribute("error", "Poll not found");
            expirePoll();
            return "findPoll";
        }
        if (checkPoll.isExpired()){
            model.addAttribute("error", "Poll is expired");
            expirePoll();
            return "findPoll";
        }
        expirePoll();
        return "redirect:/poll_display/" + poll.getPollID();
    }

    @PostMapping("/user/poll/{id}/publish")
    public String publishPoll(@PathVariable(value = "id") Long id, Model model){
        Poll poll = pRepo.findByPollID(id);
        poll.setActive(true);
        pRepo.save(poll);
        return "redirect:/polls";
    }

    @PostMapping("/user/poll/{id}/unpublish")
    public String unpublishPoll(@PathVariable(value = "id") Long id, Model model){
        Poll poll = pRepo.findByPollID(id);
        poll.setActive(false);
        pRepo.save(poll);
        return "redirect:/polls";
    }

    @PostMapping("/user/poll/{id1}/slot/{id2}/vote")
    public String votePoll(@PathVariable(value = "id1") Long id1, @PathVariable(value = "id2") Long id2, @ModelAttribute("vote") Vote vote, Model model){
        Poll poll = pRepo.findByPollID(id1);
        Integer maxVotes = poll.getVotesPer();

        List <Slots> slots = sRepo.findByPollID(id1);

        Slots slot = sRepo.findBySlotID(id2);
        Integer maxSlot = slot.getVotesPer();
        List<Vote> votes = vRepo.findBySlotID(id2);
        if (votes.size() >= maxSlot - 1){
            vote.setEmail(null);
            slot.setFull(true);
            sRepo.save(slot);
            List <Slots> slots2 = sRepo.findByPollID(id1);
            model.addAttribute("pollInfo",poll);
            model.addAttribute("listSlots", slots2);
            model.addAttribute("error", "Max votes filled for slot");
            return "pollDisplay";
        }

        int counter = 0;
        for (int i = 0; i < slots.size(); i ++){
            Slots checkSlot = slots.get(i);
            List<Vote> votes2 = vRepo.findBySlotID(checkSlot.getSlotID());
            for (Vote checkVote : votes2) {
                if (checkVote.getEmail().equals(vote.getEmail())) {
                    counter++;
                    if (counter >= maxVotes) {
                        vote.setEmail(null);
                        model.addAttribute("pollInfo", poll);
                        model.addAttribute("listSlots", slots);
                        model.addAttribute("error", "Max votes filled for poll");
                        return "pollDisplay";
                    }
                }
            }
        }
        vote.setPollID(id1);
        vote.setSlotID(id2);
        vRepo.save(vote);
        model.addAttribute("pollInfo",poll);
        model.addAttribute("vote", vote);
        model.addAttribute("slot", slot);
        return "confirmation";
    }

    @PostMapping("/user/poll/{id1}/slot/{id2}/reserve")
    public String reserveSlot(@PathVariable(value = "id1") Long id1, @PathVariable(value = "id2") Long id2, @ModelAttribute("vote") Vote vote, Model model){
        Slots slot = sRepo.findBySlotID(id2);
        slot.setReserved(true);
        slot.setEmail(vote.getEmail());
        sRepo.save(slot);
        Poll poll = pRepo.findByPollID(id1);
        model.addAttribute("pollInfo",poll);
        model.addAttribute("vote", vote);
        model.addAttribute("slot", slot);
        return "confirmation";
    }

    @PostMapping("/user/poll/{id}/delete")
    public String deletePoll(@PathVariable(value = "id") Long id){
        Poll poll = pRepo.findByPollID(id);
        pRepo.delete(poll);
        return "redirect:/polls";
    }

    @GetMapping("/user/poll/{id}/edit")
    public String getEditPoll(@PathVariable(value = "id") Long id, Model model){
        Poll poll = pRepo.findByPollID(id);
        model.addAttribute("pollInput", poll);
        return "realPollEdit";
    }

    @PostMapping("/user/poll/{id}/edit")
    public String editPoll(@ModelAttribute("pollInput") Poll poll, @PathVariable(value = "id") Long id){
        Poll oldPoll = pRepo.findByPollID(id);
        poll.setExpired(oldPoll.isExpired());
        poll.setActive(oldPoll.isActive());
        poll.setUserID(oldPoll.getUserID());
        poll.setPollID(oldPoll.getPollID());
        pRepo.save(poll);
        expirePoll();
        return "redirect:/polls";
    }

    @GetMapping("/user/poll/{id}/invite")
    public String getInvitePoll(@PathVariable(value = "id") Long id, Model model){
        Remind remind = new Remind();
        model.addAttribute("invite", remind);
        model.addAttribute("pollID", id);
        return "inviteUser";
    }


    @PostMapping("/user/poll/{id}/invite")
    public String invitePoll( @ModelAttribute("invite") Remind remind, @PathVariable(value = "id") Long id){
        remind.setPollID(id);
        rRepo.save(remind);
        String subject = "You have been invited to Doodle Poll: " + id;
        sendEmail(remind.getEmail(),subject);
        return "redirect:/polls";
    }

    @PostMapping("/user/poll/{id}/remind")
    public String remindPoll(@PathVariable(value = "id") Long id){
        List<Remind> reminds = rRepo.findByPollID(id);
        for (Remind remind : reminds) {
            String subject = "Reminder: you have been invited to Doodle Poll: " + id;
            sendEmail(remind.getEmail(), subject);
        }
        return "redirect:/polls";
    }

    public void expirePoll(){
        List <Poll> polls = pRepo.findAll();
        for (Poll poll : polls) {
            LocalDateTime localDate = LocalDateTime.now();
            LocalDateTime checkDate = convertToLocalDateTimeViaInstant(poll.getDeadline());
            boolean isBeforeLocal = checkDate.isBefore(localDate);
            poll.setExpired(isBeforeLocal);
            pRepo.save(poll);
        }
    }

    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @GetMapping("sudoLogin")
    public String sudoLogin(){
        return "redirect:/login";
    }

    @GetMapping("/poll/{id}/add_slots")
    public String addSlots(@PathVariable(value = "id") Long id, Model model){
        Poll poll = pRepo.findByPollID(id);
        List <Slots> slots = sRepo.findByPollID(id);
        boolean bool = true;
        if (slots.size() == 0){
            bool = false;
        }
        bullshit obj = new bullshit();
        model.addAttribute("pollInfo", poll);
        model.addAttribute("object", obj);
        model.addAttribute("bool",bool);
        return "createPoll";
    }


    public void sendEmail(String email, String subject){

        // Sender's email ID needs to be mentioned
        String from = "badtimerwiki@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("badtimerwiki@gmail.com", "Aren1474");

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText("Hi there! :) Someone sent you this link to fill out a doodle poll. Why don't you go ahead and head over there right now!");

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
