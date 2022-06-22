// $(function(){
//     $(".feach-btn").click(feach);
// });
//
// function feach() {
//     var btn = this;
//     var count = 60;
//     const countDown = setInterval(() => {
//         if(count===60){
//         $.post(
//             CONTEXT_PATH + "/user/sendCode?p="+Math.random(),
//             {"email":$("#email").val()},
//             function(data) {
//                 data = $.parseJSON(data);
//                 if(data.code != 0) {
//                     alert(data.msg);
//                 }
//             }
//         );
//         }
//         if (count === 0) {
//         $(btn).text('重新发送').removeAttr('disabled');
//         $(btn).css({
//             background: '#ff9400',
//             color: '#fff',
//         });
//         clearInterval(countDown);
//     } else {
//         $(btn).attr('disabled', true);
//         $(btn).css({
//             background: '#d8d8d8',
//             color: '#707070',
//         });
//         $(btn).text(count + '秒后可重新获取');
//     }
//     count--;
// }, 1000);
// }

$(function(){
    $("#verifyCodeBtn").click(getVerifyCode);
});

function getVerifyCode() {
    var email = $("#your-email").val();

    if(!email) {
        alert("请先填写您的邮箱！");
        return false;
    }

    $.get(
        CONTEXT_PATH + "/forget/code",
        {"email":email},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                alert("验证码已发送至您的邮箱,请登录邮箱查看!");
            } else if(data.code == 3){
                alert(data.msg);
            }
            else {
                alert(data.msg);
            }

        }
    );
}
