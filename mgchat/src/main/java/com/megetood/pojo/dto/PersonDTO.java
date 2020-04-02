package com.megetood.pojo.dto;

import com.megetood.validators.PasswordEqual;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;

/**
 * @Date 2020/3/20 21:11
 */
@Data
@Accessors(chain = true)
@PasswordEqual
public class PersonDTO {
    @Length(max = 13)
    private String name;
    private Integer age;

    private String password1;
    private String password2;

    @Valid
    private SchoolDTO schoolDTO;
}
