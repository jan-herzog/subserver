<#import "masterTemplate.ftl" as layout />

<@layout.masterTemplate title="Fehler">

<div class="message">
    <p class="top">Fehler</p>
    <p class="bottom">Dein Account wurde nicht verbunden! <br>Die Anfrage war ungültig.<br> Du kannst dieses Fenster jetzt schließen.</p>
</div>

<style>
    .top {
        font-weight: bold;
        font-size: 35px;
        color: red;
    }
</style>

</@layout.masterTemplate>