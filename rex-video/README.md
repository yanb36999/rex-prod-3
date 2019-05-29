# 文件上传，视频转换服务
1. 文件上传接口

        POST /file/upload-static
参数名为: file

上传成功返回结果
```json
{
    "result": "http://localhost:8898/upload/20171125/310555565964658.mov",
    "status": 200,
    "timestamp": 1511587522307
}
```
其中result就是文件访问地址

2. 视频转码

        GET /video/convert/m3u8?location=http://localhost:8898/upload/20171125/310555565964658.mov
      
location 为视频访问地址
转码成功后返回:
```json
{
    "result": "http://localhost:8898/video/20171125/311297476652771.m3u8",
    "status": 200,
    "timestamp": 1511588282007
}
```
result为转码后的访问地址