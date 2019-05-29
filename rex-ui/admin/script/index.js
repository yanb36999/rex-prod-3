importResource("/admin/css/common.css");
importMiniui(function () {
        mini.parse();
        var scriptEditor;
        var jobTree = mini.get("leftTree");
        require(["script-editor", "message"], function (editorBuilder, message) {
            var loading = message.loading("加载脚本编辑器...");
            editorBuilder.createEditor("script-editor", function (editor) {
                scriptEditor = editor;
                editor.init("javascript", "");
                loading.hide();
                loadJob();
            });
        });

        /* 加载任务数据 */

        function loadJob() {
            require(["request", "message"], function (request, message) {
                var loading = message.loading("加载任务中...");
                request.get("script", {noPaging: true}, function (response) {
                    var treeData = [];
                    var treeDataMap = {};
                    $(treeData).each(function () {
                        treeDataMap[this.id] = this;
                    });

                    function add2tree(config, type) {
                        if (!type) type = "默认";
                        if (treeDataMap[type]) {
                            var children = treeDataMap[type].children;
                            if (!children) treeDataMap[type].children = children = [];
                            children.push(config);
                        } else {
                            var newType = {name: type, id: type, __nodeType: "type"};
                            treeData.push(newType);
                            treeDataMap[type] = newType;
                            add2tree(config, type);
                        }
                    }

                    loading.hide();
                    if (response.status === 200) {
                        var configList = response.result.data;
                        $(configList).each(function () {
                            if (this.type && this.type !== "") {
                                add2tree(this, this.type);
                            } else {
                                add2tree(this, "默认");
                            }
                        });
                        mini.get("leftTree").setData(treeData);
                    }

                });
            });
        }

        /*初始化事件*/
        var selected;
        jobTree.on("nodeselect", function (e) {
            if (e.node.__nodeType === 'type') {
                return;
            }
            selected = e.node;
            $(".enable-button,.disable-button").hide();
            if (selected.status === 1) {
                $(".disable-button").show();
            } else {
                $(".enable-button").show();
            }
            initSelectedJob();
        });

        function initSelectedJob() {
            new mini.Form("#basic-info").setData(selected);
            mini.getbyName("language").setValue(selected.language);
            scriptEditor.setScript(selected.script);
            mini.getbyName("language").doValueChanged();

        }

        mini.getbyName("language").on("valuechanged", function (e) {
            var lang = e.value;
            if (lang) {
                scriptEditor.init(lang, scriptEditor.getScript());
            }
        });

        $(".execute-button").on("click", function () {
            mini.get("execute-window").show();
        });
        $(".do-execute-button").on("click", function () {
            if (selected) {

                require(["request", "message"], function (request, message) {
                    var param = {};
                    $(mini.get("parameter-grid").getData()).each(function () {
                        param[this.parameter]=this.value;
                    });
                    var loading = message.loading("执行任务中...");
                    request.post("script/" + selected.id + "/execute", param, function (response) {
                        loading.hide();
                        if (response.status === 200) {
                            if (typeof response.result === 'object') {
                                response.result = JSON.stringify(response.result);
                            }
                            $(".console").text(response.result);
                        } else {
                            $(".console").text(response.message);
                        }
                    });
                });
            }
        });
        $(".add-button").on("click", function () {
            var data = {type: "默认", name: "新建脚本", language: 'javascript'};
            if (selected && selected.__nodeType === 'type') {
                data.type = selected.id;
                jobTree.addNode(data, 'add', selected);
            } else {
                var type = jobTree.getParentNode(selected);
                if (type)
                    data.type = type.id;
                jobTree.addNode(data, 'after', selected);
            }
            jobTree.selectNode(data);
        });

        $(".save-button").on("click", function () {
            var data = new mini.Form("#basic-info").getData();
            data.language = mini.getbyName("language").getValue();
            data.script = scriptEditor.getScript();
            require(["request", "message"], function (request, message) {
                var loading = message.loading("提交中..");
                if (data.id === "") delete data.id;
                request.patch("script", data, function (response) {
                    loading.hide();
                    if (response.status === 200) {
                        data.id = response.result;
                        for (var f in data) {
                            selected[f] = data[f];
                        }
                        jobTree.updateNode(selected);
                        jobTree.selectNode(selected);
                        message.showTips("保存成功");
                    } else {
                        message.showTips("保存失败:" + response.message, "danger");
                    }
                });
            });
        });
    }
);