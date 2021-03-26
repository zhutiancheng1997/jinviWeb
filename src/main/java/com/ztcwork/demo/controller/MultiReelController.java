package com.ztcwork.demo.controller;

import com.ztcwork.demo.req.CombineVoListReq;
import com.ztcwork.demo.req.PDOByIdsAndEquipReq;
import com.ztcwork.demo.req.TimeReq;
import com.ztcwork.demo.service.DTBaseService;
import com.ztcwork.demo.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class MultiReelController {
    private static Logger logger = LoggerFactory.getLogger(MultiReelController.class);

    @Autowired
    DTBaseService dtBaseService;

    /**
     *  根据时间查询范围内的id和对应id经历的设备编号
     */
    @PostMapping(value = "/getMaterialIdByTime")
    public Result<List<MaterialIdAndEquipsVo>> getMaterialIdByTime(@RequestBody TimeReq req) {
        //todo
        logger.info("TimeReq:{}",req);
        List<MaterialIdAndEquipsVo> materials = dtBaseService.getMaterialIdByTime(req.getStartTime(), req.getEndTime());
//        List<MaterialIdAndEquipsVo> materials =new ArrayList<>();
//        ArrayList<String> list1 = new ArrayList<>();
//        list1.add("000100");list1.add("000102");list1.add("000118");list1.add("000120");
//        ArrayList<String> list2 = new ArrayList<>();
//        list2.add("000103");list2.add("000117");list2.add("000114");
//        materials.add(new MaterialIdAndEquipsVo("63538",list1));
//        materials.add(new MaterialIdAndEquipsVo("63541",list2));
//        materials.add(new MaterialIdAndEquipsVo("63542",list2));
        if(materials!=null){
            return Result.success(materials);
        }else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getPDOByIdsAndEquip",method = RequestMethod.POST)
    public Result<Map<String,PDOVo>> getPDOByIdAndEquips(@RequestBody PDOByIdsAndEquipReq req){
        logger.info("PDOByIdsAndEquipReq:{}",req);
        String equipId = req.getEquipId();
        List<String> materialIds = req.getMaterialIds();
        //todo 改回来
//        String equipId = "";
//        List<String> materialIds = new ArrayList<>();
//        materialIds.add("63538000100");
//        materialIds.add("63541000100");
        Map<String,PDOVo> map =new HashMap<>();
//        List<PDOVo> vos =new ArrayList<>();
        for(String id:materialIds){
            String prefix =id+equipId;
            PDOVo vo = dtBaseService.getPDOByMaterialId(prefix);
            if(vo!=null){
//                vos.add(vo);
                map.put(id,vo);
            }else{
                logger.info("PDO -prefix={}查询不到数据",prefix);
            }
        }
        return Result.success(map);
    }

    @RequestMapping(value = "/getCombineVoList", method = RequestMethod.POST)
    public Result<Map<String, CombineVo>> getCombineVoList(@RequestBody CombineVoListReq req) {
        logger.info("CombineVoListReq:{}", req);
        List<String> materialIds = req.getMaterialIds();
        String equipId = req.getEquipId();
        Map<String,CombineVo> voMap=new HashMap<>();
        for(String id:materialIds){
            PDPVo pdpvo = dtBaseService.getPDPByMaterialId(id+equipId);
            PDOVo pdovo = dtBaseService.getPDOByMaterialId(id+equipId);
            if(pdovo==null){
                logger.info("找不到对应的pdo数据，MaterialId : {}",id);
            }else if(pdpvo==null){
                logger.info("找不到对应的pdp数据，MaterialId : {}",id);
            }else{
                CombineVo vo =new CombineVo(pdpvo,pdovo);
                voMap.put(id,vo);
            }
        }
        //todo 下面写的测试用

//        CombineVo vo1 =test();
//        CombineVo vo2 =test();
//        voMap.put("63538",vo1);
//        voMap.put("63539",vo2);
        return Result.success(voMap);
    }

    public CombineVo test(){
        List<DetailVo> list =new ArrayList<>();
        list.add(new DetailVo().setName("宽度").setUnit("cm").setValue("100"));
        list.add(new DetailVo().setName("直径").setUnit("cm").setValue("300"));
        PDOVo pdoVo =new PDOVo()
                .setDeviceName("黄铜炉")
                .setMaterialName("")
                .setGroup("1")
                .setShift("a")
                .setStartTime("2020-03-21")
                .setEndTime("2020-03-24")
                .setItems(list);
        Map<String, ColumnVo> colMap =new HashMap<>();
        Random random = new Random();
        colMap.put("宽度",new ColumnVo()
                .setName("宽度")
                .setNumberArr(new Integer[]{
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10)}));
        colMap.put("直径",new ColumnVo()
                .setName("直径")
                .setNumberArr(new Integer[]{
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10),
                        random.nextInt(10)}));
        PDPVo pdpVo =new PDPVo()
                .setDeviceName("黄铜炉")
                .setMaterialName("")
                .setStartTime("2020-03-21")
                .setEndTime("2020-03-24")
                .setColMap(colMap);
        CombineVo vo =new CombineVo(pdpVo,pdoVo);
        return vo;
    }
}
