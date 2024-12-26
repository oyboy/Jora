<#import "parts/common.ftlh" as c>
<@c.page>
<div class="container mt-4">
    <h2 class="mb-4">Управление группой</h2>
    <#if currentUserRole.role?string == "ROLE_LEADER">
        <form action="/projects/${project_hash}/group/ban" method="post" class="mb-4">
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="text" class="form-control" name="email" id="email" placeholder="Введите email">
            </div>
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <button type="submit" class="btn btn-danger mt-2">Забанить / разбанить</button>
        </form>
    </#if>

    <#if emailNotFound??>
        <div class="alert alert-danger">${emailNotFound}</div>
    </#if>
    <#if suicideError??>
        <div class="alert alert-danger">${suicideError}</div>
    </#if>

    <h4>Создать новый тег</h4>
    <form action="/projects/${project_hash}/group/tag-add" method="post" class="mb-4">
        <div class="form-group">
            <label for="tagName">Название тега:</label>
            <input type="text" class="form-control" name="tagName" id="tagName" placeholder="Введите название тега">
        </div>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit" class="btn btn-success mt-2">Создать</button>
    </form>

    <#if sizeException??>
        <div class="alert alert-danger">${sizeException}</div>
    </#if>
    <#if existsException??>
        <div class="alert alert-danger">${existsException}</div>
    </#if>

    <h4>Назначить тег</h4>
    <form action="/projects/${project_hash}/group/tag-set" method="post" class="mb-4">
        <div class="input-group mb-2">
            <div class="input-group-prepend">
                <label class="input-group-text" for="tagName">Название тега</label>
            </div>
            <select class="form-control" name="tagName" id="tagName">
                <#list tagsForProject as tag>
                    <option value="${tag.name}">${tag.name}</option>
                </#list>
            </select>
        </div>
        <div class="input-group">
            <div class="input-group-prepend">
                <label class="input-group-text" for="email">Пользователь:</label>
            </div>
            <select class="form-control" name="email" id="email">
                <#list usersAndTags as userAndTag>
                    <option value="${userAndTag.user.email}">${userAndTag.user.email}</option>
                </#list>
            </select>
        </div>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit" class="btn btn-info mt-2">Назначить</button>
    </form>

    <h4>Пользователи:</h4>
    <#if leaderChangeRoleException??>
        <div class="alert alert-danger">${leaderChangeRoleException}</div>
    </#if>
    <#list usersAndTags as userAndTag>
    <div class="card mb-3">
        <div class="card-body">
            <h5 class="card-title">${userAndTag.user.username}</h5>
            <p class="card-text">${userAndTag.user.email}</p>
            <p class="card-text"><strong>Роль:</strong> ${userAndTag.role}</p>
            <#if currentUserRole.role?string == "ROLE_LEADER">
                <form action="/projects/${project_hash}/group/change-role" method="post" class="mb-2">
                    <input type="hidden" name="email" value="${userAndTag.user.email}">
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <button type="submit" class="btn btn-secondary" name="action" value="PROMOTE">↑ Повысить роль</button>
                    <button type="submit" class="btn btn-secondary" name="action" value="DEMOTE">↓ Понизить роль</button>
                </form>
            </#if>
            <h6>Теги:</h6>
            <ul class="list-group">
                <#if userAndTag.tags?has_content>
                    <#list userAndTag.tags as tag>
                        <li class="list-group-item">${tag.name}</li>
                    </#list>
                <#else>
                    <li class="list-group-item">Нет привязанных тегов</li>
                </#if>
            </ul>
        </div>
    </div>
    </#list>
</div>
</@c.page>
