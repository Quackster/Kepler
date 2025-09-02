{% if (onlineFriends|length == 0) and (offlineFriends|length == 0) %}

<ul id="quickmenu-friends">
<li class="odd">No friends were found.</li>
</ul>

{% else %}

<ul id="online-friends">
{% set num = 0 %}
{% for friend in onlineFriends %}
	{% if num % 2 == 0 %}
	<li class="even">
	{% else %}
	<li class="odd">
	{% endif %}
	<a href="{{ site.sitePath }}/home/{{ friend.getUsername() }}">{{ friend.getUsername() }}</a>
</li>
{% set num = num + 1 %}
{% endfor %}

</ul>

<ul id="offline-friends">

{% for friend in offlineFriends %}
	{% if num % 2 == 0 %}
	<li class="even">
	{% else %}
	<li class="odd">
	{% endif %}
	<a href="{{ site.sitePath }}/home/{{ friend.getUsername() }}">{{ friend.getUsername() }}</a>
</li>
{% set num = num + 1 %}
{% endfor %}

</ul>


{% endif %}