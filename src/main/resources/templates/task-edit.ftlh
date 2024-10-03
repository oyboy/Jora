<#import "parts/common.ftlh" as c>
<@c.page>
    <h1>Task edit</h1>
    <form action="/projects/${project_hash}/tasks/edit" method="post">
        <label for="name">Название:</label>
        <input type="text" id="name" name="name" value="${task.name}" required>
        <br>

        <label for="description">Описание:</label>
        <input type="text" id="description" name="description" value="${task.description}" required>
        <br>

        <label>Приоритет:</label>
        <label>
            <input type="radio" name="priority" value="LOW"
                    ${(task.priority == "LOW")?then("checked", "")} required> Низкий
        </label>
        <label>
            <input type="radio" name="priority" value="MEDIUM"
                    ${(task.priority == "MEDIUM")?then("checked", "")}> Средний
        </label>
        <label>
            <input type="radio" name="priority" value="HIGH"
                    ${(task.priority == "HIGH")?then("checked", "")}> Высокий
        </label>
        <br>

        <label for="status">Статус: </label>
        <select name="status" id="status">
            <option value="CREATED"
                    ${(task.status == "CREATED")?then("selected","")}>Created</option>
            <option value="IN_PROGRESS"
                    ${(task.status == "IN_PROGRESS")?then("selected","")}>In progress</option>
            <option value="DONE"
                    ${(task.status == "DONE")?then("selected","")}>Done</option>
        </select>

        <input type="hidden" name="task_id" value="${task.id}">
        <button type="submit">Сохранить</button>
    </form>
</@c.page>