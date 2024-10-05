<#import "parts/common.ftlh" as c>
<@c.page>
    <h2>Авторизация</h2>
    <form action="/login" method="post">
        <label for="email">Почта: <input type="text" name="username"></label>
        <br>
        <label for="password">Пароль: <input type="password" name="password"></label>
        <br>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <input type="submit" name="Авторизация">
    </form>
    <#if errorMessage??>
        <h2 style="color: red">${errorMessage}</h2>
    </#if>
    <a href="/registration">Регистрация</a>
</@c.page>