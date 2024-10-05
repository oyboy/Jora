<#import "parts/common.ftlh" as c>

<@c.page>
    <div style="text-align: right">
    <#if user?? && user.email??>
        <h4>Имя пользователя:<i>${user.username}</i></h4>
        <h4>Почта пользователя: <i>${user.email}</i></h4>
        <form action="/logout" method="post">
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <input type="submit" value="Выйти"/>
        </form>
    <#else >
        <a href="login">Войти</a>
    </#if>
    </div>
    <form action="/home/create" method="get">
        <input type="submit" value="+"/>
    </form>
<h1 style="text-align: center">My Projects</h1>
<#list projects as project>
    <div>
        <h2>${project.title}</h2>
        <p>${project.description}</p>
        <i>${project.createdAt?date}</i>
        <a href="/projects/${project.hash}/tasks">Go to tasks</a>
    </div>
    <#else>
    <h2>У вас ещё нет проектов. <a href="/home/create">Создать?</a></h2>
</#list>
</@c.page>