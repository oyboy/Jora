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
                // Измените элемент ссылки на изображение, если MIME тип изображения
                const extensions = ['.jpg', '.png', '.jpeg', '.bmp'];
                const filePreview = extensions.some(extension => file.fileName.endsWith(extension));

                if (filePreview) {
                    const img = $('<img />')
                        .attr('src', file.downloadUrl)
                        .attr('alt', file.fileName)
                        .css({ 'max-width': '200px', 'max-height': '200px', 'cursor': 'pointer' });
                    img.on('click', function() {
                        $('#modalImage').attr('src', file.downloadUrl); // Устанавливаем src для модального изображения
                        $('#imageModal').show(); // Показываем модальное окно
                    });
                    filesContainer.append(img);
                } else {
                    const downloadLink = $('<a></a>')
                        .attr('href', file.downloadUrl)
                        .attr('download', file.fileName)
                        .text(file.fileName);
                    fileElement.append(downloadLink);
                }
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
    // Закрытие модального окна при клике на иконку закрытия
    $('#closeModal').on('click', function() {
        $('#imageModal').hide();
    });
    $('#imageModal').on('click', function(event) {
        if(event.target !== this) return; // Закрываем только при клике на фон
        $(this).hide();
    });
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
                beforeSend: function() {
                    // Показываем прогресс-бар перед отправкой
                    $('#uploadProgress').show();
                    $('#progressBar').css('width', '0%'); // Сбрасываем ширину прогрессбара
                },
                xhr: function() {
                    const xhr = new window.XMLHttpRequest();
                    // Обрабатываем событие прогресса
                    xhr.upload.addEventListener("progress", function(evt) {
                        if (evt.lengthComputable) {
                            const percentComplete = evt.loaded / evt.total * 100;
                            $('#progressBar').css('width', percentComplete + '%'); // Обновляем ширину прогрессбара
                        }
                    }, false);
                    return xhr; // Возвращаем XMLHttpRequest
                },
                success: function(fileIds) {
                    $('#uploadProgress').hide(); // Скрываем прогресс-бар после завершения
                    sendCommentWithFiles(text, fileIds);
                },
                error: function (error) {
                    $('#uploadProgress').hide(); // Скрываем прогресс-бар при ошибке
                    alert('Ошибка загрузки файлов. Максимальный размер не должен превышать 10МБ');
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
        const xhr = new XMLHttpRequest();
        xhr.open('GET', downloadUrl, true);
        xhr.responseType = 'blob'; // Указываем, что ожидаем бинарный ответ

        // Показываем прогресс-бар перед отправкой
        $('#uploadProgress').show();
        $('#progressBar').css('width', '0%'); // Сбрасываем ширину прогрессбара

        // Обрабатываем событие прогресса
        xhr.onprogress = function(event) {
            if (event.lengthComputable) {
                const percentComplete = (event.loaded / event.total) * 100;
                $('#progressBar').css('width', percentComplete + '%'); // Обновляем ширину прогрессбара
            }
        };

        xhr.onload = function() {
            if (xhr.status === 200) {
                const blob = xhr.response; // Получаем файл в виде Blob
                const downloadLink = document.createElement('a');
                downloadLink.href = URL.createObjectURL(blob);
                downloadLink.download = fileName;
                downloadLink.click(); // Инициируем скачивание
            } else {
                console.error('Ошибка при скачивании файла:', xhr.statusText);
            }
            $('#uploadProgress').hide(); // Скрываем прогресс-бар после завершения
        };

        xhr.onerror = function() {
            console.error('Ошибка при загрузке файла');
            $('#uploadProgress').hide(); // Скрываем прогресс-бар при ошибке
        };

        xhr.send(); // Отправляем запрос
    });
});