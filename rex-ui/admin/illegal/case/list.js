importResource("/admin/css/common.css");

var handleSignData = [];
importMiniui(function () {
    mini.parse();
    require(["miniui-tools","request","message"], function (tools,request,message) {
        request.get("cars/illegal/handle-status", function (resp) {
            if (resp.status === 200) {
                for (var i = 0; i < resp.result.length; i++) {
                    handleSignData.push({text: resp.result[i].comment, id: resp.result[i].code});
                }
                mini.getbyName("handleStatus$EQ").setData( handleSignData);
            }
        });
        window.tools = tools;
        var grid = window.grid = mini.get("datagrid");
        grid.load();
        tools.initGrid(grid);
        $(".search-button").on("click", function () {
            search();
        });
        $(".refresh-button").on("click", function () {
            grid.reload();
        });
        var illegalCode = {};
        var handleStatusAll = {};
        //修改机动车违法行为对应中文状态
        request.get("illegal/code?paging=false", function (response) {
            if (response.status == 200) {
                for(var i = 0; i<response.result.data.length; i++){
                    illegalCode[response.result.data[i].id] = response.result.data[i].contentAbbreviate;
                }
                //修改处理状态为对应中文状态
                request.get("cars/illegal/handle-status", function (handle) {
                    var handleSign = [];
                    if (handle.status == 200) {
                        for(var i = 0; i < handle.result.length; i++){
                            var temphandleSignMap = {};
                            handleStatusAll[handle.result[i].code] = handle.result[i].text;
                            temphandleSignMap["id"] = handleStatusAll[handle.result[i].code];
                            temphandleSignMap["text"] = handleStatusAll[handle.result[i].text];
                            handleSign.push(temphandleSignMap);
                        }

                        grid.setUrl(API_BASE_PATH + "cars/illegal");
                       // mini.getbyName("illegalCase.handleSign$EQ").setData(handleSign);

                        window.renderhandleSign = function (e) {
                            return handleStatusAll[e.value];
                        }
                        window.renderillegalActivities = function (e) {
                            return illegalCode[e.value];
                        }
                    } else {
                        message.showTips("处理状态查询失败:" + handle.message);
                    }
                });
            } else {
                message.showTips("违法行为查询失败:" + response.message);
            }
        });
        mini.getbyName("handleStatus$EQ").on('valuechanged', function (e) {
            var handleSign = mini.getbyName("handleStatus$EQ").getValue();
            search();
        });
        function search() {
            tools.searchGrid("#search-box", grid);
        }
        tools.bindOnEnter("#search-box", search);
    });
});
function edit(id,userId, hphm,code, hpzl) {
    tools.openWindow("admin/illegal/case/detail.html?id=" + id + "&hphm="+ hphm + "&userId="+ userId +"&hpzl=" + hpzl + "&code=" + code, "编辑认证信息", "100%", "100%", function (e) {
        grid.reload();
    })
}
function updatePermissionStatus(id, status) {

}
window.renderAction = function (e) {
    var row = e.record;
    var html = [
        tools.createActionButton("查看详情", "icon-edit", function () {
            edit(row.id, row.userId, row.illegalCase.plateNumber, row.illegalCase.illegalActivities,row.illegalCase.plateType);
        })
    ];
    return html.join("");
}

