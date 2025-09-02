{% autoescape 'html' %}
<div class="habblet box-content">
{% if tagCloud|length > 0 %}
    <ul class="tag-list">
	{% for kvp in tagCloud %}
		{% set tag = kvp.getKey() %}
		{% set size = kvp.getValue() %}
		<li><a href="{{ site.sitePath }}/tag/{{ tag }}" class="tag" style="font-size: {{ size }}px;">{{ tag }}</a>
	{% endfor %}
	</ul> 
{% else %}
No tags to display.
{% endif %}							
   <div class="tag-search-form">
<form name="tag_search_form" action="{{ site.sitePath }}/tag/search" class="search-box">
    <input type="text" name="tag" id="search_query" value="" class="search-box-query" style="float: left"/>
	<a onclick="$(this).up('form').submit(); return false;" href="#" class="new-button search-icon" style="float: left"><b><span></span></b><i></i></a>	
</form>    </div>
</div>
{% endautoescape %}

