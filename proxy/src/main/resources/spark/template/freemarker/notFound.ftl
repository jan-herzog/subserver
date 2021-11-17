<#import "masterTemplate.ftl" as layout />

<@layout.masterTemplate title="Fehler">


<div class="message">
    <p class="top">404</p>
    <p class="bottom">Diese Seite existiert nicht!</p>
</div>

<style>
    .top {
        font-weight: bold;
        font-size: 35px;
        color: rgb(255, 187, 0);
    }
</style>
</@layout.masterTemplate>