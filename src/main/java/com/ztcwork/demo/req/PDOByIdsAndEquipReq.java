package com.ztcwork.demo.req;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PDOByIdsAndEquipReq {
    List<String> materialIds;
    String equipId;
}
