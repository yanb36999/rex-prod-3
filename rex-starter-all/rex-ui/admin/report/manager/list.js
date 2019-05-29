importResource("/admin/css/common.css");
var statusMapping ={
    "1101":"复核通过",
    "1102":"复核不通过",
    "2101":"已通知被举报人",
    "2102": "已处罚录入,微信",//所有违法处理已完成
    "2103":"已处罚录入,现场",
    "2104":"已处理，综合办理",
    "3102":"处理未完毕",//部分违法处理完成
    "3103":"已处罚"
};//群众线下处理返回到外网

importMiniui(function () {
    require(["request", "miniui-tools"], function (request, tools) {
        var platType = [];

        mini.parse();

        // request.get("cars/illegal/handle-status",function (resp) {
        //     if(resp.status===200){
        //             $(resp.result).each(function () {
        //                 statusMapping[this.code+""]=this.text;
        //             });
        //     }
        // });
        request.get("dict/all/plate-type", function (resp) {
            if (resp.status === 200) {
                for (var value in resp.result) {
                    platType.push({text: resp.result[value],s:value+" "+ resp.result[value], id: value});
                }
                mini.getbyName("illegalCar.plateType").setData(platType);
            }
        });
        var orgMapper ={};
        request.get("organizational?paging=false", function (resp) {
            if (resp.status === 200) {
                var org = [];
                $(resp.result.data).each(function () {
                    orgMapper[this.code]=this;
                    org.push({text: this.name, id: this.code,s:this.code+" "+this.name});
                });
                mini.getbyName("illegalCar.signDept").setData(org);
            }
        });


        var grid = window.grid = mini.get("datagrid");

        grid.getColumn("signDept").renderer=function (e) {
            return orgMapper[e.value]?orgMapper[e.value].name:e.value;
        };
        tools.initGrid(grid);

        grid.setUrl(request.basePath + "illegal/report/checked");

        var columns =["reportInfo.reportId","illegalCar.plateType"];
        $( grid.getColumns()).each(function () {
            if(this.field){
                columns.push(this.field);
            }
        });

        grid.getColumn("signReviewStatus").renderer=function (e) {
             return statusMapping[e.value]?statusMapping[e.value]:"未知";
        };

        // grid.getColumn("requestTime").renderer=function (e) {
        //     return mini.formatDate(new Date(e.value),'yyyy-MM-dd HH:mm:ss')
        // };
        function search() {
            var defaultParam = {};
            require(["request"], function (request) {
                var param = new mini.Form("#search-box").getData(true, false);
                if (defaultParam) {
                    for (var field in defaultParam) {
                        param[field] = defaultParam[field];
                    }
                }
                var plateNumber =param["illegalCar.plateNo"];

                //if(plateNumber&&plateNumber.length>6){
                //    param["illegalCar.plateNo"]= plateNumber.substring(1,plateNumber.length);
                //}
                  param = request.encodeQueryParam(param);
                grid.load(param);
            });
           // tools.searchGrid("#search-box", grid,{includes:columns.join(",")});
        }

        $(".search-button").click(search);
        tools.bindOnEnter("#search-box", search);

        search();
        function edit(row) {
            tools.openWindow("admin/report/manager/detail.html?id="+(row.reportInfo.reportId)
                +"&plateType="+(row.illegalCar.plateType)+"&plateNumber="+(row.illegalCar.plateNo)
                ,"举报信息详情","95%","95%",function () {
                grid.reload();
            });
        }
        window.renderAction = function (e) {
            var row = e.record;

            var html = [
                tools.createActionButton("复核", "icon-edit", function () {
                    edit(row);
                })
            ];
            return html.join("");
        }

    });
});
