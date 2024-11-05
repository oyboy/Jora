<#import "parts/common.ftlh" as c>

<@c.page>
    <div style="text-align: right">
    <#if user?? && user.email??>
        <img src="/home/user/${user.id}/avatar" class="avatar" style="width: 50px; height:50px"/>
        <h4>Имя пользователя:<a href="/home/user/${user.id}"><i>${user.username}</i></a></h4>
        <h4>Почта пользователя: <i>${user.email}</i></h4>
        <form action="/logout" method="post">
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <input type="submit" value="Выйти"/>
        </form>
    <#else >
        <a href="login">Войти</a>
    </#if>
        <#include "parts/notification.ftlh">
    </div>

    <form action="/home/join" method="post">
        <label for="project_hash">Ссылка: </label>
        <input type="text" id="project_hash" name="project_hash">
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit">Присоединиться</button>
    </form>
    <#if userError??>
        <div class="error">${userError}</div>
    </#if>

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
        <a href="/projects/${project.hash}/group">To group</a>
        <a href="/projects/${project.hash}/discussion">To discussion</a>
    </div>
    <#else>
    <h2>У вас ещё нет проектов. <a href="/home/create">Создать?</a></h2>
</#list>
</@c.page>