<#assign name = "unknown">
<#assign isAdmin = false>

<#if Session??>
    <#assign known = Session.SPRING_SECURITY_CONTEXT??>
    <#if known>
        <#assign
        user = Session.SPRING_SECURITY_CONTEXT.authentication.principal
        name = user.getUsername()
        isAdmin = user.isAdmin()
        >
    </#if>
<#else >
    <h3>No session</h3>
</#if>