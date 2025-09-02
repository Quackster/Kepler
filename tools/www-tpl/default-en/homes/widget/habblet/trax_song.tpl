{% if sticker.hasSong() %}
{% set song = sticker.getSong() %}
<embed type="application/x-shockwave-flash"
src="{{ site.sitePath }}/flash/traxplayer/traxplayer.swf" name="traxplayer" quality="high"
base="{{ site.sitePath }}/flash/traxplayer/" allowscriptaccess="always" menu="false"
wmode="transparent" flashvars="songUrl={{ site.sitePath }}/trax/song/{{ song.getId() }}&amp;sampleUrl=http://cdn.classichabbo.com/r38/dcr/hof_furni/mp3/"
height="66" width="210" />
{% else %}
<div id="traxplayer-content" style="text-align: center;"><img src="{{ site.staticContentPath }}/web-gallery/images/traxplayer/player.png"/></div>
{% endif %}