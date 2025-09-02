<h4 title=""></h4>

<div id="webstore-preview-box"></div>

<div id="webstore-preview-price">
Price:<br /><b>
	{% if product.getPrice() > 1 %}{{ product.getPrice() }} credits{% else %}{{ product.getPrice() }} credit{% endif %}
</b>
</div>

<div id="webstore-preview-purse">
{% if playerDetails.credits > 1 %}You have:<br /><b>{{ playerDetails.credits }} credits</b><br />{% else %}You have:<br /><b>{{ playerDetails.credits }} credit</b><br />{% endif %}
<a href="{{ site.sitePath }}/credits" target=_blank>Get Credits</a>
</div>

<div id="webstore-preview-purchase" class="clearfix">
	<div class="clearfix">
		<a href="#" class="new-button" id="webstore-purchase"><b>Purchase</b><i></i></a>
	</div>
</div>

<span id="webstore-preview-bg-text" style="display: none">Preview</span>		