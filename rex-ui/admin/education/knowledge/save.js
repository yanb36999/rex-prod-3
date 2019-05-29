importResource("/admin/css/common.css");

var editor;
importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message","ueditor.config.js","plugin/ueditor/ueditor.all.min"], function (request, tools, message) {
        var api = "knowledge-master";
        var func = request.post;
        var id = request.getParameter("id");
        if (id) {
            loadData(id);
            api += "/" + id;
            func = request.put;
        }

        require(["plugin/ueditor/lang/zh-cn/zh-cn"], function () {
            editor = UE.getEditor("container");
        });

        $(".save-button").on("click", (function () {
            var data = getDataAndValidate();
            if (!data)return;
            var loading = message.loading("提交中");
            func(api, data, function (response) {
                loading.close();
                if (response.status == 200 || response.status == 201) {
                    message.showTips("保存成功");
                    if (!id) id = response.result;
                    tools.closeWindow();
                } else {
                    message.showTips("保存失败:" + response.message, "danger");
                    if (response.result)
                        tools.showFormErrors("#basic-info", response.result);
                }
            })
        }));
    });
});
function loadData(id) {
    require(["miniui-tools", "request", "message"], function (tools, request, message) {
        var loading = message.loading("加载中...");
        request.get("knowledge-master/" + id, function (response) {
            console.log(response);
            loading.hide();
            if (response.status == 200) {
                new mini.Form("#basic-info").setData(response.result);
                editor.ready(function () {
                    editor.setContent(response.result.content);
                });
            } else {
                message.showTips("加载数据失败", "danger");
            }
        });
    });

}
function getDataAndValidate() {
    var form = new mini.Form("#basic-info");
    form.validate();
    if (form.isValid() == false) {
        return;
    }

    var data = form.getData();

    data.content = editor.getContent();

    console.log(data);
    return data;
}