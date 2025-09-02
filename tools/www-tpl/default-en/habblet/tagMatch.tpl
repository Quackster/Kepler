{% autoescape 'html' %}
{% if errorMsg != "" %}
<div class="tag-match-error">
	{{ errorMsg }}
</div>
{% else %}
Coming soon.
{% endif %}							
{% endautoescape %}
