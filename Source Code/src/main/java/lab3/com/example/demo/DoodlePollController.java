package lab3.com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DoodlePollController {
    //autowired repos here
    //need repos created
    @Autowired
    private UserRepository uRepo;

    //model attributes to be placed on page
    @ModelAttribute("user")
    public User userDto() {
        return new User();
    }

    //HTTP requests and responses

    //default
    @GetMapping("")
    public String defaultRequest(){
        //this will redirect to doodle poll view
        //doodle pool view will then have sign up button
        //index page will be used for testing purposes only
        return "index";
    }

    @GetMapping("/index")
    public String getIndex(){
        //index page will be used for testing purposes only
        return "index";
    }

    //returns signup html view for GET request
    @GetMapping("/register")
    public String getRegister(){
        return "signup";
    }

    //once user submits view, the page will come here.
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user, Model model){

        User checkUserValid = uRepo.findByUsername(user.getUsername());
        if(checkUserValid != null){
            model.addAttribute("inUse", user.getUsername());
            return "signup";
        }
        ValidPassword uvp = new ValidPassword();
        if(uvp.hasErrors(user.getPassword()) && user.getPassword() != null){
            model.addAttribute("errors", uvp.getErrors());
            return "signup";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setStatus("active");
        user.setRole("general");
        uRepo.save(user);
        return "index";
    }


}
