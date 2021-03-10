
//下拉三角
triangleHtml ="<span class=\"caret\">";
var pdpDataList={};
var pdpData ={};
// colMap = {};//存下面ajax数据

/**
 * 菜单选择函数
 * @constructor
 */
function PDOChoose() {
    $("#choose_btn").text("PDO");
    $("#choose_btn").append(triangleHtml)
}
function PDRChoose() {
    $("#choose_btn").text("PDR");
    $("#choose_btn").append(triangleHtml)

}
function PDPChoose() {
    $("#choose_btn").text("PDP");
    $("#choose_btn").append(triangleHtml)

}

/**
 * 根据材料号搜索PDO、PDP、PDR
 */
function searchPDX() {
    var pdx = $("#choose_btn").text();
    var id = $("#id_input").val();
    //清空表
    $("#dataTable").bootstrapTable('destroy');
    switch (pdx) {
        case "PDP":
            break;
        case "PDR":
            pdpAjax(id);
            break;
        case "PDO":
            pdoAjax(id);
            break;
    }
}


/**
 * 一共5个div
 * 输入查询框是第一个 用于输入PDO PDP PDR的查询信息进行查询
 * layui tab是第二个 只在PDP查询时显示 里面嵌了echart 块
 * bootstrap-table是第三个
 * 模态框是第五个  不占主页面内容
 */

function pdoAjax(id) {
    $("#dataTable").css("display","block");
    // $("#PDPChart").css("display","none");
    $("#pdpTab").css("display","none");
    //清空pdp的tab
    var element = layui.element;
    $.each(pdpDataList,function (i, item) {
        element.tabDelete('pdpTab', item.rowkey);
    });

    var s = "/getPDO/"+id;
    var layer = layui.layer;
    var loadIndex;
    $.ajax({
            url: s,
            async:true,//Ajax调用方式为异步。false:同步加载
            beforeSend: function () {
                loadIndex = layer.load(2);
            },
            complete: function () {
                layer.close(loadIndex);
            },
            success: function (v) {
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
                console.log("查询PDO，id=" + id);
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
                // console.log(dat);
                //加载表
                $("#dataTable").bootstrapTable({
                    data: dat,
                    columns: mycolumn
                });
            }
        });
}



function pdpAjax(id) {
    var s = "/getPDP/"+id;
    var layer = layui.layer;
    var loadIndex;
    $("#pdpTab").css("display","block");
    $("#dataTable").css("display","none");
    $.ajax({
        url: s,
        async:true,//Ajax调用方式为异步。false:同步加载
        beforeSend: function () {
            loadIndex = layer.load(2);
        },
        complete: function () {
            layer.close(loadIndex);
        },
        success: function (v) {
            //todo 不成功，给出提示信息
            if (v.code != 0) {
                layer.msg("找不到对应数据！");
                return;
            }
            ///防止重复查询生成相同的tab
            if(pdpDataList[v.data.rowkey]!=null){
                return;
            }
            pdpDataList[v.data.rowkey]=v.data;
            pdpData=v.data;
            //生成tab数据块
            var element =layui.element;
            element.tabAdd('pdpTab', {
                title: pdpData.rowkey
                ,content: "deviceName:"+pdpData.deviceName+"<br>startTime:"+pdpData.startTime+"<br>endTime:"+pdpData.endTime+"<br>"
                    +"<button class=\"btn btn-default\" type=\"button\" onclick=\"PDPModalShow()\">绘制图像</button>"
                    +"<div class=\"chart\" id=\"chart"+pdpData.rowkey+"\""+" style=\"height: 500px;width: 1000px\" ></div>"
                ,id: pdpData.rowkey
            });

        }
    });
}
function PDPModalShow() {
    //加载模态框内容,模态框展示供选择
    $("#PDPModal").modal('show');
    var $picker1 = $("#PDPPicker1");
    var $picker2 = $("#PDPPicker2");
    $.each(pdpData.colMap, function (i, item) {
        // $picker1.append("<option>" + item.descrip + "</option>");
        // $picker1.append("<option>" + item.descrip + "</option>");
        $picker1.append("<option>" + item.name + "</option>");
        $picker2.append("<option>" + item.name + "</option>");
    });
    $picker1.selectpicker("refresh");
    $picker2.selectpicker("refresh");
}
function PDPModalOnClick() {

    var $picker1 = $("#PDPPicker1");
    var $picker2 = $("#PDPPicker2");
    var colName1 =$picker1.val();
    var colName2 =$picker2.val();

    if(colName1==""||colName2==""){
        var layer =layui.layer;
        layer.msg("选择列！")
    }
    $("#dataTable").css("display","none");
    // $("#PDPChart").css("display","block");
    pdpEchartPaint(colName1,colName2);
    //模态框确认后操作，关闭，删除菜单栏选项
    $("#PDPModal").modal('hide');
    $picker1.children("option").remove();
    $picker2.children("option").remove();
}
function pdpEchartPaint(colName1,colName2) {
    var xdata = getArr(pdpData.colMap[colName1]);
    var ydata = getArr(pdpData.colMap[colName2]);
    var data =[];
    $.each(xdata,function (i, item) {
        data.push([item,ydata[i]]);
    });
    var chartId = $("#pdpTab .layui-show .chart").attr("id");
    var myChart = echarts.getInstanceByDom(document.getElementById(chartId));
    // var myChart = echarts.getInstanceByDom($(".layui-show.chart"));
    if(myChart==null){
        myChart = echarts.init(document.getElementById(chartId));
    }
    // var title="采样时间:"+"["+d.samplePeriod+"ms]"+"  地址: "+"["+d.address+"]"+"  描述: "+"["+d.descrip+"]";
    option = {
        title:{
            show:true,
            text:"",
            x:'center',
            y:'top'
        },
        grid: {
            // top: 35,
            left: '10%',
            right: '10%',
            // right: 45,
            width:'auto',
            height:'auto'
        },
        xAxis: {
            name: colName1
        },
        yAxis: {
            name: colName2
        },
        dataZoom: [
            {
                type: 'slider',
                show: true,
                xAxisIndex: [0],
                start: 1,
                end: 100
            },
            {
                type: 'slider',
                show: true,
                yAxisIndex: [0],
                left: '93%',
                start: 1,
                end: 100
            },
            {
                type: 'inside',
                xAxisIndex: [0],
                start: 1,
                end: 100
            },
            {
                type: 'inside',
                yAxisIndex: [0],
                start: 1,
                end: 100
            }
        ],
        series: [{
            data: data,
            type: 'scatter'
        }]
    };
    myChart.setOption(option);
    window.addEventListener("resize",function(){
        myChart.resize();
    });
}


function getArr(colDat) {
    var arr;
    if(colDat.type===ColType.Double){
        arr = colDat.doubleArr;
    }else if(colDat.type===ColType.Int32){
        arr = colDat.intArr;
    }else if(colDat.type===ColType.Int64){
        arr = colDat.longArr;
    }else if(colDat.type===ColType.Boolean){
        arr = colDat.boolArr;
    }
    return arr;
}

$(function(){
    var element =layui.element;
    element.on('tab(pdpTab)', function(data){
        // console.log("tab onchange");
        var key = $("#pdpTab .layui-this").attr("lay-id");
        pdpData=pdpDataList[key];

    });
    element.on('tabDelete(pdpTab)', function(data){
        var key = $("#pdpTab .layui-this").attr("lay-id");
        delete pdpDataList[key];
    });

});