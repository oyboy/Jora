<#import "parts/common.ftlh" as c>
<@c.page>
    <a href="/home">to projects</a>
    <h2>Welcome to group</h2>
    <#if currentUserRole.role?string == "ROLE_LEADER">
        <form action="/projects/${project_hash}/group/ban" method="post">
            <label for="email">
                Email: <input type="text" name="email">
            </label>
            <br>
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <button type="submit">Забанить / разбанить</button>
        </form>
    </#if>
    <#if emailNotFound??>
        <div class="error">${emailNotFound}</div>
    </#if>
    <#if suicideError??>
        <div class="error">${suicideError}</div>
    </#if>

        <h4>Создать новый тег</h4>
        <form action="/projects/${project_hash}/group/tag-add" method="post">
            <label for="tagName">
                Название тега: <input type="text" name="tagName">
            </label>
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <button type="submit">Создать</button>
        </form>
        <#if sizeException??>
            <div class="error">${sizeException}</div>
        </#if>
        <#if existsException??>
            <div class="error">${existsException}</div>
        </#if>
    <br>

    <h4>Назначить тег</h4>
    <form action="/projects/${project_hash}/group/tag-set" method="post">
        <label for="tagName">Название тега: </label>
        <select name="tagName" id="tagName">
            <#list tagsForProject as tag>
                <option value="${tag.name}">${tag.name}</option>
            </#list>
        </select>
        <label for="email">Пользователь: </label>
        <select name="email" id="email">
            <#list usersAndTags as userAndTag>
                <option value="${userAndTag.user.email}">${userAndTag.user.email}</option>
            </#list>
        </select>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit">Назначить</button>
    </form>

    <h4>Пользователи: </h4>
    <#list usersAndTags as userAndTag>
        <b>${userAndTag.user.username}</b>
        <p>${userAndTag.user.email}</p>
        <p>${userAndTag.role}</p>
        <form action="/projects/${project_hash}/group/change-role" method="post">
            <input type="hidden" name="email" value="${userAndTag.user.email}">
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <button type="submit" name="action" value="PROMOTE">↑ Повысить роль</button>
            <button type="submit" name="action" value="DEMOTE">↓ Понизить роль</button>
        </form>
        <ul>
            <#if userAndTag.tags?has_content>
                <#list userAndTag.tags as tag>
                    <li>${tag.name}</li>
                </#list>
            <#else>
                <p>Нет привязанных тегов</p>
            </#if>
        </ul>
    </#list>

</@c.page>