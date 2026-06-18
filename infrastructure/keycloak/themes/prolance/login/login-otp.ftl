<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('totp'); section>
    <#if section = "header">
        Two-factor authentication
    <#elseif section = "form">
        <form id="kc-otp-login-form" action="${url.loginAction}" method="post">

            <#-- Device selector: only shown when user has multiple OTP credentials -->
            <#if otpLogin.userOtpCredentials?has_content && otpLogin.userOtpCredentials?size gt 1>
                <p class="kc-label" style="margin-bottom: 10px;">Verify with</p>
                <div class="otp-devices" id="otp-devices">
                    <#list otpLogin.userOtpCredentials as credential>
                        <label
                            class="otp-device-card <#if credential.id == otpLogin.selectedCredentialId>selected</#if>"
                            for="kc-otp-credential-${credential?index}"
                            onclick="selectDevice(this)"
                        >
                            <input
                                type="radio"
                                id="kc-otp-credential-${credential?index}"
                                name="selectedCredentialId"
                                value="${credential.id}"
                                <#if credential.id == otpLogin.selectedCredentialId>checked</#if>
                            />
                            <div class="otp-device-radio">
                                <div class="otp-device-radio-dot"></div>
                            </div>
                            <div class="otp-device-icon">
                                <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
                                          d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" />
                                </svg>
                            </div>
                            <div class="otp-device-info">
                                <div class="otp-device-name">${credential.userLabel}</div>
                                <div class="otp-device-type">Authenticator app</div>
                            </div>
                        </label>
                    </#list>
                </div>
            <#elseif otpLogin.userOtpCredentials?has_content>
                <#-- Single device — submit the id silently -->
                <input type="hidden" name="selectedCredentialId"
                       value="${otpLogin.userOtpCredentials[0].id}" />
            </#if>

            <#-- OTP hint -->
            <p class="otp-hint">
                Enter the 6-digit code from your authenticator app
            </p>

            <#-- Error state: shake the boxes and show message -->
            <#assign hasOtpError = messagesPerField.existsError('totp')>

            <#-- 6 individual digit boxes -->
            <div class="otp-digits-row<#if hasOtpError> otp-digits-error</#if>" id="otp-boxes">
                <input class="otp-digit<#if hasOtpError> is-error</#if>" type="text" inputmode="numeric" maxlength="1" placeholder="·" autocomplete="off" data-index="0" />
                <input class="otp-digit<#if hasOtpError> is-error</#if>" type="text" inputmode="numeric" maxlength="1" placeholder="·" autocomplete="off" data-index="1" />
                <input class="otp-digit<#if hasOtpError> is-error</#if>" type="text" inputmode="numeric" maxlength="1" placeholder="·" autocomplete="off" data-index="2" />
                <span class="otp-separator">—</span>
                <input class="otp-digit<#if hasOtpError> is-error</#if>" type="text" inputmode="numeric" maxlength="1" placeholder="·" autocomplete="off" data-index="3" />
                <input class="otp-digit<#if hasOtpError> is-error</#if>" type="text" inputmode="numeric" maxlength="1" placeholder="·" autocomplete="off" data-index="4" />
                <input class="otp-digit<#if hasOtpError> is-error</#if>" type="text" inputmode="numeric" maxlength="1" placeholder="·" autocomplete="off" data-index="5" />
            </div>

        

            <#-- Hidden real input that Keycloak reads -->
            <input class="otp-hidden-input" type="text" id="otp" name="otp" autocomplete="one-time-code" />

            <div class="form-group" style="margin-top: 24px;">
                <button id="kc-login" class="kc-button" type="submit">Verify &amp; Sign In</button>
            </div>

            <div class="otp-actions">
                <a href="${url.loginUrl}" class="kc-link">← Back to login</a>
            </div>

        </form>

        <script>
            (function () {
                var boxes  = Array.from(document.querySelectorAll('.otp-digit'));
                var hidden = document.getElementById('otp');
                var form   = document.getElementById('kc-otp-login-form');

                function sync() {
                    hidden.value = boxes.map(function(b){ return b.value; }).join('');
                }

                /* On error: clear boxes so user can re-type without extra friction */
                var hasError = document.querySelector('.otp-digit.is-error');
                if (hasError) {
                    boxes.forEach(function(b){ b.value = ''; });
                }

                boxes.forEach(function (box, i) {
                    box.addEventListener('keydown', function (e) {
                        if (e.key === 'Backspace') {
                            if (box.value === '' && i > 0) {
                                boxes[i - 1].value = '';
                                boxes[i - 1].focus();
                            } else {
                                box.value = '';
                            }
                            /* Remove error state as user starts re-typing */
                            boxes.forEach(function(b){ b.classList.remove('is-error'); });
                            document.querySelector('.otp-digits-row') && document.querySelector('.otp-digits-row').classList.remove('otp-digits-error');
                            sync();
                            e.preventDefault();
                            return;
                        }
                        if (e.key === 'ArrowLeft'  && i > 0)               { boxes[i - 1].focus(); e.preventDefault(); return; }
                        if (e.key === 'ArrowRight' && i < boxes.length - 1) { boxes[i + 1].focus(); e.preventDefault(); return; }
                        if (e.key === 'Enter') { sync(); form.submit(); return; }
                    });

                    box.addEventListener('input', function (e) {
                        /* Remove error styling once user starts typing again */
                        box.classList.remove('is-error');

                        var val = box.value.replace(/\D/g, '');
                        /* Handle paste of full code into any box */
                        if (val.length > 1) {
                            val.split('').slice(0, boxes.length).forEach(function(ch, j) {
                                boxes[j].value = ch;
                                boxes[j].classList.remove('is-error');
                            });
                            boxes[Math.min(val.length, boxes.length) - 1].focus();
                            sync();
                            return;
                        }
                        box.value = val;
                        sync();
                        if (val && i < boxes.length - 1) {
                            boxes[i + 1].focus();
                        }
                        /* Auto-submit when all 6 digits filled */
                        if (hidden.value.length === 6) {
                            setTimeout(function(){ form.submit(); }, 150);
                        }
                    });

                    box.addEventListener('paste', function (e) {
                        e.preventDefault();
                        var text = (e.clipboardData || window.clipboardData).getData('text').replace(/\D/g, '');
                        text.split('').slice(0, boxes.length).forEach(function(ch, j) {
                            boxes[j].value = ch;
                            boxes[j].classList.remove('is-error');
                        });
                        boxes[Math.min(text.length, boxes.length) - 1].focus();
                        sync();
                        if (hidden.value.length === 6) {
                            setTimeout(function(){ form.submit(); }, 150);
                        }
                    });
                });

                boxes[0].focus();

                /* Device card toggle */
                window.selectDevice = function(card) {
                    document.querySelectorAll('.otp-device-card').forEach(function(c) {
                        c.classList.remove('selected');
                    });
                    card.classList.add('selected');
                    card.querySelector('input[type="radio"]').checked = true;
                };
            })();
        </script>
    </#if>
</@layout.registrationLayout>
