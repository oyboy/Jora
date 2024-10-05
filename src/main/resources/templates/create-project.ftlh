<#import "parts/common.ftlh" as c>
<@c.page>
    <a href="/home">Home</a>
    <h1>Creating project</h1>
    <form method="post" action="/home/create">

        <label>Название проекта: <input type="text" name="title" /></label><br>
        <#if errors?? && errors.getFieldError("title")??>
            <div class="error">${errors.getFieldError("title").defaultMessage}</div>
        </#if>

        <label>Описание: <input type="text" name="description" value="optional" /></label><br>
        <#if errors?? && errors.getFieldError("description")??>
            <div class="error">${errors.getFieldError("description").defaultMessage}</div>
        </#if>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit">Сохранить</button>
    </form>
</@c.page>