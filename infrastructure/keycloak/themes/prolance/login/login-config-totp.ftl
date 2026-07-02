<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('totp','userLabel'); section>
    <#if section = "header">
        <style>
            .split-card {
                max-width: 850px;
            }
            .totp-two-column {
                display: flex;
                gap: 32px;
                align-items: flex-start;
            }
            .totp-info-col {
                flex: 1;
                background: var(--color-background-alt, #F9FAFB);
                border-radius: 12px;
                padding: 24px;
                border: 1px solid var(--color-border, #E5E7EB);
            }
            .totp-input-col {
                flex: 0 0 320px;
                padding-top: 12px;
            }
            
            @media (max-width: 768px) {
                .totp-two-column {
                    flex-direction: column;
                }
                .totp-input-col {
                    flex: auto;
                    width: 100%;
                    padding-top: 0;
                }
            }
        </style>
        ${msg("loginTotpTitle")}
    <#elseif section = "form">
        <form action="${url.loginAction}" class="form-credentials" id="kc-totp-settings-form" method="post">
            
            <div class="totp-two-column">
                <!-- INFO PART -->
                <div class="totp-info-col">
                    <ol style="margin: 0; padding-left: 20px; font-size: 14px; color: var(--color-text-secondary, #4B5563); display: flex; flex-direction: column; gap: 20px;">
                        <li style="padding-left: 8px;">
                            <p style="margin: 0 0 10px 0; color: var(--color-text, #111827); font-weight: 500;">
                                ${msg("loginTotpStep1")}
                            </p>
                            <ul style="margin: 0; padding-left: 20px; color: var(--color-text-muted, #6B7280); display: flex; flex-direction: column; gap: 4px;">
                                <#list totp.supportedApplications as app>
                                    <li>${msg(app)}</li>
                                </#list>
                            </ul>
                        </li>

                        <#if mode?? && mode = "manual">
                            <li style="padding-left: 8px;">
                                <p style="margin: 0 0 10px 0; color: var(--color-text, #111827); font-weight: 500;">
                                    ${msg("loginTotpManualStep2")}
                                </p>
                                <p style="font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; background: white; padding: 12px 16px; border-radius: 8px; border: 1px solid var(--color-border, #E5E7EB); word-break: break-all; margin-bottom: 12px; font-size: 13px; color: var(--color-primary, #2563EB);">
                                    <span id="kc-totp-secret-key">${totp.totpSecretEncoded}</span>
                                </p>
                                <p style="margin: 0;">
                                    <a href="${totp.qrUrl}" id="mode-barcode" class="kc-link" style="font-size: 13px; font-weight: 500;">${msg("loginTotpScanBarcode")}</a>
                                </p>
                            </li>
                            <li style="padding-left: 8px;">
                                <p style="margin: 0 0 10px 0; color: var(--color-text, #111827); font-weight: 500;">
                                    ${msg("loginTotpManualStep3")}
                                </p>
                                <ul style="margin: 0; padding-left: 20px; color: var(--color-text-muted, #6B7280); display: flex; flex-direction: column; gap: 4px;">
                                    <li id="kc-totp-type">${msg("loginTotpType")}: ${msg("loginTotp." + totp.policy.type)}</li>
                                    <li id="kc-totp-algorithm">${msg("loginTotpAlgorithm")}: ${totp.policy.getAlgorithmKey()}</li>
                                    <li id="kc-totp-digits">${msg("loginTotpDigits")}: ${totp.policy.digits}</li>
                                    <#if totp.policy.type = "totp">
                                        <li id="kc-totp-period">${msg("loginTotpInterval")}: ${totp.policy.period}</li>
                                    <#elseif totp.policy.type = "hotp">
                                        <li id="kc-totp-counter">${msg("loginTotpCounter")}: ${totp.policy.initialCounter}</li>
                                    </#if>
                                </ul>
                            </li>
                        <#else>
                            <li style="padding-left: 8px;">
                                <p style="margin: 0 0 12px 0; color: var(--color-text, #111827); font-weight: 500;">
                                    ${msg("loginTotpStep2")}
                                </p>
                                <div style="background: white; padding: 16px; border-radius: 12px; display: inline-flex; justify-content: center; align-items: center; margin-bottom: 12px; border: 1px solid var(--color-border, #E5E7EB); box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);">
                                    <img id="kc-totp-secret-qr-code" src="data:image/png;base64, ${totp.totpSecretQrCode}" alt="Figure: Barcode" style="width: 160px; height: 160px; display: block;" />
                                </div>
                                <div style="margin-top: 4px;">
                                    <a href="${totp.manualUrl}" id="mode-manual" class="kc-link" style="font-size: 13px; font-weight: 500;">${msg("loginTotpUnableToScan")}</a>
                                </div>
                            </li>
                        </#if>
                        
                        <li style="padding-left: 8px;">
                            <p style="margin: 0; color: var(--color-text, #111827); font-weight: 500;">
                                ${msg("loginTotpStep3")}
                            </p>
                            <p style="margin: 6px 0 0 0; color: var(--color-text-muted, #6B7280); font-size: 13px; line-height: 1.5;">
                                ${msg("loginTotpStep3DeviceName")}
                            </p>
                        </li>
                    </ol>
                </div>

                <!-- INPUTS PART -->
                <div class="totp-input-col">
                    <div class="form-group" style="margin-bottom: 16px;">
                        <label for="totp" class="kc-label" style="font-weight: 500;">${msg("authenticatorCode")} <span style="color: #ef4444;">*</span></label>
                        <input type="text" id="totp" name="totp" autocomplete="off" 
                               class="kc-input<#if messagesPerField.existsError('totp')> is-error</#if>"
                               placeholder="000000"
                               style="letter-spacing: 2px; font-size: 16px;"
                               aria-invalid="<#if messagesPerField.existsError('totp')>true</#if>"
                               autofocus />
                        <#if messagesPerField.existsError('totp')>
                            <p class="field-error">${kcSanitize(messagesPerField.get('totp'))?no_esc}</p>
                        </#if>
                    </div>
                    
                    <input type="hidden" id="totpSecret" name="totpSecret" value="${totp.totpSecret}" />
                    <#if mode??><input type="hidden" id="mode" name="mode" value="${mode}"/></#if>

                    <div class="form-group" style="margin-bottom: 24px;">
                        <label for="userLabel" class="kc-label" style="font-weight: 500;">${msg("loginTotpDeviceName")} <#if totp.otpCredentials?size gte 1><span style="color: #ef4444;">*</span></#if></label>
                        <input type="text" id="userLabel" name="userLabel" autocomplete="off"
                               class="kc-input<#if messagesPerField.existsError('userLabel')> is-error</#if>"
                               <#if totp.otpCredentials?size gte 1>required</#if>
                               placeholder="e.g. My iPhone"
                               aria-invalid="<#if messagesPerField.existsError('userLabel')>true</#if>" />
                        <#if messagesPerField.existsError('userLabel')>
                            <p class="field-error">${kcSanitize(messagesPerField.get('userLabel'))?no_esc}</p>
                        </#if>
                    </div>

                    <#if isAppInitiatedAction??>
                        <div class="form-group checkbox-group" style="margin-bottom: 24px;">
                            <input type="checkbox" id="logout-sessions" name="logout-sessions" value="on" checked>
                            <label for="logout-sessions" style="font-size: 14px;">
                                Sign out from all other devices
                            </label>
                        </div>
                    </#if>

                    <div class="form-group" style="display: flex; gap: 12px; flex-direction: column; margin-top: 8px;">
                        <button class="kc-button" type="submit" id="kc-save" style="width: 100%;">
                            ${msg("doSubmit")}
                        </button>
                        <#if isAppInitiatedAction??>
                            <button type="submit" class="kc-button" id="kc-cancel" name="cancel-aia" value="true" style="width: 100%; background: white; border: 1px solid var(--color-border, #E5E7EB); color: var(--color-text, #111827);">
                                ${msg("doCancel")}
                            </button>
                        <#elseif cancelRequired??>
                            <button type="submit" class="kc-button" id="kc-cancel" name="cancel" value="true" style="width: 100%; background: white; border: 1px solid var(--color-border, #E5E7EB); color: var(--color-text, #111827);">
                                ${msg("doCancel")}
                            </button>
                        </#if>
                    </div>
                </div>

            </div>
        </form>
    </#if>
</@layout.registrationLayout>
