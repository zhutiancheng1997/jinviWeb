package com.ztcwork.demo.vo;

import com.ztcwork.demo.entity.dcDetail;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PDOVo {
    public String rowkey; // required
    public String startTime; // required
    public String endTime; // required
    public String deviceName; // required
    public String materialName; // required
    public String group; // required
    public String shift; // required
    public List<DetailVo> items; // required
}
