// 百度地图API功能
var map = new BMap.Map("allmap", {minZoom: 8, maxZoom: 20});
var styleJson = [
    {
        "featureType": "highway",
        "elementType": "all",
        "stylers": {
            "weight": "1",
            "visibility": "on"
        }
    }
];
map.setMapStyle({
    styleJson: styleJson
});

var stCtrl = new BMap.PanoramaControl(); //构造全景控件
stCtrl.setOffset(new BMap.Size(20, 20));
map.addControl(stCtrl);//添加全景控件
map.centerAndZoom('', 8);
map.enableScrollWheelZoom();
var bdary = new BMap.Boundary();
var mapType1 = new BMap.MapTypeControl({mapTypes: [BMAP_NORMAL_MAP,BMAP_HYBRID_MAP]});
var mapType2 = new BMap.MapTypeControl({anchor: BMAP_ANCHOR_TOP_LEFT});
// map.addControl(mapType1);          //2D图，卫星图
map.addControl(mapType2);

window.reInit = function () {
    map.clearOverlays();
    showParentArea(false);
}

function areaMapRename(name) {
    return name;
}

window.showAreaMarker = function (areaName, markData, autoZoom) {


    bdary.get(areaMapRename(areaName), function (rs) {       //获取行政区域
        var count = rs.boundaries.length; //行政区域的点有多少个
        if (count === 0) {
            alert("未能获取到对应的行政区域数据,请联系管理员!");
            initMarker(markData);
            return;
        }
        var pointArray = [];
        for (var i = 0; i < count; i++) {
            var ply = new BMap.Polygon(rs.boundaries[i],
                {fillColor: "", strokeWeight: 3, strokeColor: "rgb(0, 53, 133)"}); //建立多边形覆盖物
            map.addOverlay(ply);  //添加覆盖物
            pointArray = pointArray.concat(ply.getPath());
        }
         if (autoZoom)
             map.setViewport(pointArray);    //调整视野
        initMarker(markData);
    });
}

function initMarker(data_info) {
    for (var i = 0; i < data_info.length; i++) {
        var index = 11;
        var myIcon = new BMap.Icon("http://api.map.baidu.com/img/markers.png", new BMap.Size(23, 25), {
            offset: new BMap.Size(10, 25),
            imageOffset: new BMap.Size(0, 0 - index * 25)
        });
        var point = new BMap.Point(data_info[i][0], data_info[i][1]);
        map.centerAndZoom(point, 15);
        var marker = new BMap.Marker(point, {icon: myIcon});  // 创建标注
        var content = data_info[i][2];
        map.addOverlay(marker);               // 将标注添加到地图中
        if(content){
        addClickHandler(content, marker, point);
        }
        var lb = data_info[i][3];
        if (lb) {
            var opts = {
                position: point,    // 指定文本标注所在的地理位置
                offset: new BMap.Size(-(lb.length * 12 / 2), -35)    //设置文本偏移量
            };
            var label = new BMap.Label(lb, opts);  // 创建文本标注对象
            label.setStyle({
                backgroundColor: 'rgb(1, 150, 221)',
                borderColor: 'rgb(0, 53, 133)',
                color: "white",
                fontSize: "12px",
                height: "18px",
                cursor: "pointer",
                lineHeight: "18px",
                "-mozBorderRadius": "5px",
                borderRadius: "5px"
            });
            map.addOverlay(label);
        }
    }

    function addClickHandler(content, marker, point) {
        marker.addEventListener("click", function (e) {
                openInfo(content, e, point)
            }
        );
    }

    function openInfo(content, e, point) {
        var opts = {
            width: 300,     // 信息窗口宽度
            height: 400,     // 信息窗口高度
            title: "车辆信息", // 信息窗口标题
            enableMessage: true//设置允许信息窗发送短息
        };
        var infoWindow = new BMap.InfoWindow(content, opts);  // 创建信息窗口对象
        map.openInfoWindow(infoWindow, point); //开启信息窗口
    }
}