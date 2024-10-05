<#import "parts/common.ftlh" as c>
<@c.page>
    <h2>Регистрация</h2>
    <form action="/registration" method="post">
        <label>Имя пользователя:  <input type="text" name="username"></label>
        <#if !userExistsError?? && errors?? && errors.getFieldError("username")??>
            <div class="error">${errors.getFieldError("username").defaultMessage}</div>
        </#if>
        <br>

        <label>Почта:  <input type="text" name="email"></label>
        <#if !userExistsError?? && errors?? && errors.getFieldError("email")??>
            <div class="error">${errors.getFieldError("email").defaultMessage}</div>
        </#if>
        <br>

        <label>Пароль: <input type="password" name="password"></label>
        <#if !userExistsError?? && errors?? && errors.getFieldError("password")??>
            <div class="error">${errors.getFieldError("password").defaultMessage}</div>
        </#if>
        <br>

        <label>Подтверждение пароля: <input type="password" name="confirmPassword"></label>
        <#if !userExistsError?? && errors?? && errors.getFieldError("confirmPassword")??>
            <div class="error">${errors.getFieldError("confirmPassword").defaultMessage}</div>
        </#if>
        <br>
        <#if userExistsError??>
            <div class="error">Пользователь с таким email уже существует</div>
        </#if>

        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <input type="submit" name="Регистрация">
    </form>
    <a href="/login">Авторизоваться</a>
</@c.page>