<head>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/rooms.css" type="text/css" />
<script src="{{ site.staticContentPath }}/web-gallery/static/js/rooms.js" type="text/javascript"></script>
</head>
<div id="rooms-habblet-list-container-h120" class="recommendedrooms-lite-habblet-list-container">
                <ul class="habblet-list">

{% set num = 0 %}
{% for room in highestRatedRooms %}
	{% if num % 2 == 0 %}
	<li class="even">
	{% else %}
	<li class="odd">
	{% endif %}
	
	{% set occupancyLevel = 0 %}
	{% if room.getData().getVisitorsNow() > 0 %}
	
	{% set percentage = ((room.getData().getVisitorsNow() * 100) / room.getData().getVisitorsMax()) %}
	
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
	
    <span class="clearfix enter-room-link room-occupancy-{{ occupancyLevel }}" title="Go to room" roomid="{{ room.getData().getId() }}">
	    <span class="room-enter">Enter {{ site.siteName }} Hotel</span>
	    <span class="room-name">{{ room.getData().getName() }}</span>
	    <span class="room-description">{{ room.getData().getDescription() }}</span>
		<span class="room-owner">Owner: <a href="{{ site.sitePath }}/home/{{ room.getData().getOwnerName() }}">{{ room.getData().getOwnerName() }}</a></span>
    </span>
</li>
{% set num = num + 1 %}
{% endfor %}
        </ul>
            <div id="room-more-data-h120" style="display: none">
                <ul class="habblet-list room-more-data">
{% for room in highestHiddenRatedRooms %}
	{% if num % 2 == 0 %}
	<li class="even">
	{% else %}
	<li class="odd">
	{% endif %}
	
	{% set occupancyLevel = 0 %}
	{% if room.getData().getVisitorsNow() > 0 %}
	
	{% set percentage = ((room.getData().getVisitorsNow() / room.getData().getVisitorsMax()) * 100) %}
	
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
	
    <span class="clearfix enter-room-link room-occupancy-{{ occupancyLevel }}" title="Go to room" roomid="{{ room.getData().getId() }}">
	    <span class="room-enter">Enter {{ site.siteName }} Hotel</span>
	    <span class="room-name">{{ room.getData().getName() }}</span>
	    <span class="room-description">{{ room.getData().getDescription() }}</span>
		<span class="room-owner">Owner: <a href="{{ site.sitePath }}/home/{{ room.getData().getOwnerName() }}">{{ room.getData().getOwnerName() }}</a></span>
    </span>
</li>
{% set num = num + 1 %}
{% endfor %}
                </ul>
            </div>
            <div class="clearfix">
                <a href="#" class="room-toggle-more-data" id="room-toggle-more-data-h120">Show more rooms</a>
            </div>
</div>
<script type="text/javascript">
L10N.put("show.more", "Show more rooms");
L10N.put("show.less", "Show fewer rooms");
var roomListHabblet_h120 = new RoomListHabblet("rooms-habblet-list-container-h120", "room-toggle-more-data-h120", "room-more-data-h120");
</script>
