$(document).ready(function() {
    const csrfToken = $('input[name="_csrf"]').val();
    //Загрузка проектов для пользователя и фильтров
    loadProjects();
    $("#taskFilter, #projectFilter").change(function () {
        //Загрузка при применении фильтров
        loadTasks()
    });
    function loadProjects() {
        $.ajax({
            url: '/home/api/calendar/projects',
            method: 'GET',
            success: function(projects) {
                const projectFilter = $('#projectFilter');
                projectFilter.empty(); // Очищаем старые данные
                projectFilter.append('<option value="">Все проекты</option>');

                projects.forEach(project => {
                    let projectId = Object.keys(project)[0];
                    let projectTitle = project[projectId];
                    projectFilter.append(`
                        <option value="${projectId}">${projectTitle}</option>
                    `); // Предполагаем, что у проекта есть атрибуты id и name
                });
                //Первичная инициализация
                loadTasks();
            },
            error: function() {
                console.error("Ошибка при загрузке проектов");
            }
        });
        console.log("Проекты загружены")
    }
    function loadTasks() {
        const selectedProject = $('#projectFilter').val();
        const selectedTaskType = $('#taskFilter').val();

        let url = '/home/api/calendar/tasks';

        url += selectedTaskType === 'my' ? '/my' : '/all';
        if (selectedProject) {
            url += `/${selectedProject}`;
        }
        console.log("Current url: " + url);
        $.ajax({
            url: url,
            method: 'GET',
            success: function(tasks) {
                // Ваш код для обновления событий в календаре
                const events = tasks.map(task => ({
                    id: Number(task.taskId),
                    title: task.title,
                    description: task.description,
                    start: task.createdAt,
                    end: task.deadline,
                    color: getColorByPriority(task.priority),
                    priority: task.priority
                }));

                $('#calendar').fullCalendar('removeEvents'); // Удаляем старые события
                $('#calendar').fullCalendar('addEventSource', events); // Добавляем новые события

                $('#calendar').fullCalendar({
                    selectable: true,
                    selectHelper: true,
                    //Показ подробного описания задачи
                    eventClick: function(event){
                        $('#taskTitle').text(event.title);
                        $('#taskStart').text(event.start.format('YYYY-MM-DD HH:mm'));
                        $('#taskEnd').text(event.end ? event.end.format('YYYY-MM-DD HH:mm') : 'Без срока');
                        $('#taskPriority').text(event.priority);
                        $('#taskDescription').text(event.description ? event.description : 'Без описания');
                        // Добавляем отображение аватаров пользователе
                        console.log("Clicked this shit");
                        $.ajax({
                            url: `/home/api/calendar/tasks/${event.id}/users`,
                            method: 'GET',
                            success: function(users) {
                                const usersContainer = $('#taskUsers');
                                usersContainer.empty(); // Очищаем предыдущие данные

                                users.forEach(userMap => {
                                    // userMap будет объектом, где ключ — это id пользователя
                                    const userId = Object.keys(userMap)[0];
                                    const username = userMap[userId];

                                    const userElement = $('<div class="user-item"></div>');
                                    userElement.html(`
                            <img src="/home/user/${userId}/avatar" class="avatar" style="width: 50px; height: 50px; margin-bottom: 10px" alt="${username}'s avatar" />
                            <span>${username}</span>
                        `);
                                    usersContainer.append(userElement); // Добавляем пользователя в контейнер
                                });
                            },
                            error: function() {
                                console.error("Ошибка при загрузке пользователей");
                            }
                        });
                        $('#taskModal').modal('show');
                    },
                    editable: true,
                    //Перетаскивание (изменение даты начала)
                    eventDrop: function(event, delta, revertFunc){
                        let confirmChange = confirm("Вы уверены, что хотите изменить дату задачи?");
                        if (confirmChange){
                            let newStartDate = event.start; // Новая дата начала
                            let newEndDate = event.end ? event.end : null;
                            const dataToSend = {
                                taskId: event.id,
                                createdAt: newStartDate,
                                deadline: newEndDate
                            };
                            //console.log("data: " + JSON.stringify(dataToSend));
                            $.ajax({
                                url: "/home/api/calendar/tasks/update",
                                type: "POST",
                                headers: {
                                    'X-CSRF-Token': csrfToken
                                },
                                contentType: 'application/json', // Указываем тип содержимого как JSON
                                data: JSON.stringify(dataToSend),
                                success: function(response) {
                                    console.log("Задача успешно обновлена");
                                },
                                error: function(jqXHR) {
                                    if (jqXHR.status === 403) {
                                        alert("Ошибка: Доступ запрещен. Убедитесь, что у вас есть нужные права.");
                                    } else {
                                        alert("Ошибка при обновлении задачи");
                                    }
                                    revertFunc(); // Откат к предыдущему состоянию, если ошибка
                                }
                            })
                        }
                        else {
                            // Если пользователь отменил изменения
                            revertFunc(); // Откат к предыдущему состоянию
                            console.log("Изменения отменены пользователем");
                        }
                    },
                    events: events,
                    header: {
                        left: 'month, agendaWeek, agendaDay, list',
                        center: 'title',
                        right: 'prev, today, next'
                    },
                    buttonText: {
                        today: 'Today',
                        month: 'Month',
                        week: 'Week',
                        list: 'List'
                    }
                });
            }
        });
        // Функция для получения цвета по приоритету
        function getColorByPriority(priority) {
            switch (priority) {
                case 'LOW':
                    return '#28a745'; // Зеленый цвет
                case 'MEDIUM':
                    return '#ffc107'; // Желтый цвет
                case 'HIGH':
                    return '#dc3545'; // Красный цвет
                default:
                    return '#000000'; // Цвет по умолчанию (черный)
            }
        }
    }
});