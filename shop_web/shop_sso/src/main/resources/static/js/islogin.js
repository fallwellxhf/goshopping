
$(function () {
    $.ajax({
        url:"http://localhost:8084/sso/islogin",
        dataType: "jsonp",
        jsonpCallback:"islogin",
        success:function (data) {
            if (data) {
                //已经登入
                $("#pid").html(JSON.parse(data).nickname+"您好，欢迎来到<b><a>ShopCZ商城</a>[<a href='http://localhost:8084/sso/logout'>注销</a>]</b>");
            } else {
                //未登入
                $("#pid").html("[<a href='javascript:login();'>登入</a>][<a href='http://localhost:8084/sso/toReginster'>注册</a>]");
            }
        }
    });
});

function login() {
    // alert("地址为："+location.href);
    var  returnUrl=location.href;
    //进行关键字编码url
    returnUrl = encodeURI(returnUrl);
    location.href="http://localhost:8084/sso/toLogin?returnUrl="+returnUrl;

}
