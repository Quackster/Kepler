		<ul id="webstore-item-list">

{% for product in products %}
	<li id="webstore-item-{{ product.getId() }}" title="{{ product.getName() }}">
		<div class="webstore-item-preview {{ product.getCssClass() }}">
			<div class="webstore-item-mask">
			{% if product.getAmount() > 1 %}<div class="webstore-item-count"><div>x{{ product.getAmount() }}</div></div>{% endif %}
			</div>
		</div>
	</li>
{% endfor %}
	
{% for i in [1..emptyProducts] %}
	<li class="webstore-item-empty"></li>
{% endfor %}


</ul>