<#import "parts/common.ftlh" as c>
<@c.page>
    <script src="/static/scripts/calendar.js"></script>
    <body>
    <h2>Календарь</h2>
    <select id="projectFilter">
        <option value="all">Все мои проекты</option>
    </select>
    <select id="taskFilter">
        <option value="my">Мои задачи</option>
        <option value="all">Мои и коллег</option>
    </select>

    <div class="container">
        <div id="calendar"></div>
    </div>
    <br>
    <!-- Modal -->
    <div class="modal" id="taskModal" tabindex="-1" role="dialog" aria-labelledby="taskModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="taskTitle">Заголовок задачи</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Закрыть">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <p><strong>Начало:</strong> <span id="taskStart"></span></p>
                    <p><strong>Конец:</strong> <span id="taskEnd"></span></p>
                    <p><strong>Приоритет:</strong> <span id="taskPriority"></span></p>
                    <p><strong>Описание:</strong> <span id="taskDescription"></span></p>
                    <div id="taskUsers" class="users-list"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
                </div>
            </div>
        </div>
    </div>
    <input type="hidden" name="_csrf" value="${_csrf.token}">
    </body>
</@c.page>