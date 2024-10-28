<#import "parts/common.ftlh" as c>
<@c.page>
    <script src="/static/scripts/discussion.js"></script>
    <#if errorUserId??>
        <div class="error">${errorUserId}</div>
    </#if>
    <div class="commentsSection">
        <div class="commentsList">
            <#-- Здесь отображаются комментарии -->
            <!--Функционал в js-коде -->
        </div>
        <label>
            <textarea class="newComment" placeholder="Добавьте ваш комментарий"></textarea>
        </label>
        <button class="addCommentButton">Добавить комментарий</button>
        <input type="hidden" id="projectHash" value="${projectHash}">
        <input type="hidden" id="currentUserId" value="${currentUser.id}">
        <input type="hidden" name="_csrf" value="${_csrf.token}">
    </div>
    <#----------------->
    <#--Поле с читателями комментария-->
    <div id="readerModal" class="modal">
        <div class="modal-content">
            <span class="close-button" id="close-button">&times;</span>
            <h2>Прочитали:</h2>
            <div id="readerList"></div>
        </div>
    </div>
</@c.page>
