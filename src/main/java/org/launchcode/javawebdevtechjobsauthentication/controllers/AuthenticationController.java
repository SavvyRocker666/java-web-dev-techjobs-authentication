package org.launchcode.javawebdevtechjobsauthentication.controllers;


import org.launchcode.javawebdevtechjobsauthentication.data.UserRepository;
import org.launchcode.javawebdevtechjobsauthentication.models.User;
import org.launchcode.javawebdevtechjobsauthentication.models.dto.LoginFormDTO;
import org.launchcode.javawebdevtechjobsauthentication.models.dto.RegistrationFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthenticationController {
    @Autowired
    UserRepository userRepository;

    private static final String userSessionKey = "user";

    @GetMapping("/register")
    public String displayRegistrationForm(Model model){
        model.addAttribute(new RegistrationFormDTO());
        model.addAttribute("title","Register");
        return "register";
    }
    @PostMapping("/register")
    public String processRegistrationForm(@ModelAttribute @Valid RegistrationFormDTO registrationFormDTO, Errors errors, HttpServletRequest request, Model model){
        if(errors.hasErrors()){
            model.addAttribute("title","Register");
            return "register";
        }
        User existingUser = userRepository.findByUsername(registrationFormDTO.getUsername());
        if(existingUser != null){
            errors.rejectValue("username","username.alreadyexists","A user with this username already exists.");
            model.addAttribute("title","Register");
            return "register";
        }
        String password = registrationFormDTO.getPassword();
        String verifyPassword = registrationFormDTO.getVerifyPassword();
        if(!password.equals(verifyPassword)){
            errors.rejectValue("password","password.mismatch","Passwords do not match.");
            model.addAttribute("title","Register");
            return "register";
        }
        User newUser = new User(registrationFormDTO.getUsername(),registrationFormDTO.getPassword());
        userRepository.save(newUser);
        setUserInSession(request.getSession(),newUser);
        return "redirect:";
    }

    @GetMapping("/login")
    public String displayLoginForm(Model model){
        model.addAttribute(new LoginFormDTO());
        model.addAttribute("title","Log In");
        return "login";
    }
    @PostMapping("/login")
    public String processLoginForm(@ModelAttribute @Valid LoginFormDTO loginFormDTO, Errors errors,HttpServletRequest request,Model model){
        if(errors.hasErrors()){
            model.addAttribute("title","Log In");
            return "login";
        }
        User theUser = userRepository.findByUsername(loginFormDTO.getUsername());
        if(theUser == null){
            errors.rejectValue("username","username.notfound","The username is not found in database.");
            model.addAttribute("title","Log In");
            return "login";
        }
        String password = loginFormDTO.getPassword();
        if(!theUser.isMatchingPassword(password)){
            errors.rejectValue("password","password.invalid","Invalid Password.");
            model.addAttribute("title","Log In");
            return "login";
        }
        setUserInSession(request.getSession(),theUser);
        return "redirect:";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:";
    }
    public User getUserFromSession(HttpSession session){
        Integer userID = (Integer) session.getAttribute(userSessionKey);
        if(userID==null){
            return null;
        }
        Optional<User> user = userRepository.findById(userID);
        if(user.isEmpty()){
            return null;
        }
        return user.get();
    }
    private static void setUserInSession(HttpSession session,User user){
        session.setAttribute(userSessionKey,user.getId());
    }
}