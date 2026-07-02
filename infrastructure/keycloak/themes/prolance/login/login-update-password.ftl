<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('password','password-confirm'); section>
    <#if section = "header">
        Set a new password
    <#elseif section = "form">
        <form id="kc-update-password-form" action="${url.loginAction}" method="post">

            <#-- Password strength hint strip -->
            <div style="background: var(--color-primary-soft); border: 1px solid var(--color-primary); border-radius: 8px; padding: 12px 14px; margin-bottom: 20px; display: flex; align-items: flex-start; gap: 10px;">
                <svg style="width:18px;height:18px;color:var(--color-primary);flex-shrink:0;margin-top:1px;" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <p style="font-size:13px;color:var(--color-primary);line-height:1.5;margin:0;">
                    Choose a strong password with at least 8 characters, including uppercase letters, numbers and symbols.
                </p>
            </div>

            <input type="hidden" id="username" name="username" value="${username}" autocomplete="username" />

            <div class="form-group">
                <label for="password-new" class="kc-label">${msg("passwordNew")}</label>
                <div style="position:relative;">
                    <input type="password" id="password-new"
                           class="kc-input<#if messagesPerField.existsError('password','password-confirm')> is-error</#if>"
                           name="password-new"
                           style="padding-right: 44px;"
                           autocomplete="new-password"
                           autofocus
                           placeholder="Min. 8 characters"
                           aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>" />
                    <button type="button" onclick="togglePassword('password-new', this)" tabindex="-1"
                            style="position:absolute;right:12px;top:50%;transform:translateY(-50%);background:none;border:none;cursor:pointer;color:var(--color-text-muted);padding:0;display:flex;align-items:center;">
                        <svg id="eye-new" style="width:18px;height:18px;" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                    </button>
                </div>
                <#if messagesPerField.existsError('password')>
                    <p class="field-error">${kcSanitize(messagesPerField.get('password'))?no_esc}</p>
                </#if>
            </div>

            <div class="form-group">
                <label for="password-confirm" class="kc-label">${msg("passwordConfirm")}</label>
                <div style="position:relative;">
                    <input type="password" id="password-confirm"
                           class="kc-input<#if messagesPerField.existsError('password-confirm')> is-error</#if>"
                           name="password-confirm"
                           style="padding-right: 44px;"
                           autocomplete="new-password"
                           placeholder="Repeat your new password"
                           aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>" />
                    <button type="button" onclick="togglePassword('password-confirm', this)" tabindex="-1"
                            style="position:absolute;right:12px;top:50%;transform:translateY(-50%);background:none;border:none;cursor:pointer;color:var(--color-text-muted);padding:0;display:flex;align-items:center;">
                        <svg id="eye-confirm" style="width:18px;height:18px;" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                    </button>
                </div>
                <#if messagesPerField.existsError('password-confirm')>
                    <p class="field-error">${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}</p>
                </#if>
            </div>

            <#if isAppInitiatedAction??>
                <div class="form-group checkbox-group" style="margin-bottom: 4px;">
                    <input type="checkbox" id="logout-sessions" name="logout-sessions" value="on" checked>
                    <label for="logout-sessions">
                        Sign out from all other devices
                    </label>
                </div>
            </#if>

            <div class="form-group" style="margin-top: 8px; display: flex; gap: 10px; flex-direction: column;">
                <button class="kc-button" type="submit">${msg("doSubmit")}</button>
                <#if isAppInitiatedAction??>
                    <a href="${url.loginUrl}" class="kc-link" style="text-align:center; font-size:13px; color:var(--color-text-muted);">
                        Cancel, keep my current password
                    </a>
                </#if>
            </div>

        </form>

        <script>
            function togglePassword(fieldId, btn) {
                var input = document.getElementById(fieldId);
                var isText = input.type === 'text';
                input.type = isText ? 'password' : 'text';
                var svg = btn.querySelector('svg');
                if (isText) {
                    svg.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />';
                    btn.style.color = 'var(--color-text-muted)';
                } else {
                    svg.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />';
                    btn.style.color = 'var(--color-primary)';
                }
            }
        </script>
    </#if>
</@layout.registrationLayout>
