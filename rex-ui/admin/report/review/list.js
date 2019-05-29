importResource("/admin/css/common.css");

importMiniui(function () {
    require(["request", "miniui-tools"], function (request, tools) {
        var platType = [];

        mini.parse();
        request.get("dict/all/plate-type", function (resp) {
            if (resp.status === 200) {
                for (var value in resp.result) {
                    platType.push({text: resp.result[value], s: value + " " + resp.result[value], id: value});
                }
                mini.getbyName("illegalCar.plateType").setData(platType);
            }
        });
        var orgMapper = {};
        request.get("organizational?paging=false", function (resp) {
            if (resp.status === 200) {
                var org = [];
                $(resp.result.data).each(function () {
                    orgMapper[this.code] = this;
                    org.push({text: this.name, id: this.code, s: this.code + " " + this.name});
                });
                mini.getbyName("illegalCar.signDept").setData(org);
            }
        });


        var grid = window.grid = mini.get("datagrid");
        grid.getColumn("signDept").renderer = function (e) {
            return orgMapper[e.value] ? orgMapper[e.value].name : e.value;
        };
        tools.initGrid(grid);

       var sort= tools.multiSort(grid, function (sortParam) {
           search();
        });

        grid.setUrl(request.basePath + "illegal/report/new");
        window.renderCwtStatus = function (e) {
            if (e.value == 0) {
                return "车网通数据";
            } else if (e.value == 5) {
                return "<sapn style='color: red'>网上申请</sapn>";
            }
        };
        var columns = ["reportInfo.reportId", "illegalCar.plateType"];
        $(grid.getColumns()).each(function () {
            if (this.field) {
                columns.push(this.field);
            }
        });

        sort.addSort("illegalCar.cwtStatus","desc");
        sort.addSort("illegalCar.createTime","desc");
        // grid.getColumn("requestTime").renderer=function (e) {
        //     return mini.formatDate(new Date(e.value),'yyyy-MM-dd HH:mm:ss')
        // };
        function search() {
            var param = {};
            var sortParam=sort.getSortParam();
            for (var f in sortParam) {
                param[f] = sortParam[f];
            }
            tools.searchGrid("#search-box", grid, param);
        }

        $(".search-button").click(search);
        tools.bindOnEnter("#search-box", search);

        search();

        function edit(row) {
            tools.openWindow("admin/report/review/detail.html?id=" + (row.reportInfo.reportId)
                + "&plateType=" + (row.illegalCar.plateType) + "&plateNumber=" + (row.illegalCar.plateNo)
                , "举报信息复核", "100%", "100%", function () {
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
