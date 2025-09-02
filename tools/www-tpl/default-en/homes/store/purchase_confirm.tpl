<div class="webstore-item-preview {{ product.getCssClass() }}">
	<div class="webstore-item-mask">
		
	</div>
</div>

{% if noCredits %}
<p>
You do not have enough credits to purchase this</p>

<div class="clear"></div>
{% else %}
<p>
Are you sure you want to purchase this product?</p>

<p class="new-buttons">
<a href="#" class="new-button" id="webstore-confirm-cancel"><b>Cancel</b><i></i></a>
<a href="#" class="new-button" id="webstore-confirm-submit"><b>Continue</b><i></i></a>
</p>

<div class="clear"></div>
{% endif %}