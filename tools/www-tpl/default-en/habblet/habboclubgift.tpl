<div class="box-content">
    <div id="hc-catalog">
	  {% if currentPage != 1 %}
      <div class="hc-catalog-navi">
        <a href="/credits/habboclub?month=0&catalogpage=0" onclick="return habboclub.catalogUpdate(1, 0)">&lt;&lt;</a>
      </div>
	  {% else %}
	  <div class="hc-catalog-navi">
	    &lt;&lt;
	  </div>
	  {% endif %}
	  
	  {% if currentPage != 1 %}
      <div class="hc-catalog-navi">
        <a href="/credits/habboclub?month=0&catalogpage=0" onclick="return habboclub.catalogUpdate({{ currentPage - 1}}, 0)">Previous</a>
      </div>
	  {% else %}
	  <div class="hc-catalog-navi">
	    Previous
	  </div>
	  {% endif %}
	  
	 {% for page in pages %}
	 {% if page == currentPage %}
	       <div class="hc-catalog-monthNumber">
        <b>{{ currentPage }}</b>
      </div>

	 {% else %}
      <div class="hc-catalog-monthNumber">
        <a href="/credits/habboclub?month={{ page }}&catalogpage=0" onclick="return habboclub.catalogUpdate({{ page }}, 0)">{{ page }}</a>
      </div>
	 {% endif %}
    {% endfor %}

	 {% if currentPage != lastPage %}
	  <div class="hc-catalog-monthNumber">
        <a href="/credits/habboclub?month=0&catalogpage=0" onclick="return habboclub.catalogUpdate({{ currentPage + 1 }}, 0)">Next</a>
      </div>
	  {% else %}
	  <div class="hc-catalog-monthNumber">
        Next
      </div>
	  {% endif %}
	
	{% if currentPage == lastPage %}
	&gt;&gt;
	{% else %}
	  <div class="hc-catalog-monthNumber">
        <a href="/credits/habboclub?month=0&catalogpage=0" onclick="return habboclub.catalogUpdate({{ lastPage }}, 0)">&gt;&gt;</a>
      </div>
	{% endif %}
    </div>
    <div id="hc-catalog-selectedGift">
      <div id="hc-catalog-starGreen">
        <div id="hc-catalog-giftNumber">
          #{{ currentPage }}
        </div>
      </div>
      <div id="hc-catalog-giftPicture">
        <img src="{{ site.staticContentPath }}/web-gallery/images/hcgifts/{{ item.getSprite() }}.png" alt="{{ item.getName() }}"/>
      </div>
    </div>
    <div id="hc-catalog-giftName">
      <b>{{ item.getName() }}</b>
    </div>
</div>