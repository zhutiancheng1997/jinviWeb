package com.ztcwork.demo.controller;

import com.ztcwork.demo.req.PDPTrendReq;
import com.ztcwork.demo.service.DTBaseService;
import com.ztcwork.demo.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SingleReelContoller {
    private static Logger logger = LoggerFactory.getLogger(SingleReelContoller.class);

    @Autowired
    DTBaseService dtBaseService;

    /**
     * 根据id查询经过的设备
     */
    @RequestMapping(value = "/getEquip/{materialId}")
    public Result<EquipVo> getEquip(@PathVariable("materialId") String id) {
        logger.info("materialId:{}", id);
        //todo 改回来
//        List<String> equips = dtBaseService.getEquipByMaterialId(id);
        List<String> equips = new ArrayList<>();
        equips.add("000100");
        equips.add("000102");
        equips.add("000118");
        equips.add("000114");
        equips.add("000120");
        if (equips != null) {
            return Result.success(EquipVo.builder().equips(equips).build());
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

    }

    /**
     * 根据id或者说是前缀查询对应的PDO数据
     * 例：getPDO/TestProID000124
     */
    @RequestMapping(value = "/getPDO/{materialId}")
    public Result<PDOVo> getPDO(@PathVariable("materialId") String id) {
        logger.info("materialId:{}", id);
        //todo 改回来
//        PDOVo vo = dtBaseService.getPDOByMaterialId(id);
        PDOVo vo = dtBaseService.getPDOByMaterialId("TestProID000124");
        if (vo != null) {
            return Result.success(vo);
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

    /**
     * 根据查询前缀查询 PDP数据。
     * 由于PDP数据量较大，因此对每个列的数据进行/100的比例采样
     * 例子：/getPDPTrend/00010020210227232858
     */
    @PostMapping(value = "/getPDPTrend")
    public Result<PDPVo> getPDPTrend(@RequestBody PDPTrendReq req) {
        logger.info("PDPTrendReq:{}", req);
        //todo 改回来
        PDPVo vo = dtBaseService.getPDPByMaterialId("00010020210227232858");
//        PDPVo vo = dtBaseService.getPDPByMaterialId(req.getPrefix());
        if (vo != null) {
            return Result.success(vo);
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

    }
}
