importResource("/admin/css/common.css");

importMiniui(function () {
    require(["request", "miniui-tools"], function (request, tools) {
        mini.parse();
        var requestId = request.getParameter("requestId");

        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);

        grid.setUrl(request.basePath + "logger/business");

        var columns =["id"];
        $( grid.getColumns()).each(function () {
            if(this.field){
                columns.push(this.field);
            }
        });

        grid.getColumn("createTime").renderer=function (e) {
            return mini.formatDate(new Date(e.value),'yyyy-MM-dd HH:mm:ss')
        };

        grid.getColumn("message").renderer=function (e) {
            var val = e.value;
            if(val.length>200){
                val = val.substring(0,200)+"...";
            }
            var real = e.value;
            var func  ="showLog_"+(Math.round(Math.random()*1000000))
            window[func]=function () {
                var win = window.open("about:blank");
                var textarea = $("<textarea>")
                    .val(real);
                textarea.css({width: window.innerWidth, height: window.innerHeight});
                $(win.document.body).append(textarea);
            };

            var html = $("<a href='javascript:(0)' onclick='window."+func+"()'>").text(val);


            return html[0].outerHTML;

        };
        grid.getColumn("level").renderer=function (e) {

            return $("<span>").addClass(e.value).text(e.value)[0].outerHTML;
        };

        function search() {
            tools.searchGrid("#search-box", grid,{requestId:requestId,includes:columns.join(",")});
        }

        $(".search-button").click(search);
        tools.bindOnEnter("#search-box", search);

        search();

        window.renderAction = function (e) {
            var row = e.record;

            var html = [
                tools.createActionButton("查看详情", "icon-find", function () {
                    edit(row.id);
                })
            ];
            return html.join("");
        }

    });
});
