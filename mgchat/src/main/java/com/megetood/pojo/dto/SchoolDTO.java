package com.megetood.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * @Date 2020/3/20 21:35
 */
@Data
@Accessors(chain = true)
public class SchoolDTO {
    @Length(min = 3)
    private String name;
}
