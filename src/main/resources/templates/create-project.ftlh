<#import "parts/common.ftlh" as c>
<@c.page>
    <div class="container">
        <h1 class="text-center mb-4">Создание проекта</h1>

        <form method="post" action="/home/create" class="bg-light p-4 border rounded">
            <div class="mb-3">
                <label for="title" class="form-label">Название проекта:</label>
                <input type="text" name="title" id="title" class="form-control" required>
                <#if errors?? && errors.getFieldError("title")??>
                    <div class="alert alert-danger mt-2">${errors.getFieldError("title").defaultMessage}</div>
                </#if>
            </div>

            <div class="mb-3">
                <label for="description" class="form-label">Описание:</label>
                <textarea name="description" id="description" class="form-control" rows="4" placeholder="Введите описание (необязательно)"></textarea>
                <#if errors?? && errors.getFieldError("description")??>
                    <div class="alert alert-danger mt-2">${errors.getFieldError("description").defaultMessage}</div>
                </#if>
            </div>

            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <button type="submit" class="btn btn-primary">Сохранить</button>
        </form>
    </div>
</@c.page>
