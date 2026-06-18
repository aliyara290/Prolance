<#import "template.ftl" as layout>
<@layout.registrationLayout
    displayMessage=!messagesPerField.existsError('firstName','lastName','email','username');
    section>

    <#if section = "header">
        Update Account Information
    <#elseif section = "form">

        <form id="kc-idp-review-profile-form" action="${url.loginAction}" method="post">

            <#-- Username field -->
            <#if user.editUsernameAllowed>
                <div class="form-group">
                    <label for="username" class="kc-label">
                        ${msg("username")} <span class="field-required">*</span>
                    </label>
                    <input type="text" id="username" name="username"
                           class="kc-input<#if messagesPerField.existsError('username')> is-error</#if>"
                           value="${(user.username!'')}"
                           autocomplete="username"
                           placeholder="yourname"
                           aria-invalid="<#if messagesPerField.existsError('username')>true</#if>" />
                    <#if messagesPerField.existsError('username')>
                        <p class="field-error">${kcSanitize(messagesPerField.get('username'))?no_esc}</p>
                    </#if>
                </div>
            </#if>

            <#-- Email field -->
            <div class="form-group">
                <label for="email" class="kc-label">
                    ${msg("email")} <span class="field-required">*</span>
                </label>
                <input type="email" id="email" name="email"
                       class="kc-input<#if messagesPerField.existsError('email')> is-error</#if>"
                       value="${(user.email!'')}"
                       autocomplete="email"
                       placeholder="you@company.com"
                       aria-invalid="<#if messagesPerField.existsError('email')>true</#if>" />
                <#if messagesPerField.existsError('email')>
                    <p class="field-error">${kcSanitize(messagesPerField.get('email'))?no_esc}</p>
                </#if>
            </div>

            <#-- First name / Last name row -->
            <div class="form-row">
                <div class="form-group">
                    <label for="firstName" class="kc-label">
                        ${msg("firstName")} <span class="field-required">*</span>
                    </label>
                    <input type="text" id="firstName" name="firstName"
                           class="kc-input<#if messagesPerField.existsError('firstName')> is-error</#if>"
                           value="${(user.firstName!'')}"
                           autocomplete="given-name"
                           placeholder="John"
                           aria-invalid="<#if messagesPerField.existsError('firstName')>true</#if>" />
                    <#if messagesPerField.existsError('firstName')>
                        <p class="field-error">${kcSanitize(messagesPerField.get('firstName'))?no_esc}</p>
                    </#if>
                </div>

                <div class="form-group">
                    <label for="lastName" class="kc-label">
                        ${msg("lastName")} <span class="field-required">*</span>
                    </label>
                    <input type="text" id="lastName" name="lastName"
                           class="kc-input<#if messagesPerField.existsError('lastName')> is-error</#if>"
                           value="${(user.lastName!'')}"
                           autocomplete="family-name"
                           placeholder="Doe"
                           aria-invalid="<#if messagesPerField.existsError('lastName')>true</#if>" />
                    <#if messagesPerField.existsError('lastName')>
                        <p class="field-error">${kcSanitize(messagesPerField.get('lastName'))?no_esc}</p>
                    </#if>
                </div>
            </div>

            <div class="form-group" style="margin-top: 8px;">
                <button class="kc-button" type="submit">${msg("doSubmit")}</button>
            </div>

        </form>

    </#if>
</@layout.registrationLayout>
