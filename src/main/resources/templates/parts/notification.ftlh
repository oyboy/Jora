<div class="notification-container">
    <div class="bell" id="notification-bell">
        <span class="notification-count" id="notification-count" style="display:none;"></span>
        🔔
    </div>
    <div class="dropdown" id="notification-dropdown" style="display:none;">
        <ul id="notification-list"></ul>
    </div>
    <script src="/static/scripts/notification_script.js"></script>
    <input type="hidden" id="currentUserId" value="${user.id}">
    <input type="hidden" name="_csrf" value="${_csrf.token}">
</div>