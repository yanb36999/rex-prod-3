importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message"], function (request, tools, message) {
        window.tools = tools;
        var api = "learn-type-master";
        var func = request.post;
        var id = request.getParameter("id");
        if (id) {
            loadData(id);
            func = request.patch;
        }

        $(".save-button").on("click", (function () {
            var data = getDataAndValidate();
            if (!data)return;
            if(id){
                data.id = id;
            }
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


        //课件
        $("#select-course").on("click",function () {
            selectCourseware();
        });

        //    课件
        function selectCourseware() {
            mini.open({
                url: BASE_PATH + "admin/education/learn-type/courseware-list.html",
                title: "选择课件",
                width: 1000,
                height: 500,
                ondestroy: function (action) {
                    if (action == "ok") {
                        var iframe = this.getIFrameEl();
                        var data = iframe.contentWindow.GetSelecteds();
                        data = mini.clone(data);
                        for(var i =0;i<data.length;i++){
                            addCourseware(data[i]);
                        }
                    }
                }
            });
        }

        //天数dom操作
        //添加课件
        function addCourseware(data) {
            if($("#"+data.id).length == 0){
                var courseitem = $("<div class='courseware-item' id='"+ data.id +"'><div><span style='margin-right: 20px;'>"+ data.code +"</span>"+ data.name +"</div></div>");
                var coursebtn = $("<span class='delete-btn'>X</span>").on("click",function () {
                    $(this).parent().remove();
                });
                courseitem.append(coursebtn);
                $("#courselist").append(courseitem);
            }
        }

        function loadData(id) {
            require(["miniui-tools", "request", "message"], function (tools, request, message) {
                var loading = message.loading("加载中...");
                request.get("learn-type-master/" + id, function (response) {
                    loading.hide();
                    if (response.status == 200) {

                        new mini.Form("#basic-info").setData(response.result);

                        $(".mintime").val(response.result.contentMaster.minTime);
                        $(".mintime").attr('id',response.result.contentMaster.id);

                        for(var i = 0;i<response.result.contentMaster.coursewareMasterList.length;i++){
                            addCourseware(response.result.contentMaster.coursewareMasterList[i]);
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

            data.contentMaster = {};

            $("#courselist").each(function () {
                var idList = [];
                $(this).find(".courseware-item").each(function () {
                    idList.push(this.id);
                });
                data.contentMaster.courseIdList = idList;
            });
            data.contentMaster.minTime = $(".mintime").val();
            data.contentMaster.id = $(".mintime").attr('id');

            return data;
        }


    });
});

