package com.ztcwork.demo.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.ztcwork.demo.config.dcConfig;
import com.ztcwork.demo.config.dcPDOConfig;
import com.ztcwork.demo.config.dcProductConfig;
import com.ztcwork.demo.entity.*;
import com.ztcwork.demo.utils.GzipUtil;
import com.ztcwork.demo.vo.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DTBaseService {

    private static Logger logger = LoggerFactory.getLogger(DTBaseService.class);

    Connection conn = null;

    public DTBaseService() {
        Configuration conf = HBaseConfiguration.create(); // 会自动加载hbase-site.xml
        conf.set(HConstants.ZOOKEEPER_QUORUM,
                dcConfig.zookeepers);

        conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, "2181");
        try {
            conn = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            logger.error("连接到数据库失败,检查配置！");
            e.printStackTrace();
        }
    }


    public List<String> getEquipByMaterialId(String id){
        Table table = getTable(dcPDOConfig.PDO_TABLE_NAME);
        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(Bytes.toBytes(id)));
//        Map<String,Boolean> equipMap =new HashMap<>();
        List<String> equips =new ArrayList<>();
        try {
            ResultScanner res = table.getScanner(scan);
            Iterator<Result> iterator = res.iterator();
            if(!iterator.hasNext()){
                return null;
            }
            while(iterator.hasNext()){
                Result r = iterator.next();
                String rowkey = Bytes.toString(r.getRow());
                //todo  根据rowkey的拼凑规则截取设备号
                //todo PDO表的rowkey组成暂时是 前10位为材料号，后5位为采集站号，也就是设备号
                String equipId = rowkey.substring(
                        dcPDOConfig.MATERIAL_ID_INDEX,
                        dcPDOConfig.EQUIP_ID_INDEX);
                equips.add(equipId);
                logger.info("equipId:{}",equipId);
            }
        }catch (IOException e){
            logger.error("HBase查询出错,MaterialId:{}",id);
        }
        return equips;
    }
    public List<PDPVo> getPDRByMaterialId(String id){
        Table t = this.getTable(dcProductConfig.PDR_TABLE_NAME);
        Scan scan = new Scan();
        Filter filter =new PrefixFilter(Bytes.toBytes(id));
        scan.setFilter(filter);
        List<PDPVo> vos =new ArrayList<>();
        try {
            ResultScanner scanner = t.getScanner(scan);
            Iterator<Result> iterator = scanner.iterator();
            if(!iterator.hasNext()){
                return null;
            }
            while (iterator.hasNext()){
                Result r = iterator.next();
                dcProduct dcProduct = result2PDP(r);
                PDPVo pdpVo = pdp2Vo(dcProduct);
                vos.add(pdpVo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return vos;
    }

    public PDPVo getPDPByMaterialId(String id) {
        dcProduct pdp =null;
//        Table t = this.getTable(dcProductConfig.PDR_TABLE_NAME);
        Table t = this.getTable(dcProductConfig.PDP_TABLE_NAME);
        Scan scan = new Scan();
        Filter filter =new PrefixFilter(Bytes.toBytes(id));
        scan.setFilter(filter);
        try {
            ResultScanner scanner = t.getScanner(scan);
            Result r = scanner.next();//由于一个材料号id在PDP表中肯定只对应一条数据,因此取出第一条就可以了
            if (r == null) {
                logger.info("找不到对应的数据，MaterialId : " + id);
                return null;
            }
            pdp =result2PDP(r);
        }catch (Exception e){
            e.printStackTrace();
        }
        return pdp2Vo(pdp);
    }
    private dcProduct result2PDP(Result r){
        Map<String,dcColumn> columnMap =new HashMap<>();
        dcProduct pdp =new dcProduct();
        String rowkey = Bytes.toString(r.getRow());
        pdp.setRowkey(rowkey);
        for(Cell kv:r.rawCells()){
            String colFamily = Bytes.toString(CellUtil.cloneFamily(kv));
            String colName = Bytes.toString(CellUtil.cloneQualifier(kv));
            if(dcProductConfig.PERMANENT_FILED_SET.contains(colName)){
                pdp.setFieldValue(dcProduct._Fields.findByName(colName), Bytes.toString(CellUtil.cloneValue(kv)));
                continue;
            }
            dcColumn column;
            if (columnMap.containsKey(colName)) {
                column=columnMap.get(colName);
            } else {
                column = new dcColumn();
                columnMap.put(colName,column);
            }
            if(dcProductConfig.INFO_COL_FAMILY.equals(colFamily)){
                //取出列信息
                String jsonString = Bytes.toString(CellUtil.cloneValue(kv));
                if(StrUtil.isEmpty(jsonString)){
                    logger.info("info json数据为空，rowkey : "+rowkey);
                    return null;
                }
                JSONObject map = JSONObject.parseObject(jsonString);
                String name = (String) map.get(dcColumn._Fields.NAME.getFieldName());
                String desc = (String) map.get(dcColumn._Fields.DESCRIP.getFieldName());
                Integer sp = (Integer) map.get(dcColumn._Fields.SAMPLE_PERIOD.getFieldName());
                dcColType type = dcColType.findByValue((Integer)map.get(dcColumn._Fields.TYPE.getFieldName()));
                String address = (String) map.get(dcColumn._Fields.ADDRESS.getFieldName());
                column.setName(name).setDescrip(desc).setSamplePeriod(sp).setType(type).setAddress(address);
            }else{
                //取出列数据 如果长度不对，使用下面的System.arraycopy()去做
                column.setColDat(CellUtil.cloneValue(kv));
            }
        }
        List<dcColumn> columnList =new ArrayList<>();
        for(Map.Entry<String,dcColumn> entry:columnMap.entrySet()){
            columnList.add(entry.getValue());
        }
        pdp.setData(columnList);
        return pdp;
    }
    private PDPVo pdp2Vo(dcProduct pdp){
        if(pdp==null) return null;
        PDPVo pdpVo = new PDPVo()
                .setMaterialName(pdp.getMaterialName())
                .setDeviceName(pdp.getDeviceName())
                .setStartTime(pdp.getStartTime())
                .setEndTime(pdp.getEndTime())
                .setRowkey(pdp.getRowkey());
        Map<String,ColumnVo> map = new HashMap<>();
        for(dcColumn column:pdp.getData()){
            map.put(column.getName(),dcColumn2ColumnVo(column));
        }
        pdpVo.setColMap(map);
        return pdpVo;
    }
    private ColumnVo dcColumn2ColumnVo(dcColumn c){
        int type = c.getType().getValue();
        ColumnVo columnVo = new ColumnVo()
                .setName(c.getName())
                .setDescrip(c.getDescrip())
                .setAddress(c.getAddress())
                .setSamplePeriod(c.getSamplePeriod())
                .setType(type);
        //解压
        byte[] uncompress = GzipUtil.uncompress(c.getColDat());
        //解析成对应数据类型数组
        Number[] numbers = null;
        Class z = null;
        if(type==dcColType.Single.getValue()) {
            numbers=GzipUtil.Bytes2Floats(uncompress);
        }
        else if(type==9){
            numbers = GzipUtil.Bytes2Floats(uncompress);
        }
        else if(type==dcColType.Double.getValue()){
            numbers = GzipUtil.Bytes2Doubles(uncompress);
        }else if(type==dcColType.Int32.getValue()){
            //对应int
            numbers =GzipUtil.Bytes2Ints(uncompress);
        }else if(type==dcColType.Int64.getValue()){
            //todo 长整型的处理
            numbers=null;
        }else if(type==dcColType.Boolean.getValue()){
            //todo 布尔类型的处理
            numbers = GzipUtil.Bytes2Boolean(uncompress);
        }
        if(numbers==null) return columnVo;
        int scale =100;
        if(numbers.length<1000){
            return columnVo.setNumberArr(numbers);
        }
        int x =numbers.length /scale;
        List<Number> numberList =new ArrayList<>();
        for(int i =0;i<x;i++){
            Number a=0;
            for(int j=0;j<scale;j++){
                a =a.doubleValue() + numbers[j+i*scale].doubleValue()/scale;
            }
//          arr.getClass()
//          todo 这里可以根据 arr.getClass()进行强转之后再添加
            numberList.add(a);
        }
        columnVo.setNumberArr(numberList.toArray(new Number[]{}));
        return columnVo;
    }


    /**
     * 根据表名获取Table对象
     *
     * @param tableName 表名
     * @return Table对象
     */
    private Table getTable(String tableName) {
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            logger.error("获取表" + tableName + "失败,检查表名！");
            e.printStackTrace();
        }
        return table;
    }



    /**
     * 插入dcProduct数据
     *
     * @param product
     * @return 插入是否成功
     */
    private boolean insertDCProduct(dcProduct product) {
        boolean insert = false;
        String tableName = product.isInterval ? dcProductConfig.PDR_TABLE_NAME : dcProductConfig.PDP_TABLE_NAME;//根据标志位获取表名
        Table table = getTable(tableName);
        String rowkey = product.getRowkey();
        Put put = new Put(Bytes.toBytes(rowkey));
        if (product.isSetStartTime()) {
            put.addColumn(Bytes.toBytes(dcProductConfig.DATA_COL_FAMILY), dcProductConfig.START_TIME, product.getStartTime().getBytes());
        }
        if (product.isSetEndTime()) {
            put.addColumn(Bytes.toBytes(dcProductConfig.DATA_COL_FAMILY), dcProductConfig.END_TIME, product.getEndTime().getBytes());
        }
        if (product.isSetDeviceName()) {
            put.addColumn(Bytes.toBytes(dcProductConfig.DATA_COL_FAMILY), dcProductConfig.DEVICE_NAME, product.getDeviceName().getBytes());
        }
        if (product.isSetMaterialName()) {
            put.addColumn(Bytes.toBytes(dcProductConfig.DATA_COL_FAMILY), dcProductConfig.MATERIAL_NAME, product.getMaterialName().getBytes());
        }
        if (product.isSetData()) {
            for (dcColumn column : product.getData()) {
                JSONObject map = new JSONObject(true);//关键要设置为true，否则乱序
                //这些字段固定json存储
                map.put(dcColumn._Fields.NAME.getFieldName(), column.getName());
                map.put(dcColumn._Fields.DESCRIP.getFieldName(), column.getDescrip());
                map.put(dcColumn._Fields.SAMPLE_PERIOD.getFieldName(), column.getSamplePeriod());
                map.put(dcColumn._Fields.TYPE.getFieldName(), column.getType().getValue());
//                map.put(dcColumn._Fields.TYPE.getFieldName(), column.getType());
                map.put(dcColumn._Fields.ADDRESS.getFieldName(), column.getAddress());

                put.addColumn(Bytes.toBytes(dcProductConfig.INFO_COL_FAMILY), Bytes.toBytes(column.getName()), Bytes.toBytes(map.toString()));
                put.addColumn(Bytes.toBytes(dcProductConfig.DATA_COL_FAMILY), Bytes.toBytes(column.getName()), column.getColDat());
            }
            try {
                table.put(put);
                insert = true;
            } catch (IOException e) {
                logger.error("插入dcProduct数据失败,rowkey = " + rowkey);
                e.printStackTrace();
            }
        } else {
            logger.error("接受到dcProduct数据包data为空,rowkey = " + rowkey);
        }

        return insert;
    }

    private boolean insertPDO(dcPDO pdo) {
        boolean insert = false;
        Table table = getTable(dcPDOConfig.PDO_TABLE_NAME);
        String rowkey = pdo.getRowkey();
        Put put = new Put(Bytes.toBytes(rowkey));
        if (pdo.isSetStartTime()) {
            put.addColumn(Bytes.toBytes(dcPDOConfig.COL_FAMILY), dcPDOConfig.START_TIME, Bytes.toBytes(pdo.getStartTime()));
        }
        if (pdo.isSetEndTime()) {
            put.addColumn(Bytes.toBytes(dcPDOConfig.COL_FAMILY), dcPDOConfig.END_TIME, Bytes.toBytes(pdo.getEndTime()));
        }
        if (pdo.isSetDeviceName()) {
            put.addColumn(Bytes.toBytes(dcPDOConfig.COL_FAMILY), dcPDOConfig.DEVICE_NAME, Bytes.toBytes(pdo.getDeviceName()));
        }
        if (pdo.isSetMaterialName()) {
            put.addColumn(Bytes.toBytes(dcPDOConfig.COL_FAMILY), dcPDOConfig.MATERIAL_NAME, Bytes.toBytes(pdo.getMaterialName()));
        }
        if (pdo.isSetGroup()) {
            put.addColumn(Bytes.toBytes(dcPDOConfig.COL_FAMILY), dcPDOConfig.GROUP, Bytes.toBytes(pdo.getGroup()));
        }
        if (pdo.isSetShfit()) {
            put.addColumn(Bytes.toBytes(dcPDOConfig.COL_FAMILY), dcPDOConfig.SHIFT, Bytes.toBytes(pdo.getShfit()));
        }
        for (dcDetail detail : pdo.items) {
            put.addColumn(Bytes.toBytes(dcPDOConfig.COL_FAMILY),
                    Bytes.toBytes(StrUtil.join(dcPDOConfig.SPLIT, detail.getName(), detail.getUnit())),
                    Bytes.toBytes(detail.getValue()));
        }
        try {
            table.put(put);
            insert = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return insert;
    }

    /**
     * 根据材料号id获取pdo数据
     *
     * @param id 材料号id
     * @return pdo数据
     */
    public PDOVo getPDOByMaterialId(String id) {
        Table table = getTable(dcPDOConfig.PDO_TABLE_NAME);
        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(Bytes.toBytes(id)));
        dcPDO pdo =null;
        try {
            ResultScanner res = table.getScanner(scan);
            if(res==null){
                return null;
            }
            Result r = res.next();
            if(r==null) {
                return null;
            }
            pdo = new dcPDO();
            List<dcDetail> details = new ArrayList<>();
            for (Cell kv : r.rawCells()) {
                String colName = Bytes.toString(CellUtil.cloneQualifier(kv));
                if (dcPDOConfig.PERMANENT_FILED_SET.contains(colName)) {
                    pdo.setFieldValue(dcPDO._Fields.findByName(colName), Bytes.toString(CellUtil.cloneValue(kv)));
                } else {
                    List<String> nameAndUnit = StrUtil.splitTrim(colName, dcPDOConfig.SPLIT);
                    String name="";String unit="";
                    if(nameAndUnit.size()==1){
                        name=nameAndUnit.get(0);
                    }else if(nameAndUnit.size()==2){
                        name=nameAndUnit.get(0);
                        unit=nameAndUnit.get(1);
                    }
                    dcDetail d = new dcDetail(name, unit, Bytes.toString(CellUtil.cloneValue(kv)));
                    details.add(d);
                }
            }
            pdo.setItems(details);
        }catch (IOException e){
            logger.error("HBase查询出错,MaterialId:{}",id);
        }
        if(pdo==null) return null;
        return pdo2vo(pdo);
    }

    private PDOVo pdo2vo(dcPDO pdo){
        List<dcDetail> items = pdo.getItems();
        List<DetailVo> dvos =new ArrayList<>();
        for(dcDetail detail:items){
            DetailVo dvo =new DetailVo()
                    .setName(detail.getName())
                    .setUnit(detail.getUnit())
                    .setValue(detail.getValue());
            dvos.add(dvo);
        }
        //todo 删除
//        DetailVo dvo1 =new DetailVo()
//                .setName("温度")
//                .setUnit("°")
//                .setValue("100");
//        DetailVo dvo2 =new DetailVo()
//                .setName("长度")
//                .setUnit("cm")
//                .setValue("230");
//        //MINO四辊粗轧机.ABB_BENDING_ON:
//        DetailVo dvo3 =new DetailVo()
//                .setName("MINO四辊粗轧机.ABB_BENDING_ON")
//                .setUnit("cm")
//                .setValue("234");
//        dvos.add(dvo1);
//        dvos.add(dvo2);
//        dvos.add(dvo3);
        PDOVo vo =new PDOVo()
                .setDeviceName(pdo.getDeviceName())
                .setEndTime(pdo.getEndTime())
                .setStartTime(pdo.getStartTime())
                .setGroup(pdo.getGroup())
                .setMaterialName(pdo.getMaterialName())
                .setShift(pdo.getShfit())
                .setItems(dvos);
        return vo;
    }


    public List<MaterialIdAndEquipsVo> getMaterialIdByTime(Long startTime, Long endTime) {
        Table table = getTable(dcPDOConfig.PDO_TABLE_NAME);
        Scan scan = new Scan();
        Map<String,List<String>> map =new HashMap<>();
        List<MaterialIdAndEquipsVo> vos =null;
        try {
            scan.setTimeRange(startTime,endTime);
            ResultScanner scanner = table.getScanner(scan);
            Iterator<Result> iterator = scanner.iterator();
            if(!iterator.hasNext()){return null;}
            while(iterator.hasNext()){
                Result r = iterator.next();
                String rowkey = Bytes.toString(r.getRow());
                String materialId =rowkey.substring(0,dcPDOConfig.MATERIAL_ID_INDEX);
                String equip =rowkey.substring(dcPDOConfig.MATERIAL_ID_INDEX,dcPDOConfig.EQUIP_ID_INDEX);
                if(map.containsKey(materialId)){
                    map.get(materialId).add(equip);
                }else{
                    List<String> eqs =new ArrayList<>();
                    eqs.add(equip);
                    map.put(materialId,eqs);
                }
            }
            vos = map.entrySet().stream()
                    .map(e -> new MaterialIdAndEquipsVo(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vos;
    }
}
