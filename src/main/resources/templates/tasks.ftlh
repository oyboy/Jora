<#import "parts/common.ftlh" as c>
<@c.page>
    <script src="/static/scripts/comments_script.js"></script>
    <script src="/static/scripts/tasks.js"></script>
    <h1 class="text-center">Список задач</h1>

    <form action="/projects/${project.hash}/tasks" method="get" id="filterForm" class="mb-4">
        <label for="deadlineFilter">Фильтр по дедлайну:</label>
        <select name="deadlineFilter" id="deadlineFilter" class="form-select d-inline w-auto">
            <option value="today">На сегодня</option>
            <option value="tomorrow">На завтра</option>
            <option value="thisMonth">На месяц</option>
            <option value="noDeadline">Без срока</option>
        </select>
        <button type="submit" class="btn btn-primary">Фильтровать</button>
        <button type="reset" id="resetButton" class="btn btn-secondary">Сбросить фильтр</button>
    </form>
    <script>
        document.getElementById('resetButton').addEventListener('click', function() {
            document.getElementById('deadlineFilter').value = "";
            document.getElementById('filterForm').submit();
        });
    </script>

    <#--Kanban-->
    <div class="container mt-4">
        <div class="row">
            <div class="col-md-4">
                <div class="kanban-column" data-status="CREATED">
                    <h3 class="text-primary">Создано</h3>
                    <div class="task-container border rounded p-2" ondrop="drop(event)" ondragover="allowDrop(event)">
                        <#list tasks as task>
                            <#if task.status == 'CREATED'>
                                <#include "parts/task-card.ftlh"/>
                            </#if>
                        </#list>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="kanban-column" data-status="IN_PROGRESS">
                    <h3 class="text-warning">В процессе</h3>
                    <div class="task-container border rounded p-2" ondrop="drop(event)" ondragover="allowDrop(event)">
                        <#list tasks as task>
                            <#if task.status == 'IN_PROGRESS'>
                                <#include "parts/task-card.ftlh"/>
                            </#if>
                        </#list>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="kanban-column" data-status="DONE">
                    <h3 class="text-success">Завершено</h3>
                    <div class="task-container border rounded p-2" ondrop="drop(event)" ondragover="allowDrop(event)">
                        <#list tasks as task>
                            <#if task.status == 'DONE'>
                                <#include "parts/task-card.ftlh"/>
                            </#if>
                        </#list>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <input type="hidden" id="projectHash" value="${project_hash}">


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
    <#if tagException??>
        <div class="alert alert-danger">${tagException}</div>
    </#if>

    <#include "parts/task-create.ftlh">
    <#include "parts/comment-modal.ftlh">

    <#if errors??>
        <div class="alert alert-danger">Поля заполнены неверно</div>
    </#if>
    <#if idException??>
        <div class="alert alert-danger">${idException}</div>
    </#if>
</@c.page>