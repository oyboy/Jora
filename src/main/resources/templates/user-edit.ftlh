<#import "parts/common.ftlh" as c>
<@c.page>
    <div class="container mt-4">
        <h2 class="mb-3">Страница редактирования</h2>
        <img src="/home/user/${user.id}/avatar" class="avatar mb-2"/>

        <div class="card mb-4">
            <div class="card-header">Редактировать аватар</div>
            <div class="card-body">
                <form method="post" enctype="multipart/form-data" action="/home/user/${user.id}/upload-avatar">
                    <div class="form-group">
                        <label for="avatarUpload">Загрузить изображение:</label>
                        <input type="file" name="file" class="form-control-file" id="avatarUpload" />
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                    </div>
                    <button type="submit" class="btn btn-primary">Сохранить аватар</button>
                </form>
                <form action="/home/user/${user.id}/avatar-delete" method="post" onsubmit="return confirmDelete()">
                    <input type="hidden" name="userId" value="${user.id}" />
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <button type="submit" class="btn btn-danger">Удалить аватар</button>
                </form>
                <script>
                    function confirmDelete() {
                        return confirm("Вы уверены, что хотите удалить аватар?");
                    }
                </script>
            </div>
        </div>
        <#if contentError??>
            <div class="alert alert-danger">${contentError}</div>
        </#if>

        <div class="card mb-4">
            <div class="card-header">Редактирование пользователя</div>
            <div class="card-body">
                <form action="/home/user/${user.id}" method="post">
                    <div class="form-group">
                        <label for="username">Имя пользователя:</label>
                        <input type="text" name="username" class="form-control" id="username" value="${user.username}">
                        <#if !userExistsError?? && errors?? && errors.getFieldError("username")??>
                            <div class="alert alert-danger">${errors.getFieldError("username").defaultMessage}</div>
                        </#if>
                    </div>

                    <div class="form-group">
                        <label for="email">Почта:</label>
                        <input type="text" name="email" class="form-control" id="email" value="${user.email}">
                        <#if !userExistsError?? && errors?? && errors.getFieldError("email")??>
                            <div class="alert alert-danger">${errors.getFieldError("email").defaultMessage}</div>
                        </#if>
                    </div>

                    <div class="form-group">
                        <label for="editPassword">Редактировать пароль:</label>
                        <input type="checkbox" name="editPassword" id="editPassword" onchange="togglePasswordFields()">
                    </div>

                    <script>
                        function togglePasswordFields() {
                            var checkbox = document.getElementById('editPassword');
                            var confirmPasswordField = document.getElementById('confirmPassword');
                            var passwordField = document.getElementById('password');
                            passwordField.disabled = !checkbox.checked;
                            confirmPasswordField.disabled = !checkbox.checked;
                            if (!checkbox.checked) {
                                passwordField.value = '';
                                confirmPasswordField.value = '';
                            }
                        }
                        document.addEventListener("DOMContentLoaded", function() {
                            togglePasswordFields();
                        });
                    </script>

                    <div class="form-group">
                        <label for="password">Пароль:</label>
                        <input type="password" name="password" class="form-control" id="password" disabled>
                        <#if !userExistsError?? && errors?? && errors.getFieldError("password")??>
                            <div class="alert alert-danger">${errors.getFieldError("password").defaultMessage}</div>
                        </#if>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Подтверждение пароля:</label>
                        <input type="password" name="confirmPassword" class="form-control" id="confirmPassword" disabled>
                        <#if !userExistsError?? && errors?? && errors.getFieldError("confirmPassword")??>
                            <div class="alert alert-danger">${errors.getFieldError("confirmPassword").defaultMessage}</div>
                        </#if>
                    </div>

                    <#if userExistsError??>
                        <div class="alert alert-danger">Пользователь с таким email уже существует</div>
                    </#if>

                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <button type="submit" class="btn btn-success mt-3">Сохранить</button>
                </form>
            </div>
        </div>

        <#if changesSaved??>
            <div class="alert alert-success">${changesSaved}</div>
        </#if>
    </div>
</@c.page>
