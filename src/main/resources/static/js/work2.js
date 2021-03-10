var laydate = layui.laydate;
var form =layui.form;
var startTime =null;
var endTime = null;
$(function () {
    laydate.render({
        elem: '#test1'
        ,done: function(value, date, endDate){
            startTime = (new Date(value)).getTime();
            console.log(typeof value); //得到日期生成的值，如：2017-08-18
            console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
            console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
        }
    });

    laydate.render({
        elem: '#test2'
        ,done: function(value, date, endDate){
            endTime = (new Date(value)).getTime();
        }
    });
});

var materials={};
/**
 * 根据起始时间和结束时间搜索范围内的
 */
function searchIdByTime() {
    if(startTime==null||endTime==null){
        return;
    }
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


//添加按钮点击响应函数
function addMaterialId() {
    //获取第一列的id集合
    //检查要添加的一行是否已经在其中
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
        //删了

    }
}

//移除按钮响应函数
function removeMaterialId() {
    var id = $("#materialPicker").val();
    if(id){
        var $tr = $("#tr"+id);
        $tr.remove();
    }
}