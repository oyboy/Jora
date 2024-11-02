<#import "parts/common.ftlh" as c>
<@c.page>
    <form action="/projects/${project_hash}/test/all" method="post">
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button>Send all</button>
    </form>
</@c.page>