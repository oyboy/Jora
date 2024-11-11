<#macro logout>
    <div>
        <form action="/logout" method="post">
            <button style="margin-left: 20px" type="submit" class="btn btn-primary">Выйти</button>
            <input type="hidden" name="_csrf" value="${_csrf.token}">
        </form>
    </div>
</#macro>