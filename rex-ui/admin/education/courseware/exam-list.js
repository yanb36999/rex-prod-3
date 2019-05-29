importResource("/admin/css/common.css");

importMiniui(function () {
    require(["miniui-tools"], function (tools) {
        mini.parse();

        var grid = window.grid = mini.get("grid1");
        tools.initGrid(grid);
        //动态设置URL
        grid.setUrl(API_BASE_PATH + "exam-master");
        function search() {
            tools.searchGrid("#search-box", grid);
        }
        search();

        tools.bindOnEnter("#search-box", search);


        window.GetSelecteds = function() {
            var rows = grid.getSelecteds();
            return rows;
        };
        window.GetData = function() {
            var rows = grid.getSelecteds();
            var ids = [], texts = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var row = rows[i];
                ids.push(row.id);
                texts.push(row.name);
            }
            var data = {};
            data.id = ids.join(",");
            data.text = texts.join(",");
            return data;
        };

        //////////////////////////////////
        function CloseWindow(action) {
            if (window.CloseOwnerWindow) return window.CloseOwnerWindow(action);
            else window.close();
        }

        function onOk() {
            CloseWindow("ok");
        }
        function onCancel() {
            CloseWindow("cancel");
        }


        $("#button-search").on("click",search);
        $("#button-ok").on("click",onOk);
        $("#button-cancel").on("click",onCancel);
    });

});
