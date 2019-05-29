importResource("/admin/css/common.css");

importMiniui(function () {
    require(["miniui-tools"], function (tools) {
        mini.parse();

        var grid = window.grid = mini.get("grid1");
        tools.initGrid(grid);
        //动态设置URL
        grid.setUrl(API_BASE_PATH + "video-master");
        function search() {
            tools.searchGrid("#search-box", grid);
        }
        search();

        tools.bindOnEnter("#search-box", search);


        window.GetData = function() {
            var row = grid.getSelected();
            return row;
        };

        window.onRowDblClick = function(e) {
            onOk();
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
