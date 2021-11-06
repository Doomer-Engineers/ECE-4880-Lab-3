package lab3.com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

//for security stuff, don't copy this.
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository uRepo;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = uRepo.findByUsername(s);
        if (user==null){
            throw new UsernameNotFoundException("User not found");
        }
        return new lab3.com.example.demo.UserDetails(user);
    }
}


