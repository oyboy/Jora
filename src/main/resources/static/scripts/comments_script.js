$(document).ready(function() {
    let stompClient = null;
    const projectHash = $("#projectHash").val();
    const currentUserId = Number($('#currentUserId').val());
    const csrfToken = $('input[name="_csrf"]').val();

    //Проверка на непрочитанные комментарии
    $('.task').each(function() {
        const taskId = $(this).data('task-id'); // Получите ID задачи из элемента
        checkForUnreadComments(taskId).then(unreadCount => {
            if (unreadCount > 0) {
                $(this).find(".showCommentsButton").addClass("new-comment"); // Добавьте класс для выделения.
            } else {
                $(this).find(".showCommentsButton").removeClass("new-comment");
            }
        });
    });
    let subscriptions = {};
    $(".showCommentsButton").on("click", function() {
        const taskId = $(this).data("task-id");
        console.log("Task ID: " + taskId);
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        if (!subscriptions[taskId]) {
            stompClient.connect({}, function (frame) {
                console.log('Connected: ' + frame);
                subscriptions[taskId] = stompClient.subscribe('/topic/projects/' + projectHash + '/tasks/' + taskId + "/comment", function (response) {
                    const comment = JSON.parse(response.body);
                    const commentsList = $(`.commentsSection[data-task-id="${taskId}"] .commentsList`);
                    commentsList.append(createCommentElement(comment, taskId));
                });
                loadComments(taskId); // Загрузка комментариев при открытии
            });
        }

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
            url: `/api/v1/projects/${projectHash}/tasks/${taskId}/comments`,
            success: function(comments) {
                const commentsList = $(`.commentsSection[data-task-id="${taskId}"] .commentsList`);
                commentsList.empty(); // Очищаем текущий список комментариев

                comments.forEach(comment => {
                    commentsList.append(createCommentElement(comment, taskId));
                });
            },
            error: function(err) {
                console.error("Error while loading comments:", err);
            }
        });
    }
    //Отображение комментария (добавление в ui)
    //Также изменённый стиль для автора
    function createCommentElement(comment, taskId) {
        const commentElement = $("<div>")
        const formattedText = `${comment.text} (от ${comment.username} в ${new Date(comment.createdAt).toLocaleString()})`;
        if (comment.userId === currentUserId) {
            commentElement.css("font-weight", "bold"); // Жирный шрифт для автора
        }
        commentElement.text(formattedText);

        // Обработчик клика по комментарию
        commentElement.on('click', function() {
            //Только автор может увидеть тех, кто прочитал его коммент
            if (comment.userId === currentUserId){
                getReaders(comment.commentId, taskId);
            }
        });
        // Функция показа пользователей, прочитавших комментарий
        function getReaders(commentId, taskId) {
            $.ajax({
                url: `/api/v1/projects/${projectHash}/tasks/${taskId}/comments/${commentId}/readers`,
                method: 'GET',
                success: function(readByUsers) {
                    showReaders(readByUsers);
                },
                error: function(err) {
                    console.error("Error fetching read users:", err);
                }
            });
        }
        function showReaders(readByUsers) {
            const readerListDiv = document.getElementById('readerList');
            readerListDiv.innerHTML = ''; // Очистка предыдущих данных
            // Формируем список пользователей
            readByUsers.forEach(reader => {
                if (reader.userId !== currentUserId) { // Проверяем, является ли пользователь автором
                    const readerItem = document.createElement('div');
                    readerItem.textContent = `${reader.username} (${reader.email}) at ${reader.readAt}`;
                    readerListDiv.appendChild(readerItem);
                }
            });
            //Имеет смысл показывать окно в том случае, если действительно есть такие пользователи
            if (readerListDiv.childNodes.length > 0) {
                // Открыть модальное окно с помощью Bootstrap
                const readerModal = new bootstrap.Modal(document.getElementById('readerModal'));
                readerModal.show();
            }
        }
        function closeModal() {
            const readerModal = bootstrap.Modal.getInstance(document.getElementById('readerModal'));
            if (readerModal) {
                readerModal.hide();
            }
        }

        // Подключение Intersection Observer для отслеживания
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    // Комментарий попал в область видимости
                    checkForUnreadComments(taskId).then(unreadCount => {
                        console.log("Unread comments count:", unreadCount);
                        if (unreadCount > 0) {
                            markCommentAsRead(comment.commentId, currentUserId, taskId);
                            console.log("Marked comment as read.");
                        }
                    }).catch(err => {
                        console.error("Error checking unread comments:", err);
                    });
                    observer.unobserve(entry.target); // Прекратить следить за этим элементом
                }
            });
        });
        observer.observe(commentElement[0]);

        return commentElement;
    }
    function markCommentAsRead(commentId, userId, taskId) {
        console.log("comment, user, task: " + commentId + " / " + userId + " / " + taskId);

        var requestBody = {
            userId: userId,
            commentId: commentId,
            taskId: taskId
        };
        console.log("Формируемый json: " + JSON.stringify(requestBody));

        fetch(`/api/v1/projects/${projectHash}/tasks/${taskId}/comments/read`, {
            method: 'POST',
            headers: {
                'X-CSRF-Token': csrfToken,
                'Content-Type': 'application/json' // Exclude 415 error (content media type)
            },
            body: JSON.stringify(requestBody)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                console.log(`Комментарий ID ${commentId} помечен как прочитанный.`);
                checkForUnreadComments(taskId); //После прочтения нужно убедиться, что ещё остались непрочитанные сообщения
            })
            .catch(err => {
                console.error("Ошибка при обновлении статуса комментариев:", err);
            });
    }
    function checkForUnreadComments(taskId) {
        return $.ajax({
            type: "GET",
            url: `/api/v1/projects/${projectHash}/tasks/${taskId}/comments/unreadCount`,
        }).then(unreadCount => {
            console.log("Found " + unreadCount + " unread comments in task " + taskId);
            const button = $(`.showCommentsButton[data-task-id="${taskId}"]`);

            if (unreadCount > 0) {
                button.removeClass("btn btn-outline-info");
                button.addClass("btn btn-info"); // Добавить класс для выделения
            } else {
                button.removeClass("btn btn-info");
                button.addClass("btn btn-outline-info");
            }
            return unreadCount;
        }).catch(err => {
            console.error("Error fetching unread count for task " + taskId + ":", err);
            return 0;
        });
    }
});