importResource("/admin/css/common.css");
var videoSelect = [],
    knowledgeSelect = [],
    examSelect = [],
    uploadFileUrls;

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message"], function (request, tools, message) {
        var api = "courseware-master";
        var func = request.post;
        var id = request.getParameter("id");
        if (id) {
            loadData(id);
            api += "/" + id;
            func = request.put;
        }

        //读取类型
        request.get("course-type", function (resp) {
            if (resp.status === 200) {
                var types = [];
                $(resp.result.data).each(function () {
                    types.push({text: this.name, id: this.id});
                });
                mini.getbyName("courseType.id").setData(types);
            }
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

    //    添加视频
        $("#select-video").on("click",function () {
            mini.open({
                url: BASE_PATH + "admin/education/courseware/video-list.html",
                title: "选择视频",
                width: 1000,
                height: 500,
                ondestroy: function (action) {
                    if (action == "ok") {
                        var iframe = this.getIFrameEl();
                        var data = iframe.contentWindow.GetData();
                        data = mini.clone(data);
                        initVideoPlayer(data.path);
                        $("#video-name").text(data.name);
                        videoSelect[0] = data.id;
                    }
                }
            });
        });

    //    添加知识点
        $("#select-knowledge").on("click",function () {
            mini.open({
                url: BASE_PATH + "admin/education/courseware/knowledge-list.html",
                title: "选择知识点",
                width: 1000,
                height: 500,
                ondestroy: function (action) {
                    if (action == "ok") {
                        var iframe = this.getIFrameEl();
                        var data = iframe.contentWindow.GetSelecteds();
                        data = mini.clone(data);
                        for(var i =0;i<data.length;i++){
                            addKnowledge(data[i]);
                        }
                    }
                }
            });
        });
    //    添加考试题
        $("#select-exam").on("click",function () {
            mini.open({
                url: BASE_PATH + "admin/education/courseware/exam-list.html",
                title: "选择考试题",
                width: 1000,
                height: 500,
                ondestroy: function (action) {
                    if (action == "ok") {
                        var iframe = this.getIFrameEl();
                        var data = iframe.contentWindow.GetSelecteds();
                        data = mini.clone(data);
                        for(var i =0;i<data.length;i++){
                            addExam(data[i]);
                        }
                    }
                }
            });
        });

        uploadImage();
    });
});

//上传图片
function uploadImage() {

    $("#uploadImage").html("");
    var iframe = $("<iframe border='0' contentBorder='0'>");
    iframe.css({"width": "100%", "height": "100%", "border": "0px"});

    iframe.attr("src", BASE_PATH + "admin/file/examUpload/upload.html");
    $("#uploadImage").append(iframe);

    iframe.on("load", function (e) {
    });
}

function addImg(data) {
    $("#imageShow").empty().append("<img src='"+ data +"' style='height: 200px;display: block;margin: 0 auto;'>");
    uploadFileUrls = data;
}

//知识点dom操作
//增加
function addKnowledge(data) {
    var dataIndex = knowledgeSelect.indexOf(data.id);
    if(dataIndex == -1 ){
        var item = $("<div class='knowledge-item' id='"+ data.id +"'><div>"+ data.title +"</div></div>");
        var btn = $("<span>X</span>").on("click",function () {
            removeKnowledge(data.id);
        });
        item.append(btn);
        $("#knowledge-list").append(item);
        knowledgeSelect.push(data.id);
    }
}
//删除
function removeKnowledge(item) {
    $("#"+item).remove();
    var i = knowledgeSelect.indexOf(item);
    if(i == -1) {
        console.log("数据错误");
    }else {
        knowledgeSelect.splice(i,1);
    }
}

//考试题dom操作
//增加
function addExam(data) {
    //TODO 考试题返回值需要一个类型名称
    var dataIndex = examSelect.indexOf(data.id);
    if(dataIndex == -1 ){
        var item = $("<div class='exam-item' id='"+ data.id +"'></div>");
        var topic =$("<div class='exam-title'>（"+ data.answer.toString() +"） "+ data.topic +"</div>")
        var btn = $("<span class='exam-remove'>X</span>").on("click",function () {
            removeExam(data.id);
        });
        var options = $("<div class='exam-options'></div>");
        for(var i = 0;i<data.examOptions.length;i++){
            options.append("<div class='exam-option'><span style='margin-right:10px;'>"+ data.examOptions[i].option +"</span>"+ data.examOptions[i].content +"</div>");
        }

        item.append(btn,topic,options);
        $("#exam-list").append(item);
        examSelect.push(data.id);
    }
}
//删除
function removeExam(item) {
    $("#"+item).remove();
    var i = examSelect.indexOf(item);
    if(i == -1) {
        console.log("数据错误");
    }else {
        examSelect.splice(i,1);
    }
}

//视频播放
function initVideoPlayer(video, call) {

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
    });
}

function loadData(id) {
    require(["miniui-tools", "request", "message"], function (tools, request, message) {
        var loading = message.loading("加载中...");
        request.get("courseware-master/" + id, function (response) {
            loading.hide();
            if (response.status == 200) {
                console.log(response.result);
                new mini.Form("#basic-info").setData(response.result);
            //   封面
                addImg(response.result.picture);

            //    视频只有一个
                for(var i=0;i<response.result.videoMasterList.length;i++){
                    $("#video-name").text(response.result.videoMasterList[i].name);
                    initVideoPlayer(response.result.videoMasterList[i].path);
                    videoSelect[0] = response.result.videoIdList[0];
                }
                //知识点
                for(var i=0;i<response.result.knowledgeMasterList.length;i++){
                    addKnowledge(response.result.knowledgeMasterList[i]);
                }
                //考试题
                for(var i=0;i<response.result.examMasterList.length;i++){
                    addExam(response.result.examMasterList[i]);
                }


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
    data.videoIdList = videoSelect;
    data.examIdList = examSelect;
    data.examUnitCount = examSelect.length;
    data.knowledgeIdList = knowledgeSelect;
    data.picture = uploadFileUrls;

    data.courseType.name = mini.getbyName("courseType.id").getText();

    console.log(data);
    return data;
}