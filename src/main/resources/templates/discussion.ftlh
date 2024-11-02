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
        <form id="commentForm" enctype="multipart/form-data">
            <label>
                <textarea class="newComment" name="comment" placeholder="Добавьте ваш комментарий" required></textarea>
            </label>
            <label>
                <input type="file" id="fileUpload" name="fileUpload" accept=".jpg, .jpeg, .png, .pdf" multiple>
                <span>Загрузить файл(ы)</span>
            </label>
            <button type="button" class="addCommentButton">Отправить</button>
            <input type="hidden" id="projectHash" value="${projectHash}">
            <input type="hidden" id="currentUserId" value="${currentUser.id}">
            <input type="hidden" name="_csrf" value="${_csrf.token}">
        </form>
    </div>
    <#----------------->
    <#--Окно для раскрытия изображения-->
    <div id="imageModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.8); z-index:9999;">
        <span id="closeModal" style="color:white; font-size:30px; position:absolute; top:10px; right:20px; cursor:pointer;">&times;</span>
        <img id="modalImage"/>
    </div>
    <#--Progress-bar-->
    <div id="uploadProgress" style="display:none;">
        <div id="progressBar" style="width: 0%; height: 20px; background-color: green;"></div>
    </div>
</@c.page>
