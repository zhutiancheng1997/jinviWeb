package com.ztcwork.demo.controller;

import com.ztcwork.demo.req.PDPTrendReq;
import com.ztcwork.demo.req.TimeReq;
import com.ztcwork.demo.service.DTBaseService;
import com.ztcwork.demo.vo.CodeMsg;
import com.ztcwork.demo.vo.MaterialIdAndEquipsVo;
import com.ztcwork.demo.vo.PDPVo;
import com.ztcwork.demo.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class QualityController {
    private static Logger logger = LoggerFactory.getLogger(QualityController.class);

    @Autowired
    DTBaseService dtBaseService;

//    //00010020210227232858
//    @PostMapping(value = "/getQuality")
    //根据时间查询范围内的id

}
