enum dcColType{
	Empty = 0,
      
    Object = 1,

	DBNull = 2,

	Boolean = 3,

	Char = 4,
  
	SByte = 5,
 
	Byte = 6,
 
	Int16 = 7,
	
	UInt16 = 8,
	
	Int32 = 9,
   
	UInt32 = 10,
	
	Int64 = 11,
  
	UInt64 = 12,
	
	Single = 13,
	
	Double = 14,
  
	Decimal = 15,

	DateTime = 16,

	String = 18
}

#列数据
struct dcColumn{
	1: string name #名称
	2: string descrip #描述
	3: i32 samplePeriod #采样时间 单位毫秒
	4: dcColType type #标识该列的数据类型
	5: string address  #地址
	6: binary colDat #是压缩后的数据,根据type转化成对应类型
}

#冶炼实际  一个结构体实例在HBase里是一个单元格数据
struct dcDetail{
	1: string name  #名称 
	2: string unit		#单位
	3: string value		#数值
}

#加工实绩数据
struct dcPDO{
	1: string rowkey  #物料id+采集器设备id+PLCid
	2: string startTime
	3: string endTime
	4: string deviceName #设备名称
	5: string materialName #物料名称
	6: string group #班组
	7: string shfit #班次
	8: list<dcDetail> items   #一定要有开始时间和结束时间的item
}

#加工过程数据
struct dcProduct{
  1: string rowkey
  2: string startTime
  3: string endTime
  4: string deviceName #设备名称
  5: string materialName #物料名称
  6: list<dcColumn> data #多个列数据
  7: bool isInterval #是否是定时发送的数据
 }

service dcSendProductService{
	i8 sendProduct(1:dcProduct product), #按卷发送加工过程数据
	i8 sendPDO(1:dcPDO pdo), #按卷发送加工实际数据
	list<dcPDO> getPDOByMaterialId(1:string materialId) #根据卷号获取该卷号所有PDO数据
}