$(document).ready(function() {
    const csrfToken = $('input[name="_csrf"]').val();
    let projectHash = $('#projectHash').val();

    window.allowDrop = function(event) {
        event.preventDefault();
    }
    window.drag = function(event) {
        event.dataTransfer.setData("text/plain", event.target.dataset.taskId);
    }
    window.drop = function(event, delta, revertFunc) {
        event.preventDefault();
        const taskId = event.dataTransfer.getData("text/plain");

        const taskElement = document.querySelector(`[data-task-id='${taskId}']`);
        if (taskElement) {
            const dropContainer = event.target.closest('.task-container');
            const column = event.target.closest('.kanban-column');

            if (dropContainer && !dropContainer.contains(taskElement)) {
                dropContainer.appendChild(taskElement); // Добавление задачи в целевой контейнер
                const newStatus = column.dataset.status;
                updateTaskStatus(taskId, newStatus, revertFunc);
            }
        }
    }
    function updateTaskStatus(taskId, newStatus, revertFunc) {
        const data = {
            id: Number(taskId),
            status: newStatus
        };
        console.log("Формируемый json: " + JSON.stringify(data));
        $.ajax( {
            url: `/api/v1/projects/${projectHash}/tasks/update`,
            method: 'POST',
            headers: {
                'X-CSRF-Token': csrfToken
            },
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                console.log("Задача успешно обновлена");
            },
            error: function() {
                alert("Ошибка при обновлении задачи");
                revertFunc();
            }
        })
    }
});