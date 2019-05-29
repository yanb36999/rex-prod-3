importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message"], function (request, tools, message) {
        var api = "wechat-msg/again";
        var func = request.post;

        $(".save-button").on("click", (function () {
            var data = getDataAndValidate();
            if (!data)return;
            var loading = message.loading("提交中");
            func(api, data, function (response) {
                loading.close();
                if (response.status == 200 || response.status == 201) {
                    message.showTips("保存成功");
                    if (!id) id = response.result;
                } else {
                    message.showTips("保存失败:" + response.message, "danger");
                    if (response.result)
                        tools.showFormErrors("#basic-info", response.result);
                }
            })
        }));
    });
});
function getDataAndValidate() {
    var form = new mini.Form("#basic-info");
    form.validate();
    if (form.isValid() == false) {
        return;
    }
    return form.getData();
}