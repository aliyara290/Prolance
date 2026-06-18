<#import "template.ftl" as layout>
<#import "social-icons.ftl" as icons>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm'); section>
    <#if section = "header">
        Create account
    <#elseif section = "form">
        <form id="kc-register-form" action="${url.registrationAction}" method="post">

            <div class="form-row">
                <div class="form-group">
                    <label for="firstName" class="kc-label">${msg("firstName")}</label>
                    <input type="text" id="firstName"
                           class="kc-input<#if messagesPerField.existsError('firstName')> is-error</#if>"
                           name="firstName" value="${(register.formData.firstName!'')}"
                           autocomplete="given-name" placeholder="John" />
                    <#if messagesPerField.existsError('firstName')>
                        <p class="field-error">${kcSanitize(messagesPerField.get('firstName'))?no_esc}</p>
                    </#if>
                </div>
                <div class="form-group">
                    <label for="lastName" class="kc-label">${msg("lastName")}</label>
                    <input type="text" id="lastName"
                           class="kc-input<#if messagesPerField.existsError('lastName')> is-error</#if>"
                           name="lastName" value="${(register.formData.lastName!'')}"
                           autocomplete="family-name" placeholder="Doe" />
                    <#if messagesPerField.existsError('lastName')>
                        <p class="field-error">${kcSanitize(messagesPerField.get('lastName'))?no_esc}</p>
                    </#if>
                </div>
            </div>

            <div class="form-group">
                <label for="email" class="kc-label">${msg("email")}</label>
                <input type="email" id="email"
                       class="kc-input<#if messagesPerField.existsError('email')> is-error</#if>"
                       name="email" value="${(register.formData.email!'')}"
                       autocomplete="email" placeholder="you@company.com" />
                <#if messagesPerField.existsError('email')>
                    <p class="field-error">${kcSanitize(messagesPerField.get('email'))?no_esc}</p>
                </#if>
            </div>

            <#if !realm.registrationEmailAsUsername>
                <div class="form-group">
                    <label for="username" class="kc-label">${msg("username")}</label>
                    <input type="text" id="username"
                           class="kc-input<#if messagesPerField.existsError('username')> is-error</#if>"
                           name="username" value="${(register.formData.username!'')}"
                           autocomplete="username" placeholder="yourname" />
                    <#if messagesPerField.existsError('username')>
                        <p class="field-error">${kcSanitize(messagesPerField.get('username'))?no_esc}</p>
                    </#if>
                </div>
            </#if>

            <#if passwordRequired??>
                <div class="form-row">
                    <div class="form-group">
                        <label for="password" class="kc-label">${msg("password")}</label>
                        <input type="password" id="password"
                               class="kc-input<#if messagesPerField.existsError('password','password-confirm')> is-error</#if>"
                               name="password" autocomplete="new-password"
                               placeholder="Min. 8 characters" />
                        <#if messagesPerField.existsError('password')>
                            <p class="field-error">${kcSanitize(messagesPerField.get('password'))?no_esc}</p>
                        </#if>
                    </div>
                    <div class="form-group">
                        <label for="password-confirm" class="kc-label">${msg("passwordConfirm")}</label>
                        <input type="password" id="password-confirm"
                               class="kc-input<#if messagesPerField.existsError('password-confirm')> is-error</#if>"
                               name="password-confirm" autocomplete="new-password"
                               placeholder="Repeat password" />
                        <#if messagesPerField.existsError('password-confirm')>
                            <p class="field-error">${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}</p>
                        </#if>
                    </div>
                </div>
            </#if>

            <div class="form-group" style="margin-top: 4px;">
                <button class="kc-button" type="submit">${msg("doRegister")}</button>
            </div>

            <#if social.providers?? && social.providers?has_content>
                <div class="social-login-section">
                    <p class="social-login-label">Sign up using</p>
                    <div class="social-icons-row">
                        <#list social.providers as p>
                            <a href="${p.loginUrl}" id="social-${p.alias}" class="social-icon-btn" title="Continue with ${p.displayName!}">
                                <@icons.socialIcon provider=p />
                            </a>
                        </#list>
                    </div>
                </div>
            </#if>

            <div class="footer-action">
                <span>Already have an account? </span>
                <a href="${url.loginUrl}" class="kc-link">${msg("doLogIn")}</a>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
