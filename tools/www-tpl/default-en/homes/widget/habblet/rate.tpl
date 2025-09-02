{% set average = sticker.getAverageRating() %}
{% set px = sticker.getRatingPixels() %}

<script type="text/javascript">	
	var ratingWidget;
	 
		ratingWidget = new RatingWidget({{ playerDetails.id }}, {{ sticker.getId() }});
	 
</script><div class="rating-average">
		<b>Average rating: {{ average }}</b><br/>
	<div id="rating-stars" class="rating-stars" >
				<ul id="rating-unit_ul1" class="rating-unit-rating">
				<li class="rating-current-rating" style="width:{{ px }}px;" />
	
			</ul>	
	</div>
	{{ sticker.getVoteCount() }} votes total
	<br/>
	({{ sticker.getHighVoteCount() }} users voted 4 or better)
</div>