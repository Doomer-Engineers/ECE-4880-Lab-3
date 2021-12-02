package lab3.com.example.demo;

import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private SMS phone = new SMS();

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
        return "index";
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

        System.out.println(id);

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

    @GetMapping("/polls/delete/{id}")
    public String deletePoll(@PathVariable(value = "id") Long id){
        Poll selPoll = pRepo.findByPollID(id);
//        Long pollID = selPoll.getUseID();

        User currUser = getLoggedInUser();
        User pollOwner = uRepo.findByID(selPoll.getUserID());
        if(currUser != pollOwner){ return "redirect:/polls";}
        pRepo.delete(selPoll);
        return "redirect:/polls";
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

    @GetMapping("/edit_poll/{id}")
    public String editPollInfo(Model model, @PathVariable(value = "id") Long id){
        Poll poll = pRepo.findByPollID(id);

        User currUser = getLoggedInUser();
        User pollOwner = uRepo.findByID(poll.getUserID());
        if(currUser != pollOwner){ return "redirect:/polls";}

        model.addAttribute("pollInfo", poll);
        model.addAttribute("pollInput", new Poll());

        return "editPollInfo";
    }
    @PostMapping("/edit_poll/{id}")
    public String updatePollInfo(@ModelAttribute("pollInput") Poll poll, @PathVariable(value = "id") Long id){
//        model.addAttribute("pollInput", poll);
        System.out.println(poll.getTitle());
        User user = getLoggedInUser();
        poll.setUserID(user.getId());
        poll.setActive(false);
        poll.setExpired(false);
        poll.setPollID(id);
        pRepo.save(poll);
//        Long id = savedPoll.getPollID();
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
        expirePoll();
        return "redirect:/poll_display/" + poll.getPollID();
    }

    @PostMapping("/user/poll/{id}/publish")
    public String publishPoll(@PathVariable(value = "id") Long id, Model model){
        Poll poll = pRepo.findByPollID(id);
        poll.setActive(true);
        pRepo.save(poll);
        String message = "Poll was successfully published";
        model.addAttribute("message", message);
        User user = getLoggedInUser();
        List<Poll> polls = pRepo.findByUserID(user.getId());
        model.addAttribute("polls",polls);
        expirePoll();
        return "pollList";
    }

    @PostMapping("/user/poll/{id}/unpublish")
    public String unpublishPoll(@PathVariable(value = "id") Long id, Model model){
        Poll poll = pRepo.findByPollID(id);
        poll.setActive(false);
        pRepo.save(poll);
        String message = "Poll was successfully unpublished";
        model.addAttribute("message", message);
        User user = getLoggedInUser();
        List<Poll> polls = pRepo.findByUserID(user.getId());
        model.addAttribute("polls",polls);
        expirePoll();
        return "pollList";
    }

    public void expirePoll(){
        List <Poll> polls = pRepo.findAll();
        for (Poll poll : polls) {
            LocalDateTime localDate = LocalDateTime.now();
            LocalDateTime checkDate = convertToLocalDateTimeViaInstant(poll.getDeadline());
            boolean isBeforeLocal = checkDate.isBefore(localDate);
            if (isBeforeLocal) {
                poll.setExpired(true);
                pRepo.save(poll);
            }
        }
    }

    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}
