$(document).ready(function() {
    let projectHash = $('#projectHash').val();
    let currentUserId = Number($('#currentUserId').val());
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    console.log("project_hash: " + projectHash);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe(`/topic/projects/${projectHash}/discussion`, function(message) {
            const comment = JSON.parse(message.body);
            addCommentToList(comment);
        });
    });
    // Функция для добавления комментария в список
    function addCommentToList(comment) {
        const commentElement = $('<div class="comment"></div>')
            .text(comment.text)
            .append(` - ${comment.authorName}`)
            .append(` <span class="timestamp">${comment.createdAt}</span> <br>`);

        // Проверяем, является ли текущий пользователь автором комментария
        console.log('Current User ID:', currentUserId);
        console.log('Comment Author ID:', comment.authorId);

        if (comment.authorId === currentUserId) {
            commentElement.addClass('author-comment'); // Добавляем стиль для авторских комментариев
        }

        $('.commentsList').append(commentElement);
    }
    loadComments();

    //Загрузка комментариев
    function loadComments() {
        $.ajax({
            url: `/projects/${projectHash}/api/discussion/comments`,
            method: 'GET',
            success: function(comments) {
                $('.commentsList').empty(); // Очищаем предыдущие комментарии
                comments.forEach(function(comment) {
                    addCommentToList(comment);
                });
            },
            error: function() {
                console.log('Ошибка загрузки комментариев.');
            }
        });
    }
    //Отправка по сокету
    $(".addCommentButton").on("click", function() {
        const text = $('.newComment').val();
        if (text) {
            const comment = {
                text: text,
                projectHash: projectHash
            };
            console.log("Отправляемый JSON: ", JSON.stringify(comment));
            stompClient.send('/app/projects/' + projectHash + '/discussion', {},
                JSON.stringify(comment));
            //Очистка поля ввода
            $(this).closest(".commentsSection").find(".newComment").val("");
        } else {
            console.warn("Комментарий не может быть пустым!");
        }
    });

});
