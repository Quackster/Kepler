<div class="movable widget TraxPlayerWidget" id="widget-{{ sticker.getId() }}" style=" left: {{ sticker.getX() }}px; top: {{ sticker.getY() }}px; z-index: {{ sticker.getZ() }};">
<div class="w_skin_{{ sticker.getSkin() }}">
	<div class="widget-corner" id="widget-{{ sticker.getId() }}-handle">
		<div class="widget-headline"><h3>
		
{% if editMode %}
<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/icon_edit.gif" width="19" height="18" class="edit-button" id="widget-{{ sticker.getId() }}-edit" />
<script language="JavaScript" type="text/javascript">
Event.observe("widget-{{ sticker.getId() }}-edit", "click", function(e) { openEditMenu(e, {{ sticker.getId() }}, "widget", "widget-{{ sticker.getId() }}-edit"); }, false);
</script>
{% endif %}

		<span class="header-left">&nbsp;</span><span class="header-middle">TRAXPLAYER</span><span class="header-right">&nbsp;</span></h3>
		</div>	
	</div>
	<div class="widget-body">
		<div class="widget-content">
	{% if (sticker.hasSong() == false) or (editMode == true) %}
	<div id="traxplayer-content" style="text-align: center;"><img src="{{ site.staticContentPath }}/web-gallery/images/traxplayer/player.png"/></div>
	{% else %}
	<div id="traxplayer-content" style="text-align: center;"></div>
	{% include "homes/widget/habblet/trax_song.tpl" %}
	{% endif %}
{% if editMode %}
<div id="edit-menu-trax-select-temp" style="display:none">
    <select id="trax-select-options-temp">
    <option value="0">- Choose song -</option>
		{% set song = sticker.getSong() %}
		{% for s in sticker.getSongs() %}
	    <option value="{{ s.getId() }}" {% if song.getId() == s.getId() %}selected{% endif %}>{{ s.getTitle() }}</option>
		{% endfor %}</select>
</div>
{% endif %}
		<div class="clear"></div>
		</div>
	</div>
</div>
</div>
