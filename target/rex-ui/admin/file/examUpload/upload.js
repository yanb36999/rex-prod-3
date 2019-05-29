require(["css!plugin/webuploader/webuploader.css", "css!/admin/css/common.css"]);

importMiniui(function () {
    require(["plugin/webuploader/webuploader.min", "message", "request", "miniui-tools"], function (WebUploader, message, request, tools) {
        if (!WebUploader.Uploader.support()) {
            mini.alert("您的浏览器太旧不支持文件上传!<br/>" +
                "如果你使用的是IE浏览器,请<a href='https://www.baidu.com/s?wd=ie%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9B%B4%E6%96%B0flash%20player' target='_blank'>升级flash版本</a>!");
            throw new Error("");
        }
        mini.parse();

        var $list = $('#thelist'),
            $fileName = $('#thelist .item .file-name'),
            $fileState = $('#thelist .item .state'),
            $btn = $('#ctlBtn'),
            state = 'pending',
            uploader;

        uploader = WebUploader.create({

            // 不压缩image
            resize: false,

            // swf文件路径
            swf: BASE_PATH + 'plugins/webuploader/Uploader.swf',

            // 文件接收服务端。
            server: "http://file.rex.cdjg.gov.cn:8090/file/upload-static",

            // 选择文件的按钮。可选。
            // 内部根据当前运行是创建，可能是input元素，也可能是flash.
            pick: {
                id:'#picker',
                multiple: false
            },
            auto: true
        });

        // 当有文件添加进来的时候
        uploader.on('fileQueued', function (file) {
            $fileName.text(file.name);
            $fileState.text("等待上传...");
            // $list.append('<div id="' + file.id + '" class="item">' +
            //     '<span style="margin-right: 10px;">' + file.name + '</span>' +
            //     '<span class="state">等待上传...</span>' +
            //     '</div>');
        });

        // 文件上传过程中创建进度条实时显示。
        uploader.on('uploadProgress', function (file, percentage) {
            $fileState.text("上传中" + (percentage * 100).toFixed(2) + '%');
        });

        uploader.on('uploadSuccess', function (file,resultData) {
            console.log(resultData);
            // $('#' + file.id).find('.state').text('已上传');
            $fileState.text("已上传");
            if (resultData && resultData.status == 200 && resultData.result) {
                parent.addImg(resultData.result);
            }
        });

        uploader.on('uploadError', function (file) {
            $fileState.text('上传出错');
        });

        // uploader.on('uploadComplete', function (file) {
        //     $('#' + file.id).find('.progress').fadeOut();
        // });

        uploader.on('all', function (type) {
            if (type === 'startUpload') {
                state = 'uploading';
            } else if (type === 'stopUpload') {
                state = 'paused';
            } else if (type === 'uploadFinished') {
                state = 'done';
            }

            if (state === 'uploading') {
                $btn.text('暂停上传');
            } else {
                $btn.text('开始上传');
            }
        });

        $btn.on('click', function () {
            if (state === 'uploading') {
                uploader.stop();
            } else {
                uploader.upload();
            }
        });

    });
});