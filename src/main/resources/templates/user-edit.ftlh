<#import "parts/common.ftlh" as c>
<@c.page>
    <h2>Страница редактирования</h2>
    <img src="/home/user/${currentUser.id}/avatar" class="avatar"/>
    <#--Загрузить аватар-->
    <form method="post" enctype="multipart/form-data" action="/home/user/${currentUser.id}/upload-avatar">
        Загрузить изображение: <input type="file" name="file" value="Загрузить новый аватар">
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <input type="submit" value="Сохранить аватар">
    </form>
    <#--Удалить аватар-->
    <form action="/home/user/${currentUser.id}/avatar-delete" method="post">
        <input type="hidden" name="userId" value="${currentUser.id}" />
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit">Удалить аватар</button>
    </form>
    <#if contentError??>
        <div class="error">${contentError}</div>
    </#if>

    <form action="/home/user/${currentUser.id}" method="post">
        <label>Имя пользователя:  <input type="text" name="username" value="${currentUser.username}"></label>
        <#if !userExistsError?? && errors?? && errors.getFieldError("username")??>
            <div class="error">${errors.getFieldError("username").defaultMessage}</div>
        </#if>
        <br>

        <label>Почта:  <input type="text" name="email" value="${currentUser.email}"></label>
        <#if !userExistsError?? && errors?? && errors.getFieldError("email")??>
            <div class="error">${errors.getFieldError("email").defaultMessage}</div>
        </#if>
        <br>

        <label>Редактировать пароль: <input type="checkbox" name="editPassword" id="editPassword" onchange="togglePasswordFields()"></label>
        <br>
        <script>
            function togglePasswordFields() {
                var checkbox = document.getElementById('editPassword');
                var confirmPasswordField = document.getElementById('confirmPassword');
                var passwordField = document.getElementById('password');
                if (checkbox.checked) {
                    passwordField.disabled = false;      // Разрешить ввод нового пароля
                    confirmPasswordField.disabled = false;
                    passwordField.value = '';
                } else {
                    passwordField.disabled = true;       // Запретить ввод нового пароля
                    confirmPasswordField.disabled = true;
                }
            }
            document.addEventListener("DOMContentLoaded", function() {
                togglePasswordFields();
            });
        </script>

        <label>Пароль: <input type="password" name="password" id="password"></label>
        <#if !userExistsError?? && errors?? && errors.getFieldError("password")??>
            <div class="error">${errors.getFieldError("password").defaultMessage}</div>
        </#if>
        <input type="hidden" id="encryptedPassword" value="${currentUser.password}"/>
        <br>

        <label>Подтверждение пароля: <input type="password" name="confirmPassword" id="confirmPassword"></label>
        <#if !userExistsError?? && errors?? && errors.getFieldError("confirmPassword")??>
            <div class="error">${errors.getFieldError("confirmPassword").defaultMessage}</div>
        </#if>
        <br>
        <#if userExistsError??>
            <div class="error">Пользователь с таким email уже существует</div>
        </#if>

        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <input type="submit" name="Редактировать">
    </form>
    <#if changesSaved??>
        <div class="info">${changesSaved}</div>
    </#if>
    <a href="/home">Назад</a>
</@c.page>