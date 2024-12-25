<#import "parts/common.ftlh" as c>
<@c.page>
    <div class="container">
        <h1 class="text-center mb-4">Мои проекты</h1>

        <form action="/home/join" method="post" class="mb-4">
            <div class="input-group">
                <label for="project_hash" class="input-group-text">Ссылка:</label>
                <input type="text" id="project_hash" name="project_hash" class="form-control" placeholder="Введите ссылку проекта" required>
                <input type="hidden" name="_csrf" value="${_csrf.token}">
                <button type="submit" class="btn btn-secondary">Присоединиться</button>
            </div>
            <#if userError??>
                <div class="alert alert-danger mt-2">${userError}</div>
            </#if>
        </form>

        <form action="/home/create" method="get" class="mb-4">
            <button type="submit" class="btn btn-success">+ Создать новый проект</button>
        </form>

        <#list projects as project>
            <div class="card mb-3">
                <div class="card-body">
                    <h2 class="card-title d-flex justify-content-between">
                        <span>${project.title}</span>
                        <i class="bi bi-link-45deg"
                           style="cursor: pointer;"
                           onclick="copyLink('${project.hash}', this)"
                           data-bs-container="body"
                           data-bs-toggle="popover"
                           data-bs-content="Ссылка скопирована!"
                           data-bs-placement="top">
                        </i>
                    </h2>
                    <p class="card-text" style="white-space: pre-wrap;">${project.description}</p>
                    <i class="text-muted">${project.createdAt?date}</i>
                    <div class="d-flex justify-content-between align-items-center mt-2">
                        <div>
                            <a href="/projects/${project.hash}/tasks" class="btn btn-info btn-sm">Перейти к задачам</a>
                            <a href="/projects/${project.hash}/group" class="btn btn-secondary btn-sm">В группу</a>
                            <a href="/projects/${project.hash}/discussion" class="btn btn-warning btn-sm">В обсуждение</a>
                        </div>
                        <form action="/home/delete" method="post" class="d-inline" style="justify-content: right">
                            <i class="bi bi-trash text-danger"
                               style="font-size: 1.5rem;
                               cursor: pointer;"
                               onclick="event.preventDefault();
                               if(confirm('Вы уверены, что хотите удалить этот проект?'))
                               { this.closest('form').submit(); }">
                            </i>
                            <input type="hidden" name="projectId" value="${project.id}">
                            <input type="hidden" name="_csrf" value="${_csrf.token}">
                        </form>
                    </div>
                </div>
            </div>
        </#list>

        <#if projects?size == 0>
            <h2 class="text-center">У вас ещё нет проектов. <a href="/home/create">Создать?</a></h2>
        </#if>
    </div>

    <script>
        function copyLink(hash, element) {
            // Для поддержки старых браузеров
            const textarea = document.createElement('textarea');
            textarea.value = hash;
            document.body.appendChild(textarea);
            textarea.select();
            document.execCommand('copy');
            document.body.removeChild(textarea);

            $(element).popover('show');
            setTimeout(() => {
                $(element).popover('hide');
            }, 1500);
        }
    </script>
</@c.page>