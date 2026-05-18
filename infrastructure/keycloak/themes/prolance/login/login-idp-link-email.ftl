<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>

    <#if section = "header">
        Link ${idpDisplayName!}
    <#elseif section = "form">

        <div class="verify-email-body">

            <#-- Animated envelope + link icon -->
            <div class="verify-email-icon verify-email-icon--idp">
                <img src="${url.resourcesPath}/img/email.png" alt="Verify email" />
            </div>

            <#-- Title -->
    
            <#-- Instruction card -->
            <div class="verify-email-card">
                <p class="verify-email-intro">
                    An email with instructions to link
                    <strong>${idpDisplayName!}</strong> account
                    <#if brokerContext?? && brokerContext.username?has_content>
                        <strong>${brokerContext.username}</strong>
                    </#if>
                    with your Prolance account has been sent to you.
                </p>
            </div>

            <#-- Action links -->
            <div class="verify-email-actions">
                <p class="verify-email-hint">
                    Haven't received a verification code in your email?
                    <a href="${url.loginAction}" class="kc-link" style="font-weight:600;">Click here</a>
                    to re-send the email.
                </p>
                <p class="verify-email-hint">
                    If you already verified the email in a different browser
                    <a href="${url.loginRestartFlowUrl}" class="kc-link" style="font-weight:600;">Click here</a>
                    to continue.
                </p>
            </div>
        </div>
    </#if>
</@layout.registrationLayout>