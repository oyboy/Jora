<#import "parts/common.ftlh" as c>
<@c.page>
    <h1>Task list</h1>
    <a href="/home">К проектам</a>
    <h1>Список задач</h1>
    <table style="border: 1px">
        <tr>
            <th colspan="6">Created</th>
            <th colspan="6">In progress</th>
            <th colspan="6">Done</th>
        </tr>
        <tr>
            <td>ID</td>
            <td>Название</td>
            <td>Описание</td>
            <td>Приоритет</td>
            <td>Статус</td>
            <td>Дата создания</td>
        </tr>
        <#list tasks as task>
            <tr>
                <td><h4>${task.id}</h4></td>
                <td><h2><a href="/projects/${project.hash}/tasks/edit/${task.id}">${task.name}</a></h2></td>
                <td><p>${task.description}</p></td>
                <td><i>${task.priority}</i></td>
                <td><i>${task.status}</i></td>
                <td><i>${task.createdAt}</i></td>
            </tr>
            <#else>
            <h3>Нет созданных задач</h3>
        </#list>
    </table>
    <h2>Добавить новую задачу</h2>
    <form action="/projects/${project.hash}/tasks" method="post">
        <label for="name">Название:</label>
        <input type="text" id="name" name="name" required>
        <#if errors?? && errors.getFieldError("name")??>
            <div class="error">${errors.getFieldError("name").defaultMessage}</div>
        </#if>
        <br>

        <label for="description">Описание:</label>
        <input type="text" id="description" name="description">
        <#if errors?? && errors.getFieldError("description")??>
            <div class="error">${errors.getFieldError("description").defaultMessage}</div>
        </#if>
        <br>

        <label>Приоритет:</label>
        <label>
            <input type="radio" name="priority" value="LOW"> Низкий
        </label>
        <label>
            <input type="radio" name="priority" value="MEDIUM"> Средний
        </label>
        <label>
            <input type="radio" name="priority" value="HIGH"> Высокий
        </label>
        <br>

        <input type="hidden" name="projectId" value="${project.id}">
        <button type="submit">Добавить задачу</button>
    </form>
</@c.page>