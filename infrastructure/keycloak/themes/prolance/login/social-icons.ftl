<#macro socialIcon provider>
    <#assign iconPath = url.resourcesPath + "/img/providers/" + provider.alias + ".svg">

    <img
        src="${iconPath}"
        alt="${provider.displayName!provider.alias} icon"
        class="social-icon"
        width="20"
        height="20"
    />
</#macro>