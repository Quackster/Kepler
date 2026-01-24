<div class="movable widget RatingWidget" id="widget-{{ sticker.getId() }}" style="left: {{ sticker.getX() }}px; top: {{ sticker.getY() }}px; z-index: {{ sticker.getZ() }}">
<div class="w_skin_{{ sticker.getSkin() }}">
	<div class="widget-corner" id="widget-{{ sticker.getId() }}-handle">
		<div class="widget-headline"><h3>
{% if editMode %}
<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/icon_edit.gif" width="19" height="18" class="edit-button" id="widget-{{ sticker.getId() }}-edit" />

<script language="JavaScript" type="text/javascript">
Event.observe("widget-{{ sticker.getId() }}-edit", "click", function(e) { openEditMenu(e, {{ sticker.getId() }}, "widget", "widget-{{ sticker.getId() }}-edit"); }, false);
</script>
{% endif %}
		<span class="header-left">&nbsp;</span><span class="header-middle">My Rating</span><span class="header-right">&nbsp;</span></h3>
		</div>	
	</div>
	<div class="widget-body">
		<div class="widget-content">
	<div id="rating-main">
	
{% set average = sticker.getAverageRating() %}
{% set px = sticker.getRatingPixels() %}
{% set canRate = ((session.loggedIn == true) and (editMode == false) and (user.getId() != playerDetails.getId()) and (sticker.hasRated(playerDetails.getId()) == false)) %}

<script type="text/javascript">
{% if canRate %}
	var ratingWidget;
	document.observe("dom:loaded", function() { 
		ratingWidget = new RatingWidget({{ playerDetails.getId() }}, {{ sticker.getId() }});
	}); 
{% else %}
	var ratingWidget;
	 
	ratingWidget = new RatingWidget({{ playerDetails.getId() }}, {{ sticker.getId() }});

{% endif %}
</script><div class="rating-average">
		<b>Average rating: {{ average }}</b><br/>
	<div id="rating-stars" class="rating-stars" >
				<ul id="rating-unit_ul1" class="rating-unit-rating">
				<li class="rating-current-rating" style="width: {{ px }}px;" />
				{% if canRate %}
				<li><a href="#"   class="r1-unit rater">1</a></li>
				<li><a href="#"   class="r2-unit rater">2</a></li>
				<li><a href="#"   class="r3-unit rater">3</a></li>
				<li><a href="#"   class="r4-unit rater">4</a></li>
				<li><a href="#"   class="r5-unit rater">5</a></li>
				{% endif %}
			</ul>	
	</div>
	{{ sticker.getVoteCount() }} votes total	
	<br/>
	({{ sticker.getHighVoteCount() }} users voted 4 or better)
</div>
	</div>
		<div class="clear"></div>
		</div>
	</div>
</div>
</div>
