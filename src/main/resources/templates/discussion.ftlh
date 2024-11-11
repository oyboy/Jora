<#import "parts/common.ftlh" as c>
<@c.page>
    <script src="/static/scripts/discussion.js"></script>

    <div class="container mt-4">
        <#if errorUserId??>
            <div class="alert alert-danger">${errorUserId}</div>
        </#if>

        <div class="commentsSection">
            <div class="commentsList mb-4">
                <#-- Здесь отображаются комментарии -->
                <!-- Функционал в js-коде -->
            </div>
            <form id="commentForm" enctype="multipart/form-data" class="mb-4">
                <div class="form-group">
                    <textarea class="form-control newComment" name="comment" placeholder="Добавьте ваш комментарий" required></textarea>
                </div>
                <div class="form-group">
                    <label for="fileUpload">Загрузить файл(ы)</label>
                    <input type="file" id="fileUpload" name="fileUpload" accept=".jpg, .jpeg, .png, .pdf" multiple class="form-control-file">
                </div>
                <button type="button" class="btn btn-primary addCommentButton">Отправить</button>
                <input type="hidden" id="projectHash" value="${projectHash}">
                <input type="hidden" id="currentUserId" value="${currentUser.id}">
                <input type="hidden" name="_csrf" value="${_csrf.token}">
            </form>
        </div>

        <#-- Окно для раскрытия изображения -->
        <div id="imageModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.8); z-index:9999;">
            <span id="closeModal" style="color:white; font-size:30px; position:absolute; top:10px; right:20px; cursor:pointer;">&times;</span>
            <img id="modalImage" class="img-fluid" style="max-height: 90%; max-width: 90%; margin: auto; display: block;"/>
        </div>

        <#-- Progress-bar -->
        <div id="uploadProgress" style="display:none;">
            <div class="progress">
                <div id="progressBar" class="progress-bar" style="width: 0%; height: 20px; background-color: green;"></div>
            </div>
        </div>
    </div>
</@c.page>
