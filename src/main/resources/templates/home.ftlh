<#import "parts/common.ftlh" as c>
<@c.page>
    <div class="container">
        <h1 class="text-center mb-4">My Projects</h1>

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
                    <div class="mt-2">
                        <a href="/projects/${project.hash}/tasks" class="btn btn-info btn-sm">Перейти к задачам</a>
                        <a href="/projects/${project.hash}/group" class="btn btn-secondary btn-sm">В группу</a>
                        <a href="/projects/${project.hash}/discussion" class="btn btn-warning btn-sm">В обсуждение</a>
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
            navigator.clipboard.writeText(hash).then(() => {
                $(element).popover('show');
                console.log("copued");
                setTimeout(() => {
                    $(element).popover('hide');
                }, 1500);
            }).catch(err => {
                console.error('Ошибка при копировании: ', err);
            });
        }
    </script>
</@c.page>