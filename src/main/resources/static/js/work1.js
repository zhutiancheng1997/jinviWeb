var laydate = layui.laydate;
var form =layui.form;
// var startTime =null;
// var endTime = null;
$(function () {
    laydate.render({
        elem: '#test1'
        ,done: function(value, date, endDate){
            // startTime = (new Date(value)).getTime();
            console.log(typeof value); //得到日期生成的值，如：2017-08-18
            console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
            console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
        }
    });

    laydate.render({
        elem: '#test2'
        ,done: function(value, date, endDate){
            // endTime = (new Date(value)).getTime();
        }
    });
});

var materials={};
/**
 * 根据起始时间和结束时间搜索范围内的
 */
function searchIdByTime() {
    var startTime = $("#test1").val();
    var endTime = $("#test2").val();
    if(startTime==null||endTime==null){
        return;
    }
    startTime = (new Date(startTime)).getTime();
    endTime = (new Date(endTime)).getTime();
    //清空原来的
    $("#materialPicker").empty();
    form.render("select");
    if(endTime<startTime){
        return;
    }
    $.ajax({
        url:"/getMaterialIdByTime",
        dataType: 'json',
        contentType: 'application/json',
        type:"POST",
        data:JSON.stringify({"startTime":startTime,"endTime":endTime}),
        success:function (r) {
            if(r.code!=0){
                layer.msg("查询不到数据");return;
            }
            $.each(r.data,function (i, item) {
                var id = item.materialId;
                materials[id]=item.equips;
                $("#materialPicker").append("<option"+" value=\""+id+"\">" + id + "</option>");
            });
            form.render("select");//必须加这条才能渲染成功。
        }
    });
}

/**
 * 添加按钮点击响应函数
 * 点击后 下方表格中添加对应材料id经历的设备信息
 * 对每个设备单元格 添加单击响应事件
 */
function addMaterialId() {
    //获取第一列的id集合
    // 检查要添加的一行是否已经在其中
    var id = $("#materialPicker").val();
    if(id){
        var ids =[];//已经加载的id
        var tds = $("#equipTable tr").find("td:eq(0)");
        tds.each(function (i, item) {
            ids.push(item.innerText);
        });
        if(ids.indexOf(id)!=-1) return;//已经存在了 返回
        var equips = materials[id];
        var tr ="<tr"+" id=\""+("tr"+id)+"\">";
        tr+="<td>"+id+"</td>";
        $.each(equips,function (i, item) {
            tr+="<td"+" name=\""+item+"\">"+EquipmentMap[item]+"</td>";
        });
        tr+="</tr>";
        $("#equipTable").append(tr);
        //给新增行的单元格添加点击事件响应
        $("#equipTable td").on('click',function () {
            var equipId = $(this).attr("name");
            var equipName = $(this)[0].innerText;
            if(!equipId) return;
            var trs = $("#equipTable").find("tr");//遍历表格的其他行，看看其他材料号对应是否有经历这个设备
            var ids =[];//有的话材料号添加到ids中
            $.each(trs,function (i, item) {
                var tds =$(item).find("td");
                $.each(tds,function (j, jtem) {
                    if(jtem.innerText===equipName){//匹配到同一个设备
                        ids.push(tds[0].innerText);//添加id
                        return false;//跳出循环
                    }
                });
            });
            // var materialId = $(this).parent().attr("id").substring(2);
            //请求pdo数据加载表格
            var loadIndex;
            $.ajax({
                // url:"/getMaterialIdByTime",
                url:"/getCombineVoList",
                dataType: 'json',
                contentType: 'application/json',
                type:"POST",
                data:JSON.stringify({"materialIds":ids,"equipId":equipId}),
                // url: "/getPDO/"+materialId+equipId,
                async: true,//Ajax调用方式为异步。false:同步加载
                beforeSend: function () {
                    loadIndex = layer.load(2);
                },
                complete: function () {
                    layer.close(loadIndex);
                },
                success: pdoSuccess
            });
            //根据材料号数组ids和设备号equipId 预先加载pdp数据
        });

}
}
//下面这些是pdo表里的固定字段
var perField =["id","materialName","deviceName","startTime","endTime","group","shift"];
//存储每个materialId对应的combineVo(一个pdpVo一个pdoVo)
voMap ={};
function pdoSuccess(v) {
    //todo 不成功，给出提示信息
    if (v.code != 0) {
        layer.msg("找不到对应数据！");
        return;
    }
    voMap = v.data;
    var keys = Object.keys(voMap);
    if(keys.length==0) return;
    var mycolumn = [{
        "field": "id",
        "title": "id"
    },{
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
    //v中是PDO数组，先取出第一个来，确定表格的列名
    $.each(voMap[keys[0]].pdoVo.items,function (i,item) {
        mycolumn.push(
            {
                "field": item.name,
                "title": item.name + '(' + item.unit + ')',
                "sortable": true
            });
    });
    //拼装表格数据
    var dat = [];

    $.each(voMap,function (key, item) {
        var it =item.pdoVo;
        var d ={
            "id":key,
            "materialName": it.materialName,
            "deviceName": it.deviceName,
            "startTime": it.startTime,
            "endTime": it.endTime,
            "group": it.group,
            "shift": it.shift
        };
        $.each(it.items,function (j,jtem) {
            d[jtem.name]=jtem.value;
        });
        dat.push(d);
    });
    //加载表
    //加载之前清空原来表的内容
    var $pdoTable = $("#pdoTable");
    $pdoTable.bootstrapTable('destroy');
    $pdoTable.bootstrapTable({
        data: dat,
        columns: mycolumn,
        onClickCell:function(field, value, row, $element){
            //当单元格被点击时 取单元格对应的field,通过预加载好的PDP数据取对应field的数据进行echart绘制
            if(perField.indexOf(field)===-1){
                //y轴数据
                var _y_axis=[];
                var colors =["red","green","blue","black"];
                var c =0;
                $.each(voMap,function (key,combineVo) {
                    var colMap = combineVo.pdpVo.colMap;
                    var yd={};
                    yd.name=key;
                    yd.type="line";
                    yd.color=colors[c++];
                    yd.data=colMap[field].numberArr;
                    _y_axis.push(yd);
                });
                //x轴数据
                var _x_axis=[];
                for(var i=0;i<_y_axis[0].data.length;i++){
                    _x_axis.push(i);
                }
                // echarts.init(document.getElementById('echarts')).dispose();//销毁前一个实例
                // var myEcharts = echarts.init(document.getElementById('echarts'));//构建下一个实例
                var myChart = echarts.getInstanceByDom(document.getElementById("chart"));
                if(myChart==null){
                    myChart = echarts.init(document.getElementById("chart"));
                }
                var option = {
                    title: {
                        text: '折线图堆叠'
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        name: 'x轴',
                        data: _x_axis
                    },
                    yAxis: {
                        type: 'value',
                        name: 'y轴'
                    },
                    series: _y_axis
                };
                setTimeout(myChart.setOption(option), 500);
                window.addEventListener("resize",function(){
                    myChart.resize();
                });
            }else{
                console.log("点击了固定字段field");
            }
        }
    });

}

//移除按钮响应函数
function removeMaterialId() {
    var id = $("#materialPicker").val();
    if(id){
        var $tr = $("#tr"+id);
        $tr.remove();
    }
}

