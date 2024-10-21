$(document).ready(function() {
    const userId = $("#currentUserId").val();
    const csrfToken = $('input[name="_csrf"]').val();
    console.log("id: " + userId);

    // Получить уведомления и обновить интерфейс
    function fetchNotifications() {
        $.get(`/api/notifications/unread/${userId}`, function(data) {
            if (data.length > 0) {
                $('#notification-count').text(data.length).show();
                data.forEach(notification => {
                    $('#notification-list').append(`
                        <li data-id="${notification.id}">
                            <strong>${notification.title}</strong><br>${notification.message}
                        </li>
                    `);
                });
            } else {
                $('#notification-count').hide();
            }
        });
    }//Очистка и обновления списка
    setInterval(function() {
        $('#notification-list').empty();
    }, 3000);
    setInterval(fetchNotifications, 3000);
    // Обработчик клика на колокольчик
    $('#notification-bell').click(function() {
        $('#notification-dropdown').toggle();
    });

    // Обработчик клика на уведомление для пометки как прочитанное
    $(document).on('click', '#notification-list li', function() {
        const notificationId = $(this).data('id');
        $.ajax({
            url: `/api/notifications/read/${notificationId}`,
            type: 'POST',
            headers: {
                'X-CSRF-Token': csrfToken // Добавление CSRF-токена в заголовок
            },
            success: function() {
                $(this).remove(); // Удалить уведомление из списка
                // Если больше нет уведомлений, скрыть колокольчик
                if ($('#notification-list li').length === 0) {
                    $('#notification-count').hide();
                }
            }.bind(this),
            error: function(xhr, status, error) {
                console.error("Ошибка при пометке уведомления как прочитанное:", error);
            }
        });
    });
});
