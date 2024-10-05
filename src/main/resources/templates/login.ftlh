<#import "parts/common.ftlh" as c>
<@c.page>
    <h2>Авторизация</h2>
    <form action="/login" method="post">
        <label for="email">Почта: <input type="text" name="username"></label>
        <br>
        <label for="password">Пароль: <input type="password" name="password"></label>
        <br>
        <input type="hidden" name="_csrf" value="${_csrf.token}">

        <#if SPRING_SECURITY_LAST_EXCEPTION??>
            <h4 style="color: red">Неправильный логин или пароль</h4>
        </#if>

        <input type="submit" name="Авторизация">
    </form>
    <a href="/registration">Регистрация</a>
</@c.page>