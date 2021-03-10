package com.ztcwork.demo.req;

import lombok.Data;

import java.util.List;

@Data
public class CombineVoListReq {
    List<String> materialIds;
    String equipId;
}
