//给上传的iframe用的
var uploadFileUrls = [];

importResource("/admin/css/common.css");
importMiniui(function () {
    mini.parse();
    require(["miniui-tools", "request"], function (tools, request) {
        window.tools = tools;
        var api = "exam-master";
        var func = request.post;
        var id = request.getParameter("id");
        if (id) {
            loadData(id);
            api += "/" + id;
            func = request.put;
        }

        //读取类型
        request.get("exam-type-master", function (resp) {
            if (resp.status === 200) {
                var types = [];
                $(resp.result.data).each(function () {
                    types.push({text: this.name, id: this.id});
                });
                mini.getbyName("type").setData(types);
            }
        });

        $(".save-button").on("click", (function () {
            var data = getDataAndValidate();
            if (!data) return;
            require(["message"], function (message) {
                var loading = message.loading("提交中");
                func(api, data, function (response) {
                    loading.close();
                    if (response.status == 200) {
                        message.showTips("保存成功");
                        if (!id) id = response.result;
                        tools.closeWindow();
                    } else {
                        message.showTips("保存失败:" + response.message, "danger");
                        if (response.result)
                            message.showFormErrors("#basic-info", response.result);
                    }
                })
            });
        }));

        uploadImage();
    });
});

function uploadImage() {

    $("#uploadImage").html("");
    var iframe = $("<iframe border='0' contentBorder='0'>");
    iframe.css({"width": "100%", "height": "100%", "border": "0px"});

    iframe.attr("src", BASE_PATH + "admin/file/examUpload/upload.html");
    $("#uploadImage").append(iframe);

    iframe.on("load", function (e) {
    });
}

window.renderAction = function (e) {
    var row = e.record;
    return [
        tools.createActionButton("删除", "icon-remove", function () {
            e.sender.removeRow(row);
        })
    ].join("");
};

window.rendererTrueFalse = function (e) {
    return e.value == 1 ? "是" : "否";
}


function loadData(id) {
    require(["request", "message"], function (request, message) {
        var loding = message.loading("加载中...");
        request.get("exam-master/" + id, function (response) {
            loding.close();
            if (response.status == 200) {
                new mini.Form("#basic-info").setData(response.result);
                //选项和答案
                for (var i = 0; i < response.result.examOptions.length; i++) {
                    response.result.examOptions[i].defaultCheck = 2;
                    for (var x = 0; x < response.result.answer.length; x++) {
                        if (response.result.examOptions[i].option == response.result.answer[x]) {
                            response.result.examOptions[i].defaultCheck = 1;
                        }
                    }
                }
                //图片
                for (var i = 0; i < response.result.fileUrl.length;i++) {
                    addImg(response.result.fileUrl[i]);
                }
                mini.get('option-grid').setData(response.result.examOptions);
            } else {
                message.showTips("加载数据失败", "danger");
            }
        });
    });
}

function addImg(data) {
    $("#imglist").append("<div class='img-item'><img src='"+ data +"'><span>X</span></div>");
    uploadFileUrls.push(data);
    delImgBtn();
}
//删除的方法
function delImgBtn(){

    $("#imglist .img-item span").off("click").on("click",function () {
        var i = $(this).parent().index();
        uploadFileUrls.splice(i,1);
        $(this).parent().remove();
    });
}

function getDataAndValidate() {
    var form = new mini.Form("#basic-info");
    form.validate();
    if (form.isValid() == false) {
        return;
    }
    var data = form.getData();
    //拼选项和答案
    data.examOptions = [];
    data.answer = [];
    data.fileUrl = [];

    $(mini.get('option-grid').getData()).each(function () {
        data.examOptions.push({
            option: this.option,
            content: this.content
        });
        if (this.defaultCheck == 1) {
            data.answer.push(this.option)
        }
        data.fileUrl = uploadFileUrls;
    });
    return data;
}