package com.ztcwork.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
public class QualityVo {
    String materialId;//材料号
    List<String> equips;//经历的设备号
    Map<String,String> qualityDt;//根据材料号从mes拿到的质量数据
}
