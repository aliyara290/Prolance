<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>

    <#if section = "header">
        ${msg("emailVerifyTitle")}
    <#elseif section = "form">

        <div class="verify-email-body">

            <#-- Animated envelope icon -->
            <div class="verify-email-icon">
                <img src="${url.resourcesPath}/img/email.png" alt="Verify email" />
            </div>

            <#-- Title & subtitle -->
            <h2 class="verify-email-title">${msg("emailVerifyTitle")}</h2>

            <#-- Main instruction card -->
            <div class="verify-email-card">
                <p class="verify-email-intro">
                    ${msg("emailVerifyInstruction1", user.email!'')}
                </p>
            </div>

            <#-- Resend / continue action links -->
            <div class="verify-email-actions">

                <#-- Resend email -->
                <a href="${url.loginAction}" class="kc-button verify-email-btn-primary">
                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" style="width:16px;height:16px;flex-shrink:0;">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                    </svg>
                    ${msg("doClickHere")} to re-send the email
                </a>

                <p class="verify-email-hint">
                    ${msg("emailVerifyInstruction2")}
                    <a href="${url.loginRestartFlowUrl}" class="kc-link" style="font-weight:600;">
                        ${msg("doClickHere")}
                    </a>
                    ${msg("emailVerifyInstruction3")}
                </p>

            </div>

        </div>

    </#if>
</@layout.registrationLayout>
