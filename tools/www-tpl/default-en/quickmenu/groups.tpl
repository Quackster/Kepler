<ul id="quickmenu-groups">

{% if groups|length > 0 %}
	{% set i = 1 %}
	{% set status = 0 %}
	
	{% for group in groups %}				
		{% if i % 2 == 0 %}
			{% set status = "odd" %}
		{% else %}
			{% set status = "even" %}
		{% endif %}
			
		<li class="{{ status }}">
			{% if group.getRoomId() > 0 %}
			<a href="{{ site.sitePath }}/client?forwardId=2&amp;roomId={{ group.getRoomId() }}" onclick="HabboClient.roomForward(this, '{{ group.getRoomId() }}', 'private'); return false;" target="client" class="group-room" title=""></a>
			{% endif %}
			{% if group.isMember(playerDetails.id) %}
				{% set member = group.getMember(playerDetails.id) %}
				{% if member.getMemberRank().getRankId() == 3 %}
					<div class="owned-group" title="Owner"></div>
				{% endif %}
				{% if member.getMemberRank().getRankId() == 2 %}
					<div class="admin-group" title="Administrator"></div>
				{% endif %}

			{% endif %}
			
			{% if playerDetails.getFavouriteGroupId() == group.id %}
			<div class="favourite-group" title="Favorite"></div>
			{% endif %}
			
			<a href="{{ group.generateClickLink() }}">{% autoescape 'html' %}{{ group.name }}{% endautoescape %}</a>
		</li>

		{% set i = i + 1 %}
	{% endfor %}
{% else %}
	<li class="odd">You don't belong to any groups yet</li>
{% endif %}
<p class="create-group"><a href="#" onclick="GroupPurchase.open(); return false;">Create a group</a></p>