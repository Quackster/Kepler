<div style="position: relative;">
<div id="webstore-categories-container">
	<h4>Categories:</h4>
	<div id="webstore-categories">
<ul class="purchase-main-category">
		<li id="maincategory-1-stickers" class="selected-main-category webstore-selected-main">
			<div>Stickers</div>
			<ul class="purchase-subcategory-list" id="main-category-items-1">
				{% set num = 0 %}
				{% for category in stickerCategories %}
					{% if num == 0 %}<li id="subcategory-1-{{ category.getId() }}-stickers" class="subcategory-selected">{% else %}<li id="subcategory-1-{{ category.getId() }}-stickers" class="subcategory">{% endif %}
						<div>{{ category.getName() }}</div>
					</li>
				
				{% set num = num + 1 %}
				{% endfor %}

			</ul>
		</li>
		<li id="maincategory-4-backgrounds" class="main-category">
			<div>Backgrounds</div>
			<ul class="purchase-subcategory-list" id="main-category-items-4">
			
				{% for category in backgroundCategories %}
					<li id="subcategory-1-{{ category.getId() }}-stickers" class="subcategory">
						<div>{{ category.getName() }}</div>
					</li>
				{% endfor %}

			</ul>
		</li>
		<li id="maincategory-3-stickie_notes" class="main-category-no-subcategories">
			<div>Notes</div>
			<ul class="purchase-subcategory-list" id="main-category-items-3">

				<li id="subcategory-3-101-stickie_notes" class="subcategory">
					<div>29</div>
				</li>

			</ul>
		</li>
</ul>

	</div>
</div>


<div id="webstore-content-container">
	<div id="webstore-items-container">
		<h4>Select an item by clicking it</h4>
		<div id="webstore-items">
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
	
{% for box in emptyBoxes %}
	<li class="webstore-item-empty"></li>
{% endfor %}


</ul>		</div>
	</div>
	<div id="webstore-preview-container">
		<div id="webstore-preview-default"></div>
		<div id="webstore-preview">
		
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
</div>
	</div>
</div>

<div id="inventory-categories-container">
	<h4>Categories:</h4>
	<div id="inventory-categories">
<ul class="purchase-main-category">
	<li id="inv-cat-stickers" class="selected-main-category-no-subcategories">
		<div>Stickers</div>
	</li>
	<li id="inv-cat-backgrounds" class="main-category-no-subcategories">
		<div>Backgrounds</div>
	</li>
	<li id="inv-cat-widgets" class="main-category-no-subcategories">
		<div>Widgets</div>
	</li>
	<li id="inv-cat-notes" class="main-category-no-subcategories">
		<div>Notes</div>
	</li>
</ul>

	</div>
</div>

<div id="inventory-content-container">
	<div id="inventory-items-container">
		<h4>Select an item by clicking it</h4>
		<div id="inventory-items"><ul id="inventory-item-list">
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
	<li class="webstore-item-empty"></li>
</ul></div>
	</div>
	<div id="inventory-preview-container">
		<div id="inventory-preview-default"></div>
		<div id="inventory-preview"><h4>&nbsp;</h4>

<div id="inventory-preview-box"></div>

<div id="inventory-preview-place" class="clearfix">
	<div class="clearfix">
		<a href="#" class="new-button" id="inventory-place"><b></b><i></i></a>
	</div>
</div>

</div>
	</div>
</div>

<div id="webstore-close-container">
	<div class="clearfix"><a href="#" id="webstore-close" class="new-button"><b>Close</b><i></i></a></div>
</div>
</div>
