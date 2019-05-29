//给上传的iframe用的
var uploadFileUrls = "";

importResource("/admin/css/common.css");
importMiniui(function () {
    mini.parse();
    require(["miniui-tools", "request","./../../file/upload-single-file"], function (tools, request,uploadFile) {
        window.tools = tools;
        var api = "video-master";
        var func = request.post;
        var id = request.getParameter("id");
        if (id) {
            loadData(id);
            api += "/" + id;
            func = request.put;
        }

        $(".save-button").on("click", (function () {
            var data = getDataAndValidate();
            if (!data)return;
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

        uploadfile();

    });

});

//视频播放
function initVideoPlayer(video, call) {

    uploadFileUrls = video;

    $(".illegalVideoPlayer").html("");
    var iframe = $("<iframe border='0' contentBorder='0'>");
    iframe.css({"width": "100%", "height": "100%", "border": "0px"});

    iframe.attr("src", BASE_PATH + "admin/video/index.html");
    $(".illegalVideoPlayer").append(iframe);

    iframe.on("load", function () {
        var win = this.contentWindow;
        if (win) {
            window.videoWindow = win;
            win.init(video, null, call);
        }
        // console.log(win);
    });
}

function uploadfile() {

    $("#uploadFile").html("");
    var iframe = $("<iframe border='0' contentBorder='0'>");
    iframe.css({"width": "100%", "height": "100%", "border": "0px"});

    iframe.attr("src", BASE_PATH + "admin/file/upload.html");
    $("#uploadFile").append(iframe);

    iframe.on("load", function (e) {
    });
}

function loadData(id) {
    require(["request", "message"], function (request, message) {
        var loding = message.loading("加载中...");
        request.get("video-master/" + id, function (response) {
            loding.close();
            if (response.status == 200) {
                console.log(response);
                new mini.Form("#basic-info").setData(response.result);

                initVideoPlayer(response.result.path);
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
    //拼选项和答案
    data.path = uploadFileUrls;
    console.log(data);
    return data;
}