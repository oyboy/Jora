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
    <#list usersAndRoles as userRole>
        <b>${userRole.user.username}</b>
        <p>${userRole.user.email}</p>
        <p>${userRole.role}</p>
    </#list>
</@c.page>