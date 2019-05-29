importResource("/admin/css/common.css");
var sendStatus = [
    {id: "", text: "所有状态"},
    {id: "1", text: "推送成功"},
    {id: "0", text: "推送失败"}
];
importMiniui(function () {
    mini.parse();
    require(["miniui-tools"], function (tools) {
        window.tools = tools;
        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);
        grid.setUrl(API_BASE_PATH + "wechat/send");
        function search() {
            tools.searchGrid("#search-box", grid);
        }
        $(".search-button").click(search);
        tools.bindOnEnter("#search-box", search);
        search();
        mini.getbyName("sendStatus$EQ").on('valuechanged', function (e) {
            var handleSign = mini.getbyName("sendStatus$EQ").getValue();
            search();
        });
    });
});

window.renderStatus = function (e) {
    return e.value == 1 ? "推送成功" : "推送失败";
}

