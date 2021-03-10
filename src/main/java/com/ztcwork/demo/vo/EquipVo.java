package com.ztcwork.demo.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class EquipVo {
    List<String> equips;
}
