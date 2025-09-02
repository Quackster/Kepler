<!--
{% if (session.loggedIn == false) or (playerDetails.hasFlashWarning) %}
		<div id="flash-check" style="display: none">
		<div class="rounded" style="background-color: red; color: white; padding:5px 10px 10px 10px">
			<strong>Attention!</strong><br />
			You do not have flash installed, or enabled. Adobe Flash and Shockwave are recommended for this hotel to be playable.</br></br>
			With major internet browsers dropping Flash support, you will need to download a portable browser with Flash pre-installed <a style="color:white" href="http://www.mediafire.com/file/o9tknqhdlo655yc/Basilisk-Portable.exe/file">here</a> (for <a style="color:white" href="http://forum.ragezone.com/f353/portable-browser-flash-shockwave-basilisk-1192727/">more info</a> including screenshots).</br>
			You may download the <a style="color:white" href="{{ site.sitePath }}/help/shockwave_app">Shockwave portable client app</a> to play the server.</br>
			</br>
			<b>This alert will not appear if you have Flash enabled</b>
		</div>
		<br/>
		</div>
		
    <script> 
        function hasFlash() { 
            try { 
                return Boolean(new ActiveXObject('ShockwaveFlash.ShockwaveFlash')); 
            } catch (exception) { 
                return ('undefined' != typeof navigator.mimeTypes[ 
                    'application/x-shockwave-flash']); 
            } 
            return false; 
        }
		
		if (hasFlash()) {
			document.getElementById("flash-check").style.display = "none";
		} else {
			document.getElementById("flash-check").style.display = "block";
		}
    </script> 
{% endif %}
-->