	<div id="minimail" class="client-habblet-container contains-minimail draggable">
<div class="habblet-container ">		
		<div class="cb clearfix blue "><div class="bt"><div></div></div><div class="i1"><div class="i2"><div class="i3">
		<div class="rounded-container"><div style="background-color: rgb(255, 255, 255);"><div style="margin: 0px 1px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(111, 153, 196);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(53, 113, 173);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(39, 103, 167);"></div></div></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(53, 113, 173);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(39, 103, 167);"></div></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(111, 153, 196);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(39, 103, 167);"></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(53, 113, 173);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(39, 103, 167);"></div></div></div><h2 class="title rounded-done">Minimail
		<span class="habblet-close"></span></h2><div style="background-color: rgb(255, 255, 255);"><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(53, 113, 173);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(39, 103, 167);"></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(111, 153, 196);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(39, 103, 167);"></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(53, 113, 173);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(39, 103, 167);"></div></div></div><div style="margin: 0px 1px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(111, 153, 196);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(53, 113, 173);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(39, 103, 167);"></div></div></div></div></div></div>


	
	<div id="minimail-container">
    <div class="minimail-contents">
{% include "habblet/minimail/minimail_messages.tpl" %}

</div>
	<div id="message-compose-wait"></div>
    <form style="display: none;" id="message-compose">
        <div>A</div>
        <div id="message-recipients-container" class="input-text" style="width: 426px; margin-bottom: 1em">
        	<input type="text" value="" id="message-recipients">
        	<div class="autocomplete" id="message-recipients-auto">
        		<div class="default" style="display: none;">Digita il nome del tuo Amico</div>
        		<ul class="feed" style="display: none;"></ul>
        	</div>
        </div>
        <div>Oggetto<br>
        <input type="text" style="margin: 5px 0" id="message-subject" class="message-text" maxlength="100" tabindex="2">
        </div>
        <div>Messaggio<br>
        <textarea style="margin: 5px 0" rows="5" cols="10" id="message-body" class="message-text" tabindex="3"></textarea>
        </div>
        <div class="new-buttons clearfix">
            <a href="#" class="new-button preview"><b>Anteprima</b><i></i></a>
            <a href="#" class="new-button send"><b>Invia</b><i></i></a>
        </div>
    </form>	
</div>

	
	</div></div></div><div class="bb"><div></div></div></div>
</div>
</div>

<!-- dependencies
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/minimail.css" type="text/css" />
-->