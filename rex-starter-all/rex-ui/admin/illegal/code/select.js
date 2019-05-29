importResource("/admin/css/common.css");

importMiniui(function () {
    require(["miniui-tools"], function (tools) {
        mini.parse();
        window.tools = tools;
        var grid = window.grid = mini.get("data-grid");
        tools.initGrid(grid);
        grid.setUrl(API_BASE_PATH + "illegal/code");
        grid.getColumn("defaultMoney").renderer = function (e) {
            var r = e.record;


            return r.defaultMoney + " ( " + r.minMoney + "-" + r.maxMoney + ")";
        }
        function search() {
            var keyword = mini.getbyName("keyword").getValue();
            var param = {};
            if (keyword && keyword.length > 0) {
                require(["request"], function (request) {
                    param = request.createQuery().where()
                        .like("id", "%" + keyword + "%")
                        .or().like("contentAbbreviate", "%" + keyword + "%")
                        .or().like("illegalContent", "%" + keyword + "%")
                        .or().like("illegalRegulations", "%" + keyword + "%")
                        .or().like("punishBasis", "%" + keyword + "%")
                        .getParams();
                    grid.load(param);
                });
            } else {
                grid.load(param);
            }
        }

        $(".search-button").on("click", search);
        mini.getbyName("keyword").on("enter", search);
    });
});
window.renderAction = function (e) {
    var html = [];
    var row = e.record;

    html.push(tools.createActionButton("选择", "icon-ok", function () {
        tools.closeWindow(row);
    }));
    return html.join("");
}