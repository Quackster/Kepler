{% set rooms = sticker.getOwnerRooms() %}
{% autoescape 'html' %}

<div class="movable widget RoomsWidget" id="widget-{{ sticker.getId() }}" style="left: {{ sticker.getX() }}px; top: {{ sticker.getY() }}px; z-index: {{ sticker.getZ() }}">
<div class="w_skin_{{ sticker.getSkin() }}">
	<div class="widget-corner" id="widget-{{ sticker.getId() }}-handle">
		<div class="widget-headline"><h3>
{% if editMode %}
<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/icon_edit.gif" width="19" height="18" class="edit-button" id="widget-{{ sticker.getId() }}-edit" />
<script language="JavaScript" type="text/javascript">
Event.observe("widget-{{ sticker.getId() }}-edit", "click", function(e) { openEditMenu(e, {{ sticker.getId() }}, "widget", "widget-{{ sticker.getId() }}-edit"); }, false);
</script>
{% endif %}
		<span class="header-left">&nbsp;</span><span class="header-middle">MY ROOMS</span><span class="header-right">&nbsp;</span></h3>
		</div>	
	</div>
	<div class="widget-body">
		<div class="widget-content">
		{% if rooms|length == 0 %}
		You do not have any rooms yet
		{% else %}
    <div id="room_wrapper">
<table border="0" cellpadding="0" cellspacing="0">

{% for room in rooms %}
	{% set openState = "room_icon_open" %}
	
	{% if room.getData().getAccessTypeId() == 1 %}
		{% set openState = "room_icon_locked" %}
	{% endif %}
	
	{% if room.getData().getAccessTypeId() == 2 %}
		{% set openState = "room_icon_password" %}
	{% endif %}
<tr>

<td valign="top" >
		<div class="room_image">
				<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/rooms/{{ openState }}.gif" alt="" align="middle"/>
		</div>
</td>
<td >
        	<div class="room_info">
        		<div class="room_name">
        			{% autoescape 'html' %}{{ room.getData().getName() }}{% endautoescape %}    		</div>
				<div class="clear"></div>
        		<div>{% autoescape 'html' %}{{ room.getData().getDescription() }}{% endautoescape %}        		</div>
					<a href="{{ site.sitePath }}/client?forwardId=2&amp;roomId={{ room.getId() }}"
					   target="client"
					   id="room-navigation-link_{{ room.getId() }}"
					   onclick="HabboClient.roomForward(this, '{{ room.getId() }}', 'private', false); return false;">					 enter room					 </a>        	</div>
		<br class="clear" />

</td>
</tr>
{% endfor %}
</table>
</div>
{% endif %}
		<div class="clear"></div>
		</div>
	</div>
</div>
</div>
{% endautoescape %}