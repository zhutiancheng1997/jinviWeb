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

#������
struct dcColumn{
	1: string name #����
	2: string descrip #����
	3: i32 samplePeriod #����ʱ�� ��λ����
	4: dcColType type #��ʶ���е���������
	5: string address  #��ַ
	6: binary colDat #��ѹ���������,����typeת���ɶ�Ӧ����
}

#ұ��ʵ��  һ���ṹ��ʵ����HBase����һ����Ԫ������
struct dcDetail{
	1: string name  #���� 
	2: string unit		#��λ
	3: string value		#��ֵ
}

#�ӹ�ʵ������
struct dcPDO{
	1: string rowkey  #����id+�ɼ����豸id+PLCid
	2: string startTime
	3: string endTime
	4: string deviceName #�豸����
	5: string materialName #��������
	6: string group #����
	7: string shfit #���
	8: list<dcDetail> items   #һ��Ҫ�п�ʼʱ��ͽ���ʱ���item
}

#�ӹ���������
struct dcProduct{
  1: string rowkey
  2: string startTime
  3: string endTime
  4: string deviceName #�豸����
  5: string materialName #��������
  6: list<dcColumn> data #���������
  7: bool isInterval #�Ƿ��Ƕ�ʱ���͵�����
 }

service dcSendProductService{
	i8 sendProduct(1:dcProduct product), #�����ͼӹ���������
	i8 sendPDO(1:dcPDO pdo), #�����ͼӹ�ʵ������
	list<dcPDO> getPDOByMaterialId(1:string materialId) #���ݾ�Ż�ȡ�þ������PDO����
}