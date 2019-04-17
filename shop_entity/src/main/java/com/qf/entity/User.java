package com.qf.entity;

import com.sun.xml.internal.bind.v2.model.core.ID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import sun.security.util.Password;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User  implements Serializable {
    private int id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private int status;
}
