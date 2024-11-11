<#import "parts/common.ftlh" as c>
<@c.page>
<div class="container">
    <h2 class="mt-5">Регистрация</h2>
    <form action="/registration" method="post" class="mt-4">
        <div class="form-group">
            <label for="username">Имя пользователя:</label>
            <input type="text" class="form-control" name="username" id="username" required>
            <#if !userExistsError?? && errors?? && errors.getFieldError("username")??>
                <div class="alert alert-danger">${errors.getFieldError("username").defaultMessage}</div>
            </#if>
        </div>

        <div class="form-group">
            <label for="email">Почта:</label>
            <input type="email" class="form-control" name="email" id="email" required>
            <#if !userExistsError?? && errors?? && errors.getFieldError("email")??>
                <div class="alert alert-danger">${errors.getFieldError("email").defaultMessage}</div>
            </#if>
        </div>

        <div class="form-group">
            <label for="password">Пароль:</label>
            <input type="password" class="form-control" name="password" id="password" required>
            <#if !userExistsError?? && errors?? && errors.getFieldError("password")??>
                <div class="alert alert-danger">${errors.getFieldError("password").defaultMessage}</div>
            </#if>
        </div>

        <div class="form-group">
            <label for="confirmPassword">Подтверждение пароля:</label>
            <input type="password" class="form-control" name="confirmPassword" id="confirmPassword" required>
            <#if !userExistsError?? && errors?? && errors.getFieldError("confirmPassword")??>
                <div class="alert alert-danger">${errors.getFieldError("confirmPassword").defaultMessage}</div>
            </#if>
        </div>

        <#if userExistsError??>
            <div class="alert alert-danger">Пользователь с таким email уже существует</div>
        </#if>

        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit" class="btn btn-primary">Регистрация</button>
    </form>
    <a href="/login" class="mt-3 d-block">Авторизоваться</a>
</div>
</@c.page>
