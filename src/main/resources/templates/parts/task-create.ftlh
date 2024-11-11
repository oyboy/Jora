<button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addTaskModal">
    Создать новую задачу
</button>
<form action="/projects/${project.hash}/tasks" method="post">
<div class="modal fade" id="addTaskModal" tabindex="-1" aria-labelledby="addTaskModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addTaskModalLabel">Добавить новую задачу</h5>
            </div>
            <div class="modal-body">
                <form action="/projects/${project.hash}/tasks" method="post">
                    <!-- Поля формы по аналогии с вашим кодом -->
                    <div class="form-group">
                        <label for="name">Название:</label>
                        <input type="text" id="name" name="name" class="form-control" required>
                        <#if errors?? && errors.getFieldError("name")??>
                            <div class="alert alert-danger">Размер заголовка не должен превышать 50 символов</div>
                        </#if>
                    </div>
                    <div class="form-group">
                        <label for="description">Описание:</label>
                        <input type="text" id="description" name="description" class="form-control">
                        <#if errors?? && errors.getFieldError("description")??>
                            <div class="alert alert-danger">Размер описания не должен превышать 255 символов</div>
                        </#if>
                    </div>
                    <div class="form-group">
                        <label for="deadline">Дата завершения:</label>
                        <input type="datetime-local" name="deadline" id="deadline" class="form-control">
                        <#if errors?? && errors.getFieldError("deadline")??>
                            <div class="alert alert-danger">Проверьте время. Оно не должно совпадать с текущим или быть меньше него</div>
                        </#if>
                    </div>
                    <div class="form-group">
                        <label>Приоритет:</label>
                        <div>
                            <label><input type="radio" name="priority" value="LOW"> Низкий</label>
                            <label><input type="radio" name="priority" value="MEDIUM"> Средний</label>
                            <label><input type="radio" name="priority" value="HIGH"> Высокий</label>
                        </div>
                    </div>
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <input type="hidden" name="projectId" value="${project.id}">
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
                <button type="submit" class="btn btn-primary">Создать задачу</button>
            </div>
        </div>
    </div>
</div>
</form>