<#import "template.ftl" as layout>
<#import "social-icons.ftl" as icons>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "header">
        Sign in
    <#elseif section = "form">
        <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">

            <div class="form-group">
                <label for="username" class="kc-label">
                    <#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>Email address</#if>
                </label>
                <input tabindex="1" id="username"
                       class="kc-input<#if messagesPerField.existsError('username','password')> is-error</#if>"
                       name="username" value="${(login.username!'')}"
                       type="text" autofocus autocomplete="email"
                       placeholder="you@company.com"
                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>" />
                <#if messagesPerField.existsError('username')>
                    <p class="field-error">${kcSanitize(messagesPerField.get('username'))?no_esc}</p>
                </#if>
            </div>

            <div class="form-group">
                <div class="flex-between">
                    <label for="password" class="kc-label">${msg("password")}</label>
                </div>
                <input tabindex="2" id="password"
                       class="kc-input<#if messagesPerField.existsError('username','password')> is-error</#if>"
                       name="password" type="password" autocomplete="current-password"
                       placeholder="••••••••"
                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>" />
                <#if messagesPerField.existsError('password')>
                    <p class="field-error">${kcSanitize(messagesPerField.get('password'))?no_esc}</p>
                <#elseif messagesPerField.existsError('username','password')>
                    <p class="field-error">${kcSanitize(messagesPerField.get('username','password'))?no_esc}</p>
                </#if>
            </div>

            <div class="remeber-forgot">

            <#if realm.rememberMe && !usernameHidden??>
                <div class="form-group checkbox-group">
                    <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" <#if login.rememberMe??>checked</#if>>
                    <label for="rememberMe">${msg("rememberMe")}</label>
                </div>
            </#if>
             <#if realm.resetPasswordAllowed>
                        <a tabindex="5" href="${url.loginResetCredentialsUrl}" class="kc-link">${msg("doForgotPassword")}</a>
                    </#if>
            </div>
            <div class="form-group">
                <button tabindex="4" class="kc-button" name="login" id="kc-login" type="submit">
                    ${msg("doLogIn")}
                </button>
            </div>

            <#if social.providers?? && social.providers?has_content>
                <div class="social-login-section">
                    <p class="social-login-label">Sign in using</p>
                    <div class="social-icons-row">
                        <#list social.providers as p>
                            <a href="${p.loginUrl}" id="social-${p.alias}" class="social-icon-btn" title="Continue with ${p.displayName!}">
                                <@icons.socialIcon provider=p />
                            </a>
                        </#list>
                    </div>
                </div>
            </#if>

            <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
                <div class="footer-action">
                    <span>Don't have an account? </span>
                    <a tabindex="6" href="${url.registrationUrl}" class="kc-link">${msg("doRegister")}</a>
                </div>
            </#if>
        </form>
    </#if>
</@layout.registrationLayout>
