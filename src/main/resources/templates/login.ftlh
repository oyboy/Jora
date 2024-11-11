<#import "parts/common.ftlh" as c>
<@c.page>
    <div class="h-100 container d-flex justify-content-center align-items-center margin-top-10">
        <div class="col-md-6">
            <h2 class="text-center mb-4">Авторизация</h2>
            <form action="/login" method="post" class="border p-4 rounded shadow">
                <div class="form-group">
                    <label for="email">Почта:</label>
                    <input type="text" class="form-control" name="username" id="email" required>
                </div>
                <div class="form-group">
                    <label for="password">Пароль:</label>
                    <input type="password" class="form-control" name="password" id="password" required>
                </div>
                <input type="hidden" name="_csrf" value="${_csrf.token}">

                <#if SPRING_SECURITY_LAST_EXCEPTION??>
                    <div class="alert alert-danger">Неправильный логин или пароль</div>
                </#if>

                <button type="submit" class="btn btn-primary btn-block">Авторизация</button>
            </form>
            <a href="/registration" class="mt-3 d-block text-left">Регистрация</a>
        </div>
    </div>
</@c.page>
