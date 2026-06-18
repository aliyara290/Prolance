<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>

    <#if section = "header">
        <#-- We derive the heading from the message summary; fallback to a generic title -->
        <#if message?has_content>
            <#-- shorten very long summaries for the <title> -->
            ${message.summary?truncate(60, "…")}
        <#else>
            Information
        </#if>
    <#elseif section = "form">

        <div class="info-body">

            <#-- ── Context-aware icon ── -->
            <#assign isSuccess  = !message?has_content || message.type == "success"
                     isVerified = message?has_content && (message.summary?lower_case?contains("verified") || message.summary?lower_case?contains("confirmed"))
                     isEmail    = message?has_content && (message.summary?lower_case?contains("email") || message.summary?lower_case?contains("link"))>

            <div class="info-icon-wrap <#if isVerified>info-icon-wrap--success<#elseif isEmail>info-icon-wrap--email<#else>info-icon-wrap--info</#if>">

                <#if isVerified>
                    <#-- Green double-circle check for "verified" state -->
                    <div class="info-icon-ring">
                        <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <circle cx="24" cy="24" r="22" stroke="#16a34a" stroke-width="2" stroke-dasharray="5 3" opacity="0.3"/>
                            <circle cx="24" cy="24" r="16" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
                            <path d="M16 24l6 6 10-12" stroke="#16a34a" stroke-width="2.4"
                                  stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </div>
                <#elseif isEmail>
                    <#-- Blue envelope for "email sent / link" state -->
                    <div class="info-icon-ring">
                        <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <circle cx="24" cy="24" r="22" stroke="#267af7" stroke-width="2" stroke-dasharray="5 3" opacity="0.25"/>
                            <circle cx="24" cy="24" r="16" fill="#e8f1ff" stroke="#267af7" stroke-width="2"/>
                            <rect x="14" y="19" width="20" height="13" rx="2.5" stroke="#267af7" stroke-width="1.8" fill="none"/>
                            <path d="M14 22l10 7 10-7" stroke="#267af7" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </div>
                <#else>
                    <#-- Default info circle -->
                    <div class="info-icon-ring">
                        <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <circle cx="24" cy="24" r="22" stroke="#267af7" stroke-width="2" stroke-dasharray="5 3" opacity="0.25"/>
                            <circle cx="24" cy="24" r="16" fill="#e8f1ff" stroke="#267af7" stroke-width="2"/>
                            <path d="M24 22v8M24 18h.01" stroke="#267af7" stroke-width="2.2" stroke-linecap="round"/>
                        </svg>
                    </div>
                </#if>
            </div>

            <#-- ── Main message ── -->
            <#if message?has_content>
                <p class="info-summary">${kcSanitize(message.summary)?no_esc}</p>
            </#if>

            <#-- ── "Back to Application" link ── -->
            <#if client?? && client.baseUrl?has_content>
                <a href="${client.baseUrl}" class="kc-button info-cta-btn">
                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor"
                         style="width:16px;height:16px;flex-shrink:0;">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                    </svg>
                    Back to Application
                </a>
            </#if>

            <#-- ── Restart / Continue links (page expiry fallback) ── -->
            <#if (skipLink!false) == false>
                <#if pageRedirectUri?has_content>
                    <div class="info-secondary-action">
                        <a href="${pageRedirectUri}" class="kc-link">
                            ${msg("backToApplication")}
                        </a>
                    </div>
                <#elseif actionUri?has_content>
                    <div class="info-secondary-action">
                        <a href="${actionUri}" class="kc-link">
                            ${msg("proceedWithAction")}
                        </a>
                    </div>
                </#if>
            </#if>

        </div>

    </#if>
</@layout.registrationLayout>
