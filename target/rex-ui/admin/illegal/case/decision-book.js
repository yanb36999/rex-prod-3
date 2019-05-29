importResource("/admin/css/common.css");
importResource("/plugins/font-awesome/4.7.0/css/font-awesome.css");
importResource("/admin/report/jquery-rebox.css");
importResource('/plugins/miniui/themes/bootstrap/skin.css');
importMiniui(function () {
    mini.parse();
    require(["miniui-tools", "request","message"], function (tools, request, message) {
        var userId = request.getParameter("userId");
        var hphm = request.getParameter("hphm");
        var hpzl = request.getParameter("hpzl");
        var id = request.getParameter("id");
        var code = request.getParameter("code");
        hphm = hphm.substring(1);
        request.get("cars/illegal/detail/" + id+ "/" + userId + "/" + hphm + "/" + hpzl, function (response) {
            if (response.status == 200) {
                var carData = response.result.carIllegalCaseHandle.carInfo;
                var userData = response.result.user;
                var driverData = response.result.carIllegalCaseHandle.driverLicense;
                var caseHandleListData = response.result.carIllegalCaseHandle;

                editHandleStatus(request,function (handleStatus) {
                    editIllegalCode(request,caseHandleListData,id,handleStatus,userData);
                });
            } else {
                message.showTips("查询失败:" + response.message);
            }

        })
        //修改违法行为代码为相应中文状态
        function editIllegalCode(request,data,id,handleStatus, userData) {
            var illegalCode = {};
            //request.get("illegal/code/by-code/"+ "1001", function (response) {
            request.get("illegal/code/by-code/"+ code, function (response) {
                    if (response.status == 200) {
                        illegalCode[response.result.id] = response.result.contentAbbreviate;
                    }
                data.illegalCase.illegalActivities = illegalCode[data.illegalCase.illegalActivities];
                data.handleStatus = handleStatus[data.handleStatus];
                var dataTemp = data["illegalCase"];
                new mini.Form("#punishDecision").setData(data);
                $(function () {
                    $(".illegalDate").text(dataTemp["illegalTime"]);
                    $(".illegalAdderss").text(dataTemp["illegalAddress"]);
                    $(".illegalContent").text(response.result["illegalContent"]);
                    $(".illegalRegulations").text(response.result["illegalRegulations"]);
                    $(".punishBasis").text(response.result["punishBasis"]);
                    $(".defaultMoney").text(response.result["defaultMoney"]);
                    $(".illegalScore").text(response.result["illegalScore"]);
                });
                })
            }
        //修改违法处理状态代码为相对应中文状态
        function editHandleStatus(request,cb) {
            var handleStatus = {};
            request.get("cars/illegal/handle-status/", function (response) {
                if(response.status == 200){
                    for(var i = 0; i<response.result.length; i++){
                        handleStatus[response.result[i].code] = response.result[i].comment;
                    }
                }
                if(cb)cb(handleStatus);
            });
        }
        $(".decision-book-button").on("click", (function () {
            decisionBook(id,userId, hphm, hpzl,tools);
        }));
        $(".cancel-button").on("click", (function () {
            tools.closeWindow();
        }));
        $(".confirm-button").on("click", (function () {
            tools.closeWindow();
        }));
    });
});


