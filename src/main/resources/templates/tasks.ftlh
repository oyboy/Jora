<#import "parts/common.ftlh" as c>
<@c.page>
    <h1>Task list</h1>
    <a href="/home">К проектам</a>
    <h1>Список задач</h1>

    <form action="/projects/${project.hash}/tasks" method="get" id="filterForm">
        <label for="deadlineFilter">Фильтр по дедлайну:</label>
        <select name="deadlineFilter" id="deadlineFilter">
            <option value="today">На сегодня</option>
            <option value="tomorrow">На завтра</option>
            <option value="thisMonth">На месяц</option>
            <option value="noDeadline">Без срока</option>
        </select>
        <button type="submit">Фильтровать</button>
        <button type="reset" id="resetButton">Сбросить фильтр</button>
    </form>
    <script>
        document.getElementById('resetButton').addEventListener('click', function() {
            document.getElementById('deadlineFilter').value = "";
            document.getElementById('filterForm').submit();
        });
    </script>

    <table>
        <tr>
            <th colspan="7">Created</th>
            <th colspan="7">In progress</th>
            <th colspan="7">Done</th>
        </tr>
        <tr>
            <td>ID</td>
            <td>Название</td>
            <td>Описание</td>
            <td>Приоритет</td>
            <td>Статус</td>
            <td>Дата создания / Срок выполнения</td>
            <td>Автор</td>
        </tr>
        <#list tasks as task>
            <tr>
                <td><h4>${task.id}</h4></td>
                <td><h2><a href="/projects/${project.hash}/tasks/edit/${task.id}">${task.name}</a></h2></td>
                <td><p>${task.description}</p></td>
                <td><i>${task.priority}</i></td>
                <td><i>${task.status}</i></td>
                <td><i>${task.createdAt} |
                        <#if task.deadline??>
                            ${task.deadline}
                            <#else>
                                Без срока
                        </#if>
                </i></td>
                <td>
                    <#assign taskUsers = usersAndTasks?filter(ut -> ut.task.id == task.id) />
                    <#list taskUsers as userTask>
                        <p>${userTask.user.username} (${userTask.user.email})</p>
                    <#else>
                        <p>Нет привязанных пользователей</p>
                    </#list>
                </td>
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

        <label for="deadline">Дата завершения:</label>
        <input type="datetime-local" name="deadline" id="deadline">
        <script>
            const today = new Date();
            const formattedDate = today.toISOString().split('T')[0];
            document.getElementById('deadline').value = formattedDate;
        </script>
        <#if errors?? && errors.getFieldError("deadline")??>
            <div class="error">${errors.getFieldError("deadline").defaultMessage}</div>
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

        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <input type="hidden" name="projectId" value="${project.id}">
        <button type="submit">Добавить задачу</button>
    </form>
</@c.page>