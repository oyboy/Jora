$(document).ready(function() {
    let projectHash = $('#projectHash').val();
    let currentUserId = Number($('#currentUserId').val());
    const csrfToken = $('input[name="_csrf"]').val();
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
            .append(` <span class="timestamp">${comment.createdAt}</span><br>`);

        console.log("Attacms: " + comment.fileAttachmentDTOS);
        // Проверяем, если у комментария есть файлы
        if (comment.fileAttachmentDTOS && comment.fileAttachmentDTOS.length > 0) {
            const filesContainer = $('<div class="attachments"></div>');

            comment.fileAttachmentDTOS.forEach(file => {
                const fileElement = $('<div class="file-attachment"></div>');
                const downloadLink = $('<a></a>')
                    .attr('href', file.downloadUrl)
                    .attr('download', file.fileName)
                    .text(file.fileName);

                fileElement.append(downloadLink);
                filesContainer.append(fileElement);
            });

            commentElement.append(filesContainer);
        }

        if (comment.authorId === currentUserId) {
            commentElement.removeClass('comment');
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
    $(".addCommentButton").on("click", function(event) {
        event.preventDefault();

        const text = $('.newComment').val();
        const files = $('#fileUpload')[0].files;
        const projectHash = $('#projectHash').val();

        if (!text && files.length === 0) {
            console.warn("Комментарий и файлы не могут быть пустыми!");
            return;
        }
        // Если файлы выбраны, сначала отправим их через POST-запрос
        if (files.length > 0) {
            let formData = new FormData();
            Array.from(files).forEach(file => {
                formData.append("files", file);
            });
            formData.append("projectHash", projectHash);

            $.ajax({
                url: `/projects/${projectHash}/api/discussion/upload-files`,
                method: 'POST',
                data: formData,
                headers: {
                    'X-CSRF-Token': csrfToken
                },
                processData: false,
                contentType: false,
                success: function(fileIds) {
                    sendCommentWithFiles(text, fileIds);
                },
                error: function(error) {
                    console.error("Ошибка загрузки файлов:", error);
                }
            });
        } else {
            sendCommentWithFiles(text, []);
        }
    });
    function sendCommentWithFiles(text, fileIds) {
        const commentData = {
            text: text,
            projectHash: $('#projectHash').val(),
            attachmentIds: fileIds
        };
        stompClient.send(`/app/projects/${commentData.projectHash}/discussion`, {}, JSON.stringify(commentData));

        // Очистка полей после отправки
        $('.newComment').val("");
        $('#fileUpload').val("");
    }

    $('.commentsList').on('click', '.file-attachment a', function(event) {
        event.preventDefault(); // Предотвращаем переход по ссылке

        const downloadUrl = $(this).attr('href'); // Получаем ссылку на скачивание
        const fileName = $(this).attr('download'); // Получаем имя файла

        // Отправляем GET-запрос на сервер для скачивания
        fetch(downloadUrl)
            .then(response => {
                if (response.ok) {
                    return response.blob(); // Получаем файл в виде Blob
                } else {
                    throw new Error('Файл не найден на сервере');
                }
            })
            .then(blob => {
                // Создаем ссылку для скачивания
                const downloadLink = document.createElement('a');
                downloadLink.href = URL.createObjectURL(blob);
                downloadLink.download = fileName;
                downloadLink.click(); // Инициируем скачивание
            })
            .catch(error => {
                console.error('Ошибка при скачивании файла:', error);
            });
    });
});
