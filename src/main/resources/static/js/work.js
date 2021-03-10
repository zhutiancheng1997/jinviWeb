var layer;
var element;
var table;
//equip表格染色---单击某个单元格弹出PDO表格
var green ="rgb(187, 255, 187)";//淡绿色
var pdpData ={};
$(function () {
    layer=layui.layer;
    element=layui.element;
    table =layui.table;
    //根据初始设置 初始化表格
    initEquipTable();
});

/**
 * 按照EquipmentMap初始化设备表
 */
function initEquipTable() {
    var $table = $("#equipTable");
    //默认表格格式按照n行，10列加载
    var colCnt=10;
    var trs=[];var tr=[];
    for(var key in EquipmentMap){
        if(tr.length===colCnt){
            trs.push(tr);
            tr=[];
        }
        tr.push({"name":EquipmentMap[key],"value":key});
    }
    trs.push(tr);
    $.each(trs,function (i, item) {
       var row ="";
       $.each(item,function (j,jtem) {
           row+="<td value=\"" +jtem.value+"\" "+">"+jtem.name+"</td>";
       });
       row="<tr>"+row+"</tr>";
       $table.append(row);
    });
    var tds = $("#equipTable").find("td");
    //给设备表添加单元格单击事件
    $.each(tds,function (i,item) {
        $(item).on('click',function () {
            if(this.style.backgroundColor===green) {
                //todo 修改eqCode
                // console.log($(this)[0].innerText);//单元格内的
                // var eqCode = EquipmentMap[this.val()]//对应设备代号
                var eqCode = $(this).attr("value");//对应设备代号
                var rowkeyPrefix = materialId + eqCode;//前缀用于查询
                var pdoUrl = "/getPDO/" + rowkeyPrefix;
                var pdpUrl = "/getPDPTrend";
                //请求pdo数据加载表格
                $.ajax({
                    url: pdoUrl,
                    async: true,//Ajax调用方式为异步。false:同步加载
                    beforeSend: function () {
                        loadIndex = layer.load(2);
                    },
                    complete: function () {
                        layer.close(loadIndex);
                    },
                    success:loadPdoTable
                });

                //预请求 获得pdp趋势数据 echart需要到
                $.ajax({
                    url: pdpUrl,
                    dataType: 'json',
                    contentType: 'application/json',
                    type:"POST",
                    data:JSON.stringify({"prefix":rowkeyPrefix}),
                    success: function (r) {
                        if(r.code!=0){
                            layer.msg("没有对应的PDP数据");return;
                        }
                        pdpData=r.data;
                        // var tds = $("#dataTable").find("td");
                        var tds = $("#pdoTable").find("td");
                        //给pdo表添加单元格单击事件
                        $.each(tds,function (i,item) {
                            $(item).on('click',function () {
                                var td = $("#pdoTable").find("th")[i];
                                var colName = $(td).attr("data-field");
                                if(pdpData.colMap[colName]){
                                    var colData =pdpData.colMap[colName];
                                    var arr =colData.numberArr;
                                    var dt =[];
                                    $.each(arr,function (i, item) {
                                        dt.push([i+1,item]);
                                    });
                                    var myChart = echarts.getInstanceByDom(document.getElementById("pdpChart"));
                                    if(myChart==null){
                                        myChart = echarts.init(document.getElementById("pdpChart"));
                                    }
                                    var option = {
                                        title:{
                                            show:true,
                                            text:"",
                                            x:'center',
                                            y:'top'
                                        },
                                        grid: {
                                            left: '10%',
                                            right: '10%',
                                            width:'auto',
                                            height:'auto'
                                        },
                                        xAxis: {
                                            type:"value",
                                            name: ""
                                        },
                                        yAxis: {
                                            type:"value",
                                            name: colName
                                        },
                                        dataZoom: [
                                            {
                                                type: 'slider',
                                                show: true,
                                                yAxisIndex: [0],
                                                left: '93%',
                                                start: 1,
                                                end: 100
                                            }
                                        ],
                                        series: [{
                                            data: dt,
                                            type: 'scatter'
                                        }]
                                    };
                                    setTimeout(myChart.setOption(option), 500);
                                    // myChart.setOption(option);
                                    window.addEventListener("resize",function(){
                                        myChart.resize();
                                    });
                                }
                            });
                        });
                    }
                });
            }
        });
    });
}
//使用layui-table
function loadPdoTable1(v){
    if (v.code != 0) {
        layer.msg("找不到对应数据！");
        return;
    }
    v = v.data;
    var arr = v.items;
    var cols =[
        {
            "field": "materialName",
            "title": "材料号"
        }, {
            "field": "deviceName",
            "title": "设备号"
        }, {
            "field": "startTime",
            "title": "起始时间"
        }, {
            "field": "endTime",
            "title": "终止时间"
        }, {
            "field": "group",
            "title": "班组"
        }, {
            "field": "shift",
            "title": "班次"
        }
    ];//固定的列
    var dat = [];
    dat.push({
        "materialName": v.materialName,
        "deviceName": v.deviceName,
        "startTime": v.startTime,
        "endTime": v.endTime,
        "group": v.group,
        "shift": v.shift
    });
    $.each(arr, function (i, item) {
        var field = item.name;
        cols.push(
            {
                "field": field,
                "title": item.name + '(' + item.unit + ')',
                "event": "singleClick"
            });
        dat[0][field] = item.value;
    });
    //加载表 注意 layui table render渲染的时候 不会在原id对应的table里加载内容，而是会生成新的div和table。
    // 可使用F12调试查看
    table.render({
        id:"pdoTable",
        elem:"#pdoTable",
        cols:[cols],
        data: dat
    });
}

