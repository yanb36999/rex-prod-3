importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["miniui-tools"], function (tools) {
        window.tools = tools;

        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);
        grid.setUrl(API_BASE_PATH + "knowledge-master");
        function search() {
            tools.searchGrid("#search-box", grid);
        }

        $(".search-button").click(search);
        tools.bindOnEnter("#search-box", search);
        $(".add-button").click(function () {
            tools.openWindow("admin/education/knowledge/save.html", "添加知识点", "1000", "80%", function (e) {
                grid.reload();
            })
        });
        search();
    });
});

function edit(id) {
    tools.openWindow("admin/education/knowledge/save.html?id=" + id, "编辑知识点", "1000", "80%", function (e) {
        grid.reload();
    })
}
window.renderAction = function (e) {
    var row = e.record;
    var html = [
        tools.createActionButton("编辑", "icon-edit", function () {
            edit(row.id);
        })
    ];
    html.push(
        tools.createActionButton("删除", "icon-remove", function () {
            if (row._state == "added") {
                e.sender.removeNode(row);
            } else {
                require(["request", "message"], function (request, message) {
                    message.confirm("确定删除该知识点?", function () {
                        var loading = message.loading("删除中...");
                        request["delete"]("knowledge-master/" + row.id, {}, function (res) {
                            loading.close();
                            if (res.status == 200) {
                                e.sender.removeNode(row);
                            } else {
                                message.showTips("删除失败:" + res.message);
                            }
                        })
                    });
                })
            }
        })
    )
    return html.join("");
}
