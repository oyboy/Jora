<div class="card mb-3 task" draggable="true" ondragstart="drag(event)" data-task-id="${task.id}">
    <div class="card-body">
        <h5 class="card-title">
            <a class="text-decoration-none" href="/projects/${project.hash}/tasks/edit/${task.id}">
                <span class="badge bg-primary">${task.id}</span> ${task.name}
            </a>
        </h5>
        <p class="card-text">${task.description}</p>
        <p class="text-muted">
            Приоритет: <span class="font-weight-bold">${task.priority}</span>
        </p>
        <p class="text-muted">
            Дата создания: ${task.createdAt}<br>
            Дедлайн:
            <#if task.deadline??>
                <span class="font-weight-bold">${task.deadline}</span>
            <#else>Без срока</#if>
        </p>

        <h6>Теги:</h6>
        <ul class="list-group mb-2">
            <#assign taskTags = tasksAndTags?filter(tt -> tt.task.id == task.id) />
            <#list taskTags as taskTag>
                <#list taskTag.tags as tag>
                    <li class="list-group-item">${tag.name}</li>
                </#list>
            </#list>
        </ul>

        <h6>Участники:</h6>
        <div class="mb-2">
            <#assign taskUsers = usersAndTasks?filter(ut -> ut.task.id == task.id) />
            <#list taskUsers as userTask>
                <p class="text-secondary">${userTask.user.username} (${userTask.user.email})</p>
            </#list>
        </div>

        <button class="btn btn-outline-info showCommentsButton" data-task-id="${task.id}">Комментарии</button>
        <div class="commentsSection" data-task-id="${task.id}" style="display: none">
            <h6 class="mt-2">Комментарии к задаче</h6>
            <div class="commentsList mb-2"></div>
            <label>
                <textarea class="newComment form-control" placeholder="Добавьте ваш комментарий"></textarea>
            </label>
            <button class="btn btn-success addCommentButton mt-2" data-task-id="${task.id}">Добавить комментарий</button>
        </div>

        <input type="hidden" id="projectHash" value="${project.hash}">
        <input type="hidden" id="currentUsername" value="${user.username}">
        <input type="hidden" id="taskId" value="${task.id}">
    </div>
</div>
