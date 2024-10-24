$(document).ready(function() {
    let stompClient = null;
    const projectHash = $("#projectHash").val();
    const currentUsername = $('#currentUsername').val();

    $(".showCommentsButton").on("click", function() {
        const taskId = $(this).data("task-id");
        console.log("Task ID: " + taskId);
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            // Подписка на сообщения
            stompClient.subscribe('/topic/projects/' + projectHash + '/tasks/' + taskId + "/comment", function (response) {

                const comment = JSON.parse(response.body);
                const commentsList = $(`.commentsSection[data-task-id="${taskId}"] .commentsList`);
                commentsList.append(createCommentElement(comment));
            }.bind(this));

            loadComments(taskId); // Загрузка комментариев при открытии
        });

        const commentsSection = $(this).siblings(".commentsSection");
        commentsSection.toggle();
    });
    //Отправка на сервер
    $(".addCommentButton").on("click", function() {
        const text = $(this).closest(".commentsSection").find(".newComment").val();
        const taskId = $(this).data("task-id");

        if (text) {
            const comment = {
                taskId: Number(taskId),
                text: text
            };
            console.log("Отправляемый JSON: ", JSON.stringify(comment));
            stompClient.send('/app/projects/' + projectHash + '/tasks/' + taskId + '/comment', {},
                JSON.stringify(comment));
            //Очистка поля ввода
            $(this).closest(".commentsSection").find(".newComment").val("");
        } else {
            console.warn("Комментарий не может быть пустым!");
        }
    });
    //Загрузка комментариев из базы
    function loadComments(taskId) {
        $.ajax({
            type: "GET",
            url: `/projects/${projectHash}/tasks/${taskId}/api/comments`,
            success: function(comments) {
                const commentsList = $(`.commentsSection[data-task-id="${taskId}"] .commentsList`);
                commentsList.empty(); // Очищаем текущий список комментариев

                comments.forEach(comment => {
                    commentsList.append(createCommentElement(comment));
                });
            },
            error: function(err) {
                console.error("Error while loading comments:", err);
            }
        });
    }
    //Отображение комментария (добавление в ui)
    //Также изменённый стиль для автора
    function createCommentElement(comment) {
        const commentElement = $("<div>");
        const formattedText = `${comment.text} (от ${comment.username} в ${new Date(comment.createdAt).toLocaleString()})`;
        if (comment.username === currentUsername) {
            commentElement.css("font-weight", "bold"); // Жирный шрифт для автора
        }
        commentElement.text(formattedText);
        return commentElement;
    }
});
