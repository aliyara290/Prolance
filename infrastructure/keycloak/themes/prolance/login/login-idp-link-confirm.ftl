<#import "template.ftl" as layout>
<@layout.registrationLayout; section>

    <#if section = "header">
        Account already exists
    <#elseif section = "form">


        <#-- Action buttons -->
        <div class="idp-link-actions">

            

            <form id="kc-idp-link-existing" action="${url.loginAction}" method="post">
                <input type="hidden" name="submitAction" value="linkAccount" />
                <button class="kc-button" type="submit">
                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" style="width:16px;height:16px;flex-shrink:0;">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
                    </svg>
                    Add to existing account
                </button>
            </form>

        </div>

    </#if>
</@layout.registrationLayout>
