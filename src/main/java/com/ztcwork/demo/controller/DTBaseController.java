package com.ztcwork.demo.controller;

import com.ztcwork.demo.req.PDPTrendReq;
import com.ztcwork.demo.req.TimeReq;
import com.ztcwork.demo.service.DTBaseService;
import com.ztcwork.demo.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class DTBaseController {

    private static Logger logger = LoggerFactory.getLogger(DTBaseController.class);

    @Autowired
    DTBaseService dtBaseService;




    @RequestMapping(value = "/getPDR/{materialId}", method = RequestMethod.GET)
    public Result<List<PDPVo>> getPDR(@PathVariable("materialId") String id) {
        logger.info("materialId:{}", id);
        List<PDPVo> vos = dtBaseService.getPDRByMaterialId(id);
        if (vos != null) {
            return Result.success(vos);
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

    //http://localhost:8090/getPDP/00010820210225234933
    //可使用本地缓存优化
    @RequestMapping(value = "/getPDP/{materialId}", method = RequestMethod.GET)
    public Result<PDPVo> getPDP(@PathVariable("materialId") String id) {
        logger.info("materialId:{}", id);
        PDPVo vo = dtBaseService.getPDPByMaterialId(id);
        if (vo != null) {
            return Result.success(vo);
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }






}