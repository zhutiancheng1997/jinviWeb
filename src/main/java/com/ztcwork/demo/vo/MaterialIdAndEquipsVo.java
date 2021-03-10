package com.ztcwork.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 一卷经过多个设备
 * materialId是材料号
 * equips是经过的设备号
 */
@AllArgsConstructor
@Data
public class MaterialIdAndEquipsVo {
    String materialId;
    List<String> equips;
}
