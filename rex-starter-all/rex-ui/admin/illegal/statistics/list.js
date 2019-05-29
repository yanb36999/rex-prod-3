importResource("/admin/css/common.css");

//var handleSignMap = {"1":"已申请","-1":"申请失败","-2":"系统异常","3":"已签收","4":"已缴费","5":"已完成处罚"};
var handleSignMap = {"1":"status1","-1":"status2","-2":"status3","3":"status4","4":"status5","5":"status6"};
var testData = [{"date":"20171129","hs":-4,"count":151},{"date":"20171129","hs":-1,"count":2504},{"date":"20171129","hs":0,"count":1286},{"date":"20171129","hs":3,"count":1987},{"date":"20171129","hs":4,"count":17725},{"date":"20171129","hs":5,"count":3535},{"date":"20171130","hs":-4,"count":5},{"date":"20171130","hs":-1,"count":79},{"date":"20171130","hs":0,"count":59},{"date":"20171130","hs":1,"count":1},{"date":"20171130","hs":3,"count":197},{"date":"20171130","hs":4,"count":642},{"date":"20171130","hs":5,"count":5},{"date":"20171201","hs":-4,"count":10},{"date":"20171201","hs":-1,"count":103},{"date":"20171201","hs":0,"count":119},{"date":"20171201","hs":3,"count":199},{"date":"20171201","hs":4,"count":680},{"date":"20171201","hs":5,"count":18},{"date":"20171202","hs":-4,"count":7},{"date":"20171202","hs":-1,"count":70},{"date":"20171202","hs":0,"count":72},{"date":"20171202","hs":3,"count":194},{"date":"20171202","hs":4,"count":605},{"date":"20171202","hs":5,"count":5},{"date":"20171203","hs":-4,"count":6},{"date":"20171203","hs":-1,"count":24},{"date":"20171203","hs":0,"count":54},{"date":"20171203","hs":3,"count":92},{"date":"20171203","hs":4,"count":315},{"date":"20171203","hs":5,"count":2},{"date":"20171204","hs":-4,"count":11},{"date":"20171204","hs":-1,"count":73},{"date":"20171204","hs":0,"count":100},{"date":"20171204","hs":3,"count":233},{"date":"20171204","hs":4,"count":550},{"date":"20171204","hs":5,"count":3},{"date":"20171205","hs":-4,"count":6},{"date":"20171205","hs":-1,"count":89},{"date":"20171205","hs":0,"count":82},{"date":"20171205","hs":3,"count":295},{"date":"20171205","hs":4,"count":540},{"date":"20171205","hs":5,"count":10},{"date":"20171206","hs":-4,"count":7},{"date":"20171206","hs":-1,"count":61},{"date":"20171206","hs":0,"count":23},{"date":"20171206","hs":1,"count":2},{"date":"20171206","hs":3,"count":419},{"date":"20171206","hs":4,"count":451},{"date":"20171206","hs":5,"count":9}]
importMiniui(function () {
    mini.parse();
    require(["miniui-tools","request","message"], function (tools,request,message) {

        var results = testData;
        var data = [];
        if(results && results.length > 0){
            var firstTag = results[0][data];
            var tempCount = 0;
            var tempDate = {};
            for(var i = 0; i < results.length; i++){
                var tempTag = results[i][data];
                tempDate[handleSignMap[results[i].hs]] = results[i].count;
                tempDate["status"] = results[i].hs;
                tempCount += results[i].count;
                console.log(results[i].hs);
                console.log(handleSignMap[results[i].hs]);
                if(firstTag != tempTag){
                    tempDate["total"] = tempCount;
                    tempDate["dateTime"] = results[i-1][data];
                    data.push({"date":results[i].date});
                    firstTag = tempTag;
                    tempCount = 0;
                    tempDate = {};
                }
            }
        }
        var grid = window.grid = mini.get("datagrid");
        grid.setData(data);
        /*request.get("",function (response) {
            var results = response.result;
            var data = [];
            if(results && results.length > 0){
                var firstTag = results[0][data];
                var tempCount = 0;
                var tempDate = {};
                for(var i = 0; i < results.length; i++){
                    var tempTag = results[i][data];
                    tempDate[handleSignMap[results[i].hs]] = results[i].count;
                    tempDate["status"] = results[i].hs;
                    tempCount += results[i].count;
                    if(firstTag != tempTag){
                        tempDate["total"] = tempCount;
                        tempDate["dateTime"] = results[i-1][data];
                        data.push({"date":results[i].date});
                        firstTag = tempTag;
                        tempCount = 0;
                        tempDate = {};
                    }
                }
            }
        });*/
    });
});

