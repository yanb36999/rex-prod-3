importResource("/admin/css/common.css");
var errorMsg = {};
errorMsg.put = function (key, value) {
    errorMsg[key] = value;
};
errorMsg.put("40972", "API查询的订单不存在");
errorMsg.put("40973", "API查询过程中系统异常");
errorMsg.put("40976", "API查询系统异常");
errorMsg.put("40977", "商户证书信息错");
errorMsg.put("40978", "解包商户请求数据报错");
errorMsg.put("40979", "查询的订单不存在");
errorMsg.put("40980", "API查询过程中系统异常");
errorMsg.put("40981", "给商户打包返回数据错");
errorMsg.put("40982", "系统错误");
errorMsg.put("40983", "查询的订单不唯一");
errorMsg.put("40987", "请求数据中接口名错误");
errorMsg.put("40947", "商户代码或者商城账号有误");
errorMsg.put("40948", "商城状态非法");
errorMsg.put("40949", "商城类别非法");
errorMsg.put("40950", "商城应用类别非法");
errorMsg.put("40951", "商户证书id状态非法");
errorMsg.put("40952", "商户证书id未绑定");
errorMsg.put("40953", "商户id权限非法");
errorMsg.put("40954", "检查商户状态时数据库异常");
errorMsg.put("42022", "业务类型上送有误");
errorMsg.put("42023", "商城种类上送有误");
errorMsg.put("42020", "ID未开通汇总记账清单功能");
errorMsg.put("42021", "汇总记账明细清单功能已到期");
errorMsg.put("40990", "商户证书格式错误");
errorMsg.put("41160", "商户未开通外卡支付业务");
errorMsg.put("41161", "商户id对商城账号没有退货权限");
errorMsg.put("41177", "外卡的当日退货必须为全额退货");
errorMsg.put("26012", "找不到记录");
errorMsg.put("26002", "数据库操作异常");
errorMsg.put("26034", "退货交易重复提交");
errorMsg.put("26036", "更新支付表记录失败");
errorMsg.put("26042", "退货对应的支付订单未清算，不能退货");

var payStatusData = [
    {id: "", text: "所有状态"},
    {id: "0", text: "待支付"},
    {id: "1", text: "支付成功"},
    {id: "2", text: "支付失败"},
    {id: "11", text: "支付重复"},
    {id: "-1", text: "支付无效"}
];
var callbackStatusData = [
    {id: "", text: "所有状态"},
    {id: "1", text: "回调成功"},
    {id: "0", text: "未回调"},
    {id: "-1", text: "回调失败"}
];
var channelIdData = [
    {id: "", text: "所有渠道"},
    {id: "icbc", text: "icbc"},
    {id: "unionpay", text: "unionpay"}
];
var payStatusMap = {"0": "待支付", "1": "支付成功", "2": "支付失败", "11": "支付重复", "-1": "支付无效"};
importMiniui(function () {
    mini.parse();
    require(["miniui-tools", "request", "message"], function (tools, request, message) {
        window.tools = tools;
        var grid = window.grid = mini.get("datagrid");
        grid.load();
        tools.initGrid(grid);
        grid.setUrl(API_BASE_PATH + "pay-detail");
        window.renderPayStatus = function (e) {
            var dataMap = {};
            for (var i = 0; i < payStatusData.length; i++) {
                dataMap[payStatusData[i].id] = payStatusData[i].text;
            }
            if (e.value == 11) {
                return "<span style='color: red'>" + dataMap[e.value] + "</span>";
            }
            return dataMap[e.value];
        }
        window.renderCallbackStatusStatus = function (e) {
            var resultText = "";
            if (e.value == 1) {
                resultText = "回调成功";
            } else if (e.value == -1) {
                resultText = "回调失败";
            } else if (e.value == 0) {
                resultText = "未回调";
            }
            return resultText;
        }
        $(".search-button").on("click", function () {
            search();
        });
        $(".refresh-button").on("click", function () {
            grid.reload();
        });

        function search() {
            tools.searchGrid("#search-box", grid);
        }

        tools.bindOnEnter("#search-box", search);
    });
});

function showAtPos(obj) {
    var win = mini.get("win1");
    setFormData(obj);
    win.showAtPos("center", "middle");
}

function setFormData(data) {
    require(["jquery", "request"], function ($, request) {
        $(".callbackData").text(data.callbackData);
        if (data.channelId == "icbc") {
            $(".tranStat-tr").show();
            var paramData = {"paySerialId": data.paySerialId, "createTime": (data.createTime).Format("yyyyMMdd")};
            request.get("pay-detail/icbc-pay-info", paramData, function (response) {
                if (response.status == 200) {
                    var tranStat = (response.result.tranStat == 1) ? "支付成功" : "支付失败";
                    $(".tranStat-span").text(tranStat);
                } else {
                    var msg = response.message;
                    if (errorMsg[msg]) {
                        $(".tranStat-span").text(errorMsg[msg]);
                    } else {
                        $(".tranStat-span").text(msg ? msg : response);
                    }
                }
            });

        } else {
            $(".tranStat-tr").hide();
        }
        $(".callbackData").text(data.callbackData);
        $("[field]").each(function () {
            var el = $(this);
            var field = el.attr("field");
            if (field) {
                try {
                    var value = eval("(function(){return function(d){return d." + field + ";}})()")(data);
                    if (field == "callbackStatus") {
                        if (value == 1) {
                            value = "回调成功";
                        } else if (value == -1) {
                            value = "回调失败";
                        } else if (value == 0) {
                            value = "未回调";
                        }
                    }
                    if (field == "payStatus") {
                        value = payStatusMap[value];
                    }
                    if ((field == "payReturnTime" || field == "createTime") && (value != undefined && value != null && value != "")) {
                        value = value.Format("yyyy-MM-dd hh:mm:ss");
                    }
                    if (value == null) {
                        value = "";
                    }
                    value = value + "";
                    el.text(value);
                } catch (e) {
                    console.error(e);
                }
            }
        });
    })
}

Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
window.renderAction = function (e) {
    var row = e.record;
    var html = [
        tools.createActionButton("查看详情", "icon-find", function () {
            showAtPos(row);
        })
    ];
    return html.join("");
}

