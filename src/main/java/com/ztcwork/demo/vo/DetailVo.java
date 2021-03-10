package com.ztcwork.demo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DetailVo {
    public String name; // required
    public String unit; // required
    public String value;
}
