package com.intern.Internship.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User implements Serializable {
    private static final long serialVersionUID = -8413271848855656581L;

    @Id
    @Column(name = "email")
    private String username; // email
    private String password;

    @ColumnDefault("-1")
    private String token;

    @ManyToOne
    @JoinColumn
    private Role role;
}