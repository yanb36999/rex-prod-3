importMiniui();
require(["request"], function (request) {
    $(".login-button").on("click", function () {
        var username = $("#username").val();
        if (!username) {
            $("#username").focus();
            return;
        }
        var password = $("#password").val();
        if (!password) {
            $("#password").focus();
            return;
        }
        request.post("authorize/login",
            {
                // token_type: "token",
                username: username,
                password: password
            }, function (e) {
                if (e.status == 200) {
                    window.location = "index.html";
                } else {
                    if(e.message == '{password_error}'){
                        e.message = '账号密码错误'
                    }else if(e.message == '{user_not_exists}'){
                        e.message = '账号不存在'
                    }
                    mini.alert(e.message);
                }
            }, false);
    });
});