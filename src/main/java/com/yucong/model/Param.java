package com.yucong.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Param implements Serializable{


    private static final long serialVersionUID = 5669005930421911742l;

    private String name;

     private String age;

    private String sex;

}
