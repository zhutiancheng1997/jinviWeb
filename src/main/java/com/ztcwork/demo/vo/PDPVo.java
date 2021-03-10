package com.ztcwork.demo.vo;

import com.ztcwork.demo.entity.dcColumn;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Primary;

import java.security.PrivateKey;
import java.util.Map;

@Data
@Accessors(chain = true)
public class PDPVo {
    private Map<String, ColumnVo> colMap;
    private String rowkey;
    private String startTime;
    private String endTime;
    private String deviceName;
    private String materialName;
}
