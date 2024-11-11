<#import "logout.ftlh" as l>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">Jora</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="/home">Главная</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="/home/calendar">Календарь</a>
                </li>
            </ul>
            <div class="d-flex align-items-center">
                <#if user?? && user.email??>
                    <div class="me-5">
                        <#include "notification.ftlh">
                    </div>

                    <div class="d-flex align-items-center">
                        <img src="/home/user/${user.id}/avatar" class="avatar me-2" style="width: 50px; height:50px" alt="Avatar"/>
                        <div class="text-center">
                            <h6 class="mb-0"><a href="/home/user/${user.id}"><i>${user.username}</i></a></h6>
                            <small class="text-muted"><i>${user.email}</i></small>
                        </div>
                        <div class="ms-3">
                            <@l.logout></@l.logout>
                        </div>
                    </div>
                <#else>
                    <a href="/login" class="btn btn-outline-primary ms-3">Войти</a>
                </#if>
            </div>
        </div>
    </div>
</nav>
