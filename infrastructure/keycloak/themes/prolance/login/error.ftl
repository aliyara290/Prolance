<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>

    <#if section = "header">
        <#if message?has_content>
            <#if message.type = "error">
                <#-- Use a friendlier title for known session-expiry errors -->
                <#if message.summary?contains("expired") || message.summary?contains("Expired")>
                    Page has expired
                <#else>
                    Something went wrong
                </#if>
            <#else>
                ${message.summary}
            </#if>
        <#else>
            Something went wrong
        </#if>
    <#elseif section = "form">

        <#-- Icon + body copy -->
        <div class="error-body">

           
            <#-- Restart / continue links (shown for page-expiry) -->
            <#if client?? && client.baseUrl?has_content>
                <p class="error-instructions">
                    To restart the login process
                    <a href="${client.baseUrl}" class="kc-link">Click here</a> .
                </p>
            </#if>
            <#if (skipLink!false) == false>
                <#if pageExpiredUrl?? && pageExpiredUrl?has_content>
                    <p class="error-instructions">
                        To continue the login process
                        <a href="${pageExpiredUrl}" class="kc-link">Click here</a> .
                    </p>
                </#if>
            </#if>

            <#-- General error message when not a typical expiry -->
            <#if message?has_content && !message.summary?contains("expired") && !message.summary?contains("Expired")>
                <div class="alert alert-error" style="margin-top: 16px;">
                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                    <span>${kcSanitize(message.summary)?no_esc}</span>
                </div>
            </#if>

            <#-- Back to login link -->
            <#if client?? && client.baseUrl?has_content>
                <div class="footer-action" style="margin-top: 24px;">
                    <a href="${client.baseUrl}" class="kc-link"
                       style="display: inline-flex; align-items: center; gap: 4px;">
                        <svg style="width:14px;height:14px;" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                        </svg>
                        Back to application
                    </a>
                </div>
            </#if>
        </div>

    </#if>
</@layout.registrationLayout>
