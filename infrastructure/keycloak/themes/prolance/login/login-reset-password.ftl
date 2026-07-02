<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username'); section>
    <#if section = "header">
        ${msg("emailForgotTitle")}
    <#elseif section = "form">
        <form id="kc-reset-password-form" action="${url.loginAction}" method="post">

            <#-- Instruction text -->
            <div style="background: var(--color-card-muted); border-radius: 8px; padding: 12px 14px; margin-bottom: 20px; display: flex; align-items: flex-start; gap: 10px; border: 1px solid var(--color-border);">
                <svg style="width:18px;height:18px;color:var(--color-text-muted);flex-shrink:0;margin-top:1px;" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                <p style="font-size:13px;color:var(--color-text-secondary);line-height:1.5;margin:0;">
                    ${msg("emailInstruction")}
                </p>
            </div>

            <div class="form-group">
                <label for="username" class="kc-label">
                    <#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>Email address</#if>
                </label>
                <input type="text" id="username" name="username"
                       class="kc-input<#if messagesPerField.existsError('username')> is-error</#if>"
                       autofocus value="${(auth.attemptedUsername!'')}"
                       placeholder="you@company.com"
                       aria-invalid="<#if messagesPerField.existsError('username')>true</#if>" />
                <#if messagesPerField.existsError('username')>
                    <p class="field-error">${kcSanitize(messagesPerField.get('username'))?no_esc}</p>
                </#if>
            </div>

            <div class="form-group" style="margin-top: 8px;">
                <button class="kc-button" type="submit">${msg("doSubmit")}</button>
            </div>

            <div class="footer-action">
                <a href="${url.loginUrl}" class="kc-link" style="display: inline-flex; align-items: center; gap: 4px;">
                    <svg style="width:14px;height:14px;" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                    </svg>
                    ${msg("backToLogin")}
                </a>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
