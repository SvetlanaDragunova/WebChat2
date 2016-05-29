/**
 * Created by Svetlana on 30.05.2016.
 */
function auth() {
    $.ajax({
        method: "POST",
        url: "chat",
        dataType: 'json',
        data: { login: $('#authlog').val(), password: $('#authpass').val() , action: "auth"},
        success: function(data){
            // $("#footer-text").empty();
            // $.each(data, function(key, val) {
            //     $("<div>"+key+" "+val+"</div>").appendTo("#footer-text");
            // });
            if (data.auth == "success") {
                document.location.href = "chat.html";
            } else{
                $("authinfo").empty();
                $("<div>Ошибка при авторизации, проверьте логин/пароль</div>").appendTo("#authinfo");
            }
        },
        // error: function () {
        //     $("#footer-text").empty();
        //     $("<div>bad_ajax_request</div>").appendTo("#footer-text");
        // }
    });
}
function reg() {
    $.ajax({
        method: "POST",
        url: "chat",
        dataType: 'json',
        data: { login: $('#reglog').val(), password: $('#regpass').val() , action: "reg"},
        success: function(data){
            // $("#footer-text").empty();
            // $.each(data, function(key, val) {
            //     $("<div>"+key+" "+val+"</div>").appendTo("#footer-text");
            // });
            if (data.reg == "success") {
                document.location.href = "chat.html";
            } else{
                $("reginfo").empty();
                $("<div>Ошибка при регистрации, такой пользователь уже есть</div>").appendTo("#reginfo");
            }
        },
        // error: function () {
        //     $("#footer-text").empty();
        //     $("<div>bad_ajax_request</div>").appendTo("#footer-text");
        // }
    });
}

function userlist(){
    $.getJSON('chat?action=userlist', function(data) {
        $('#userlist').empty();
        $.each(data, function(key, val) {
            var ul = $('<ul style="padding-left: 5px"></ul>');
            $('<li>' + val + '</li>').appendTo(ul);
            ul.appendTo('#userlist');
        });
    });
}
function sendMessage() {
    $.ajax({
        method: "GET",
        url: "chat",
        dataType: 'json',
        data: { text: $('#message-input').val(), action: "sendmessage"},
        beforeSend: function () {
            $('#message-input').empty();
        }
    });
}
function messagelist(){
    $.getJSON('chat?action=messagelist', function(data) {
        $('#chat').empty();
        $.each(data, function(key, val) {
            var mas = val.split("@");
            var ul = $('<ul style="padding-left: 5px"></ul>');
            $('<li><i>' + mas[0]+'</i> '+ mas[1] + '</li>').appendTo(ul);
            ul.appendTo('#chat');
        });
    });
}
function isauth() {
    $.getJSON('chat?action=isauth', function(data) {
        $("#userinfo").empty();
        if (data.auth == "yes") {
            $('<div>' + "Приветствую, " + data.name + '</div>').appendTo("#userinfo");
        }
        if (data.auth == "no") {
            $('<div>' + "Вы не авторизованы" + '</div>').appendTo("#userinfo");
            $('#chat').empty();
        }
    });
}
function updateChat() {
    userlist();
    messagelist();
}