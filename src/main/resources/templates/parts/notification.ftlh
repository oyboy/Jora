<div class="dropdown me-3">
    <div class="bell" id="notification-bell" data-bs-toggle="dropdown" aria-expanded="false">
        <span class="notification-count" id="notification-count" style="display:none;">0</span>
        🔔
    </div>
    <div class="dropdown-menu dropdown-menu-end" id="notification-dropdown" style="display:none;">
        <ul id="notification-list" class="list-unstyled">
            <#if notifications?? && notifications?size > 0>
                <#list notifications as notification>
                    <li class="dropdown-item border-bottom" data-id="${notification.id}">
                        <a href="${notification.link}">
                            <strong>${notification.title}</strong><br>
                            <span class="small">${notification.message}</span>
                        </a>
                    </li>
                </#list>
            <#else>
                <li class="dropdown-item text-muted">Нет новых уведомлений</li>
            </#if>
        </ul>
    </div>
    <script src="/static/scripts/notification_script.js"></script>
    <input type="hidden" id="currentUserId" value="${user.id}">
    <input type="hidden" name="_csrf" value="${_csrf.token}">
</div>