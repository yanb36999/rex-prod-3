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
        var allData = {};
        request.get("cars/illegal/detail/"+ id +"/" + userId + "/" + hphm + "/" + hpzl, function (response) {
            if (response.status == 200) {
                console.log(response.result);
                var carData = response.result.carIllegalCaseHandle.carInfo;
                var userData = response.result.user;
                var driverData = response.result.carIllegalCaseHandle.driverLicense;
                var businessLoggerData = response.result.illegalCaseHistories;
                var caseHandleListData = response.result.carIllegalCaseHandle;
                //修改用户性别代码为中文 。 插入用户信息到carData and carDriver
                for ( var key in userData) {
                    if(key == 'sex'){
                        userData[key] =  userData[key] == 0 ? "男" : "女";
                    }
                    if(!carData[key]){
                        carData[key] = userData[key];
                    }
                    if(!driverData[key]){
                        driverData[key] = userData[key];
                    }
                }
                //按数据字典修改carData 对应显示中文状态
                for ( var key in driverData) {
                    if(!caseHandleListData[key]){
                        caseHandleListData[key] = driverData[key];
                    }
                }
                //驾驶证可处理计分换算
                driverData.totalScore = 12 - driverData.totalScore+"分";
                //有效期间换算
                var endDateStr = driverData.endValidDate;
                var startDateStr = driverData.startValidDate;
                driverData.validDate = (parseInt(endDateStr.substring(0,4)) - parseInt(startDateStr.substring(0,4))) + "年";
                allData = driverData;
                for ( var key in carData) {
                    if(!allData[key]){
                        allData[key] = carData[key];
                    }
                }
               // new mini.Form("#driverLicenseInfo").setData(driverData);
               // new mini.Form("#userCarInfo").setData(carData);
                var grid = window.grid = mini.get("datagrid");

                editHandleStatus(request,function (handleStatus) {
                    editIllegalCode(request,grid,caseHandleListData,id,handleStatus,businessLoggerData);
                    window.renderhandleStatus = function (e) {
                        return handleStatus[e.value];
                    }
                });
            } else {
                 message.showTips("查询失败:" + response.message);
            }

        })
        //修改违法行为代码为相应中文状态
        function editIllegalCode(request,grid,data,id,handleStatus, businessLoggerData) {
            var illegalCode = {};
            request.get("illegal/code?paging=false", function (response) {
                if (response.status == 200) {
                    for(var i = 0; i<response.result.data.length; i++){
                        illegalCode[response.result.data[i].id] = response.result.data[i].contentAbbreviate;
                    }
                }
                //获得当前违法详情
                data.illegalCase.illegalActivities = illegalCode[data.illegalCase.illegalActivities];
                data.handleStatus = handleStatus[data.handleStatus];
                var dataTemp = data["illegalCase"];
              //  new mini.Form("#illegalInfo").setData(data);
                for ( var key in data) {
                    if(!allData[key]){
                        allData[key] = data[key];
                    }
                }
                setFormData(allData);
               // new mini.Form("#punishDecision").setData(data);
                grid.setData(businessLoggerData);
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
        function setFormData(data) {
            $("[field]").each(function () {
                var el = $(this);
                var field = el.attr("field");
                if (field) {
                    try {
                        var value = eval("(function(){return function(d){return d." + field + ";}})()")(data);
                        value = value+"";
                        if (value) {
                            el.text(value);
                        } else {

                        }
                    } catch (e) {
                        console.error(e);
                    }
                }
            });
        }
        function decisionBook(id,userId, hphm, hpzl,code,tools) {
            tools.openWindow("admin/illegal/case/decision-book.html?id=" + id + "&hphm="+ hphm + "&userId="+ userId +"&hpzl=" + hpzl + "&code=" + code, "处罚决定书预览", "100%", "100%", function (e) {
                grid.reload();
            })
        }
        $(".decision-book-button").on("click", (function () {
            decisionBook(id,userId, hphm, hpzl,code,tools);
        }));
        $(".cancel-button").on("click", (function () {
            tools.closeWindow();
        }));
        $(".confirm-button").on("click", (function () {
            tools.closeWindow();
        }));
    });
});


