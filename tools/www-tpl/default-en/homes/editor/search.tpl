<ul>
	<li>Click on link below to insert it into the document</li>

	{% autoescape 'html' %}
	{% for kvp in querySearch %}
		{% set key = kvp.getKey() %}
		{% set value = kvp.getValue() %}
							
    <li><a href="#" class="linktool-result" type="{{ type }}" 
    	value="{{ value }}" title="{{ key }}">{{ key }}</a></li>

	{% endfor %}
	{% endautoescape %}


</ul>