//使用的bootstrap-table
function loadPdoTable(v) {

    //todo 不成功，给出提示信息
    if (v.code != 0) {
        layer.msg("找不到对应数据！");
        return;
    }
    v = v.data;
    var arr = v.items;
    var dat = [];
    dat.push({
        "materialName": v.materialName,
        "deviceName": v.deviceName,
        "startTime": v.startTime,
        "endTime": v.endTime,
        "group": v.group,
        "shift": v.shift
    });
    var mycolumn = [{
        "field": "materialName",
        "title": "材料号"
    }, {
        "field": "deviceName",
        "title": "设备号"
    }, {
        "field": "startTime",
        "title": "起始时间"
    }, {
        "field": "endTime",
        "title": "终止时间"
    }, {
        "field": "group",
        "title": "班组"
    }, {
        "field": "shift",
        "title": "班次"
    }];

    $.each(arr, function (i, item) {
        var field = item.name;
        mycolumn.push(
            {
                "field": field,
                "title": item.name + '(' + item.unit + ')',
                "sortable": true
            });
        dat[0][field] = item.value;
    });
    mycolumn.push(
    {
        field: 'operate',
        title: '操作',
        events: {
            'click #export': function (e, value, row, index) {
                // layer.open({
                //     type: 1,
                //     content: '传入任意的文本或html' //这里content是一个普通的String
                // });//如果加导出的条件 在这里加

                //单击导出功能
                var coldt =[];
                var cols =[];
                // var index = 0;
                $.each(pdpData.colMap,function (i, item) {
                    if(item.numberArr!=null){
                        cols.push(item.name);
                        coldt.push(item.numberArr);
                    }
                });
                //旋转coldt 90度
                var csvdt=coldt[0].map(function(col, i) {
                    return coldt.map(function(row) {
                        return row[i];
                    })
                });
                table.exportFile(
                    cols
                    ,csvdt
                    ,'csv');

            }
        },//给按钮注册事件
        formatter: function (value, row, index) {
            var result = "";
            result += '<button id="export" class="btn btn-info" data-toggle="modal" data-target="#editModal">导出</button>';
            return result;
        } 
    });
    //加载表
    $("#pdoTable").bootstrapTable({
        data: dat,
        columns: mycolumn
    });
}
var materialId;
var test;
/**
 * 根据id搜索对应卷号经过的设备 并且将对应单元格染色
 */
function searchEquip() {
    //清空上一次加载的内容
    $("#equipTable").css("display","table");
    var tds=document.getElementsByTagName("td");
    $.each(tds,function (i, item) {
        tds[i].style.backgroundColor="white";
    });

    materialId = $("#PDOId").val();
    if(materialId==="") {
        layer.msg("输入不合法");
        return;
    }
    var url = "/getEquip/"+materialId;
    $.ajax({
        url:url,
        success:function (r) {
            if(r.code!=0){
                layer.msg("找不到对应的数据");
                return;
            }
            var equips =r.data.equips;
            var tds=$("#equipTable td");
            $.each(tds,function (i, item) {
                var equipId =$(tds[i]).attr("value");
                if(equips.indexOf(equipId)!==-1){
                    tds[i].style.backgroundColor=green;
                }else{
                    tds[i].style.backgroundColor="white";
                }
            });
        }
    });


}

/**
 * 染色的表格被单击，触发事件。加载对应的PDO作为表格展示，并且加载对应的PDP作为echart图展示
 */
function tdOnClick() {

}