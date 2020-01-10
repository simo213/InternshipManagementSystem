package com.intern.Internship.controller;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.intern.Internship.model.Candidate;
import com.intern.Internship.model.Company;
import com.intern.Internship.model.User;
import com.intern.Internship.service.CandidateService;
import com.intern.Internship.service.CompanyService;
import com.intern.Internship.service.SecurityService;
import com.intern.Internship.service.UserService;

import com.intern.Internship.utils.Email;
import com.intern.Internship.utils.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/api/auth/signup")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/api/auth/signup")
    public ResponseEntity<User> registration(@RequestBody User userForm) {
        if (userForm == null) {
            return ResponseEntity.badRequest().body(new User());
        }

        String token = getJWTToken(userForm.getUsername());
        userForm.setToken(token);

        try {
            userService.save(userForm);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new User());
        }
        if (userForm.getRole().getName().equals("CANDIDATE")) {
            Candidate candidate = new Candidate();
            candidate.setID(userForm.getUsername());
            candidateService.save(candidate);
        } else {
            Company company = new Company();
            company.setID(userForm.getUsername());
            companyService.save(company);
        }
        // securityService.autoLogin(userForm.getUsername(), userForm.getPassword());
        return ResponseEntity.ok().body(userForm);
    }

    @GetMapping("/api/auth/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "ana";
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<User> login(@RequestBody User userForm) {
        try {
            User user = userService.findByUser(userForm.getUsername(), userForm.getPassword());
            String token = getJWTToken(user.getUsername());
            user.setToken(token);
            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new User());
        }
    }

    @PostMapping("/api/auth/forgot")
    public ResponseEntity<User> forgot(@RequestBody User userForm) {
        try {
            User user = userService.findByUsername(userForm.getUsername());
            String subject = "Reset password request";
            String body = "You received this e-mail because you requested a password request for your InterMAP account. Please enter the following address to get started: http://localhost:4200/reset/" + Encryption.encrypt(user.getUsername());
            Email.sendMail(subject, body, userForm.getUsername());
            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new User());
        }
    }

    @PostMapping("/api/auth/reset")
    public ResponseEntity<User> reset(@RequestBody User userForm) {
        String username = Encryption.decrypt(userForm.getUsername());
        userService.changePassword(username, userForm.getPassword());
        User user = userService.findByUsername(username);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping({ "/", "/api/auth/welcome" })
    public String welcome(Model model) {
        return "welcome";
    }

    private String getJWTToken(String username) {
        if (username.length() > 19) {
            username = username.substring(0, 19);
        }
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts.builder().setId("softtekJWT").setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();

        return token;
    }
}