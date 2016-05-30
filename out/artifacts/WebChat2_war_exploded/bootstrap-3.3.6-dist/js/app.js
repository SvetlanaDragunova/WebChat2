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
            if (data.auth == "success") {
                document.location.href = "chat.html";
            } else{
                $("authinfo").empty();
                $("<div>Ошибка при авторизации, проверьте логин/пароль</div>").appendTo("#authinfo");
            }
        }
    });
}

function reg() {
    $.ajax({
        method: "POST",
        url: "chat",
        dataType: 'json',
        data: { login: $('#reglog').val(), password: $('#regpass').val() , action: "reg"},
        success: function(data){
            if (data.reg == "success") {
                document.location.href = "chat.html";
            } else{
                $("reginfo").empty();
                $("<div>Ошибка при регистрации, такой пользователь уже есть</div>").appendTo("#reginfo");
            }
        }
    });
}

function userlist(){
    $.getJSON('chat?action=userlist', function(data) {
        $('#userlist').empty();
        $.each(data, function(key, val) {
            var ul = $('<ul class="list-unstyled" style="padding-left: 5px"></ul>');
            $('<li>' + val + '</li>').appendTo(ul);
            ul.appendTo('#userlist');
        });
    });
}
function send() {
    $.ajax({
        method: "GET",
        url: "chat",
        dataType: 'json',
        data: { message: $('#message').val(), action: "send"},
    });
}
function messagelist(){
    $.getJSON('chat?action=messagelist', function(data) {
        $('#chat').empty();
        $.each(data, function(key, val) {
            var mas = val.split("@");
            var ul = $('<ul class="list-unstyled" style="padding-left: 5px"></ul>');
            $('<li><i>' + mas[0]+':</i> '+ mas[1] + '</li>').appendTo(ul);
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
            $('<div>' + "Вы не авторизованы, перейдите по <a href='index.html'>ссылке для авторизации</a>" + '</div>').appendTo("#userinfo");
            $('#chat').empty();
        }
    });
}
function updateChat() {
    userlist();
    messagelist();
}

function logoff() {
    $.getJSON('chat?action=logoff', function(data) {

        if (data.res == "yes") {
            document.location.href = "index.html";
        }
    });
    
}