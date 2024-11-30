<#import "parts/common.ftlh" as c>
<@c.page>
<h1 class="my-4" style="margin-left: 45px;">Редактирование задачи</h1>

<div class="container">
    <#if currentRole = "ROLE_MODERATOR">
        <form action="/projects/${project_hash}/tasks/edit/join" method="post" class="mb-4">
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <input type="hidden" name="task_id" value="${task.id}">
            <button type="submit" class="btn btn-outline-secondary">Присоединиться</button>
        </form>
        <#if userError??>
            <div class="alert alert-danger">${userError}</div>
        </#if>
    <#else>
        <form action="/projects/${project_hash}/tasks/edit/help" method="post" class="mb-4">
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <input type="hidden" name="task_id" value="${task.id}">
            <button type="submit" class="btn btn-warning">Назначить другого исполнителя</button>
        </form>
        <#if moderNotFound??>
            <div class="alert alert-danger">${moderNotFound}</div>
        </#if>
        <#if notificationResult??>
            <div class="alert alert-info">${notificationResult}</div>
        </#if>
    </#if>

    <form action="/projects/${project_hash}/tasks/edit" method="post">
        <div class="mb-3">
            <label for="name" class="form-label fw-bold">Название:</label>
            <input type="text" id="name" name="name" class="form-control" value="${task.name}" required>
            <#if errors?? && errors.getFieldError("name")??>
                <div class="alert alert-danger mt-1">${errors.getFieldError("name").defaultMessage}</div>
            </#if>
        </div>

        <div class="mb-3">
            <label for="description" class="form-label fw-bold">Описание:</label>
            <input type="text" id="description" name="description" class="form-control" value="${task.description}">
            <#if errors?? && errors.getFieldError("description")??>
                <div class="alert alert-danger mt-1">${errors.getFieldError("description").defaultMessage}</div>
            </#if>
        </div>

        <div class="mb-3">
            <label for="deadline" class="form-label fw-bold">Дата завершения:</label>
            <input type="datetime-local" name="deadline" class="form-control" value="${task.deadline?if_exists}">
            <#if errors?? && errors.getFieldError("deadline")??>
                <div class="alert alert-danger mt-1">${errors.getFieldError("deadline").defaultMessage}</div>
            </#if>
        </div>

        <fieldset class="mb-3">
            <legend class="col-form-label fw-bold">Приоритет:</legend>
            <div>
                <label class="form-check">
                    <input type="radio" name="priority" value="LOW" class="form-check-input"
                            ${(task.priority == "LOW")?then("checked", "")}>
                    <span class="form-check-label">Низкий</span>
                </label>
                <label class="form-check">
                    <input type="radio" name="priority" value="MEDIUM" class="form-check-input"
                            ${(task.priority == "MEDIUM")?then("checked", "")}>

                    <span class="form-check-label">Средний</span>
                </label>
                <label class="form-check">
                    <input type="radio" name="priority" value="HIGH" class="form-check-input"
                            ${(task.priority == "HIGH")?then("checked", "")}>
                    <span class="form-check-label">Высокий</span>
                </label>
            </div>
        </fieldset>

        <div class="mb-3">
            <label for="status" class="form-label fw-bold">Статус:</label>
            <select name="status" id="status" class="form-select">
                <option value="CREATED" ${(task.status == "CREATED")?then("selected","")}>Создано</option>
                <option value="IN_PROGRESS" ${(task.status == "IN_PROGRESS")?then("selected","")}>В процессе</option>
                <option value="TESTING" ${(task.status == "TESTING")?then("selected","")}>Тестируется</option>
                <option value="DONE" ${(task.status == "DONE")?then("selected","")}>Завершено</option>
            </select>
        </div>

        <input type="hidden" name="_csrf" value="${_csrf.token}">
<#--
        <input type="hidden" name="task_id" value="${task.id}">
-->
        <button type="submit" class="btn btn-success">Сохранить</button>
    </form>
</div>
</@c.page>
