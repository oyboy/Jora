<#macro page>
    <!DOCTYPE HTML>
    <html lang="en">
    <head>
        <title>Jora</title>
        <link rel="stylesheet" href="/static/css/style.css" type="text/css">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.0/sockjs.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    </head>
    <body>
    <div>
        <#nested>
    </div>
    </body>
    </html>
</#macro>