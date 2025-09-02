<ul class="habblet-list">
{% set num = 0 %}
{% for event in events %}		
	{% set listClass = "" %}
	
	{% if num % 2 == 0 %}
		{% set listClass = "even" %}
	{% else %}
		{% set listClass = "odd" %}
	{% endif %}
	
	{% set roomData = event.getRoomData() %}
	{% set creator = event.getUserInfo().getName() %}
	
	{% set occupancyLevel = 1 %}
	{% if roomData.getVisitorsNow() > 0 %}
	
	{% set percentage = ((roomData.getVisitorsNow() / roomData.getVisitorsMax()) * 100) %}
	
	{% if (percentage >= 99) %}
		{% set occupancyLevel = 5 %}
	{% elseif (percentage > 65) %}
		{% set occupancyLevel = 4 %}
	{% elseif (percentage > 32) %}
		{% set occupancyLevel = 3 %}
	{% elseif (percentage > 0) %}
		{% set occupancyLevel = 2 %}
	{% endif %}
	
	{% endif %}

	{% autoescape 'html' %}<li class="{{ listClass }} room-occupancy-{{ occupancyLevel }}" roomid="100">
	<div title="Go to the room where this event is held">
		<span class="event-name"><a href="{{ site.sitePath }}/client?forwardId=2&amp;roomId={{ roomData.getId() }}" onclick="HabboClient.roomForward(this, '{{ roomData.getId() }}', 'private'); return false;">{{ event.getName() }}</a></span>
		<span class="event-owner"> by <a href="{{ site.sitePath }}/home/{{ creator }}">{{ creator }}</a></span>
		<p>{{ event.getDescription() }} (<span class="event-date">{{ event.getFriendlyDate() }}</span>)</p>
	</div>
	</li>{% endautoescape %}
	{% set num = num + 1 %}
{% endfor %}

</ul>