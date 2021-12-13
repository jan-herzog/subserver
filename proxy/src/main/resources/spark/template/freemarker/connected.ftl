<#import "masterTemplate.ftl" as layout />

<@layout.masterTemplate title="Verbunden">


<div class="message">
    <p class="top">Verbunden mit <a style="color: lime;"><#if username??>${username}<#else>non-UTF8-name</#if></a></p>
    <p class="bottom">Du kannst dieses Fenster jetzt schließen.</p>
</div>

<style>
    .top {
        font-weight: bold;
        font-size: 35px;
        color: rgb(255, 187, 0);
    }
</style>
</@layout.masterTemplate>