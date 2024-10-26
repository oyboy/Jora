<#import "parts/common.ftlh" as c>
<@c.page>
    <script src="/static/scripts/comments_script.js"></script>
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
            <td>Приоритет | Статус</td>
            <td>Теги</td>
            <td>Дата создания / Срок выполнения</td>
            <td>Автор</td>
        </tr>
        <#list tasksAndTags as taskTagsDTO>
            <tr class="task" data-task-id="${taskTagsDTO.task.id}">
                <td><h4>${taskTagsDTO.task.id}</h4></td>
                <td><h2><a href="/projects/${project.hash}/tasks/edit/${taskTagsDTO.task.id}">${taskTagsDTO.task.name}</a></h2></td>
                <td><p>${taskTagsDTO.task.description}</p></td>
                <td>
                    <i>${taskTagsDTO.task.priority}</i> |
                    <i>${taskTagsDTO.task.status}</i>
                </td>
                <td>
                    <ul>
                        <#list taskTagsDTO.tags as tag>
                            <li>${tag.name}</li>
                            <#else>
                            <p>Нет тегов</p>
                        </#list>
                    </ul>
                </td>
                <td><i>${taskTagsDTO.task.createdAt} |
                        <#if taskTagsDTO.task.deadline??>
                            ${taskTagsDTO.task.deadline}
                        <#else>
                            Без срока
                        </#if>
                    </i></td>
                <td>
                    <#assign taskUsers = usersAndTasks?filter(ut -> ut.task.id == taskTagsDTO.task.id) />
                    <#list taskUsers as userTask>
                        <p>${userTask.user.username} (${userTask.user.email})</p>
                    <#else>
                        <p>Нет привязанных пользователей</p>
                    </#list>
                    <#--Поле с комментариями-->
                    <button class="showCommentsButton" data-task-id="${taskTagsDTO.task.id}">Комментарии</button>
                    <div class="commentsSection" data-task-id="${taskTagsDTO.task.id}" style="display: none">
                        <h3>Комментарии к задаче</h3>
                        <p><strong> ${taskTagsDTO.task.name}</strong>
                            Описание задачи: ${taskTagsDTO.task.description}
                        </p>
                        <div class="commentsList">
                            <#-- Здесь отображаются комментарии -->
                            <!--Функционал в js-коде -->
                        </div>
                        <label>
                            <textarea class="newComment" placeholder="Добавьте ваш комментарий"></textarea>
                        </label>
                        <button class="addCommentButton" data-task-id="${taskTagsDTO.task.id}">Добавить комментарий</button>
                        <input type="hidden" id="projectHash" value="${project.hash}">
                        <input type="hidden" id="currentUsername" value="${currentUser.username}">
                        <input type="hidden" id="currentUserId" value="${currentUser.id}">
                        <input type="hidden" id="taskId" value="${taskTagsDTO.task.id}">
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                    </div>
                    <#----------------->
                </td>
            </tr>
        <#else>
            <h3>Нет созданных задач</h3>
        </#list>
    </table>

    <h2>Назначить тег</h2>
    <form action="/projects/${project_hash}/tasks/tag-set" method="post">
        <label for="tagName">Название тега: </label>
        <select name="tagName" id="tagName">
            <#list tagsForProject as tag>
                <option value="${tag.name}">${tag.name}</option>
            </#list>
        </select>
        <label for="taskId">ID задачи: </label>
        <input type="number" name="taskId">
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit">Назначить / Удалить</button>
    </form>
    <#if idException??>
        <div class="error">${idException}</div>
    </#if>

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