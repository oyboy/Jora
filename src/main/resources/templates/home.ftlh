<#import "parts/common.ftlh" as c>

<@c.page>
    <form action="/home/create" method="get">
        <input type="submit" value="+"/>
    </form>
<h1 style="text-align: center">My Projects</h1>
    <#if projects??>
        <h1>Okay</h1>
        <#else>
        <h1>Not okay</h1>
    </#if>
<#list projects as project>
    <div>
        <h2>${project.title}</h2>
        <p>${project.description}</p>
        <i>${project.createdAt}</i>
        <a href="/projects/${project.id}/tasks">Go to tasks</a>
    </div>
    <#else>
    <h2>У вас ещё нет проектов. <a href="/home/create">Создать?</a></h2>
</#list>
</@c.page>