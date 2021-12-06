package lab3.com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class ValidPassword {

    private boolean truth;

    private String checkPW;

    private ArrayList<String> errors = new ArrayList<>();

    public List<String> getErrors(){
        return errors;
    }

    public boolean hasErrors(String password){
        char[] chars = password.toCharArray();
        StringBuilder numbers = new StringBuilder();
        StringBuilder upLetters = new StringBuilder();
        StringBuilder lowLetters = new StringBuilder();
        StringBuilder specialCase = new StringBuilder();
        int whiteSpace = 0;
        for(char c : chars){
            if(Character.isDigit(c)){
                numbers.append(c);
            }
            else if(Character.isUpperCase(c)){
                upLetters.append(c);
            }
            else if(Character.isLowerCase(c)){
                lowLetters.append(c);
            }
            else if(c == ' '){
                whiteSpace++;
            }
            else{
                specialCase.append(c);
            }
        }
        //checks if there is a digit in the password
        if (numbers.length() == 0){
            errors.add("Please add a digit to password");
        }
        //checks if there is an uppercase letter in the password
        if (upLetters.length() == 0){
            errors.add("Please add a uppercase letter to password");
        }
        //checks if there is a lowercase letter in the password
        if (lowLetters.length() == 0){
            errors.add("Please add a lowercase letter to password");
        }
        //checks if there is a special character in the password
        if (specialCase.length() == 0){
            errors.add("Please add a special character to password");
        }
        //checks if there is whitespace in the password
        if (whiteSpace != 0){
            errors.add("Please remove the white space");
        }
        truth = (numbers.length() == 0 || upLetters.length() == 0 || lowLetters.length() ==0|| specialCase.length() == 0 || whiteSpace != 0);
        return truth;
    }

    public String getCheckPW() {
        return checkPW;
    }

    public void setCheckPW(String checkPW) {
        this.checkPW = checkPW;
    }
}

