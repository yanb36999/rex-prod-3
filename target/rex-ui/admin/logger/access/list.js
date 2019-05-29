importResource("/admin/css/common.css");

importMiniui(function () {
    require(["request", "miniui-tools"], function (request, tools) {
        mini.parse();
        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);

        grid.setUrl(request.basePath + "logger/access");

        var columns =["id"];
        $( grid.getColumns()).each(function () {
            if(this.field){
                columns.push(this.field);
            }
        });


        grid.getColumn("requestTime").renderer=function (e) {
            return mini.formatDate(new Date(e.value),'yyyy-MM-dd HH:mm:ss')
        }
        function search() {
            tools.searchGrid("#search-box", grid,{includes:columns.join(",")});
        }

        $(".search-button").click(search);
        tools.bindOnEnter("#search-box", search);

        search();
        
        function searchBusinessLogger(id) {
            tools.openWindow("admin/logger/business/list.html?requestId="+id,"业务日志","80%","80%",function () {

            });
        }

        window.renderAction = function (e) {
            var row = e.record;

            var html = [
                tools.createActionButton("查看业务日志", "icon-find", function () {
                    searchBusinessLogger(row.id);
                })
            ];
            return html.join("");
        }

    });
});
