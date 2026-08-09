<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg("loginTitle",(realm.displayName!''))}</title>
    <link rel="icon" href="${url.resourcesPath}/img/prolance-logo-blue.png" />
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <link rel="preload" as="image" href="./img/background.webp">
</head>
<body>

    <div class="split-card">

        <!-- LEFT PANEL — Form -->
        <div class="split-left">

            <!-- Logo -->
            <div class="logo-container">
                <div class="logo-lockup">
                    <div class="">
                        <img src="${url.resourcesPath}/img/logo.svg.webp" width="150" alt="Prolance">
                    </div>
                    <span class="logo-text">Prolance</span>
                </div>
            </div>

            <!-- Page heading -->
            <div class="card-header">
                <h1 class="card-title">
                    <#nested "header">
                </h1>
                <p class="card-subtitle">to access your Prolance workspace</p>
            </div>

            <!-- Alert messages -->
            <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                <div class="alert alert-${message.type}">
                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <#if message.type == 'success'>
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                        <#elseif message.type == 'error'>
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                        <#else>
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </#if>
                    </svg>
                    <span>${kcSanitize(message.summary)?no_esc}</span>
                </div>
            </#if>

            <!-- Form slot -->
            <#nested "form">

        </div>

    </div>

    <!-- Page-level footer -->
    <div class="page-footer">
        <div class="page-footer-links">
            <a href="#">Privacy Policy</a>
            <span>•</span>
            <a href="#">Terms of Service</a>
        </div>
        <p>© 2026 Prolance Inc. All rights reserved.</p>
    </div>

</body>
</html>
</#macro>
