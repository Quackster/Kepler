<ul id="quickmenu-rooms">
{% if rooms|length > 0 %}

{% set num = 0 %}
{% for room in rooms %}
	{% if num % 2 == 0 %}
	<li class="even">
	{% else %}
	<li class="odd">
	{% endif %}
	<a href="{{ site.sitePath }}/client?forwardId=2&amp;roomId={{ room.getData().getId() }}" onclick="roomForward(this, '{{ room.getData().getId() }}', 'private'); return false;" target="client" id="room-navigation-link_{{ room.getData().getId() }}">{% autoescape 'html' %}{{ room.getData().getName() }}{% endautoescape %}</a></li>
{% set num = num + 1 %}
{% endfor %}

{% else %}
	<li class="odd">You don't have any rooms yet</li>
{% endif %}
</ul>
<p class="create-room"><a href="{{ site.sitePath }}/client?shortcut=roomomatic" onclick="HabboClient.openShortcut(this, 'roomomatic'); return false;" target="client">Create a new room</a></p>