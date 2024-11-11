<#import "parts/common.ftlh" as c>
<@c.page>
    <script src="/static/scripts/calendar.js"></script>
    <body>
    <div class="container mt-4">
        <div class="row mb-3">
            <div class="col-md-6">
                <label for="projectFilter">Фильтр проектов</label>
                <select id="projectFilter" class="form-control">
                    <option value="all">Все мои проекты</option>
                </select>
            </div>
            <div class="col-md-6">
                <label for="taskFilter">Фильтр задач</label>
                <select id="taskFilter" class="form-control">
                    <option value="my">Мои задачи</option>
                    <option value="all">Мои и коллег</option>
                </select>
            </div>
        </div>

        <div id="calendar" class="border border-white"></div>
    </div>
    <!-- Modal -->
    <div class="modal fade" id="taskModal" tabindex="-1" aria-labelledby="taskModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="taskTitle">Заголовок задачи</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Закрыть"></button>
                </div>
                <div class="modal-body">
                    <p><strong>Начало:</strong> <span id="taskStart"></span></p>
                    <p><strong>Конец:</strong> <span id="taskEnd"></span></p>
                    <p><strong>Приоритет:</strong> <span id="taskPriority"></span></p>
                    <p><strong>Описание:</strong> <span id="taskDescription"></span></p>
                    <div id="taskUsers" class="users-list"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
                </div>
            </div>
        </div>
    </div>
    <input type="hidden" name="_csrf" value="${_csrf.token}">
    </body>
</@c.page>