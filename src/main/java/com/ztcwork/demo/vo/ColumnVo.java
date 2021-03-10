package com.ztcwork.demo.vo;

import com.ztcwork.demo.entity.dcColType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * type作为标志位
 */
@Data
@Accessors(chain = true)
public class ColumnVo {
    public String name; // required
    public String descrip; // required
    public int samplePeriod; // required
//    public dcColType type; // required
    public int type; // required
    public String address; // required
    public byte[] colDat; // required
//    public Integer[] intArr;
//    public Float[] floatArr;
//    public Double[] doubleArr;
    public Number[] numberArr;
    public Boolean[] boolArr;

}
