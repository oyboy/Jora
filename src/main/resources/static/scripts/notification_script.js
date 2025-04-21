$(document).ready(function() {
    const userId = $("#currentUserId").val();
    const csrfToken = $('input[name="_csrf"]').val();
    console.log("id: " + userId);
    let isDropdownOpen = false;
    // Получить уведомления и обновить интерфейс
    function fetchNotifications() {
        if (isDropdownOpen) {
            return;
        }
        $.get(`/api/v1/notifications/unread/${userId}`, function(data) {
            if (data.length > 0) {
                $('#notification-count').text(data.length).show();
                $('#notification-list').empty(); // Очистить перед добавлением новых уведомлений
                data.forEach(notification => {
                    const link = notification.link || window.location.href;
                    $('#notification-list').append(`
                        <li class="dropdown-item border-bottom py-2" data-id="${notification.id}">
                            <a href="${link}" class="text-decoration-none text-dark">
                                <strong>${notification.title}</strong><br>
                                <span class="small">${notification.message}</span>
                            </a>
                        </li>
                    `);
                });
            } else {
                $('#notification-count').hide();
            }
        });
    }//Очистка и обновления списка
/*    setInterval(function() {
        $('#notification-list').empty();
    }, 3000);*/
    fetchNotifications();
    setInterval(fetchNotifications, 3000);
    // Обработчик клика на колокольчик
    $('#notification-bell').click(function() {
        $('#notification-list').empty();
        fetchNotifications();
        isDropdownOpen = !isDropdownOpen;
        $('#notification-dropdown').toggle();
    });

    // Обработчик клика на уведомление для пометки как прочитанное
    $(document).on('click', '#notification-list li', function() {
        const notificationId = $(this).data('id');
        console.log("Отправляю запрос на пометку уведомления ID:", notificationId);
        $.ajax({
            url: `/api/v1/notifications/read/${notificationId}`,
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
                console.error(status);
                console.error(error);
                console.error("Ошибка при пометке уведомления как прочитанное:", xhr.statusText, xhr.responseText);
            }
        });
    });
    // Закрытие выпадающего меню при клике вне его
    $(document).click(function(event) {
        const target = $(event.target);
        if (!target.closest('#notification-bell').length && !target.closest('#notification-dropdown').length) {
            isDropdownOpen = false;
            $('#notification-dropdown').hide();
        }
    });
});
