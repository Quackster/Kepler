<div id="profile-tags-container">
{% if (tags|length) == 0 %}
No tags.
{% else %}
	{% for tag in tags %}
    <span class="tag-search-rowholder">
		{% autoescape 'html' %}
        <a href="{{ site.sitePath }}/tag/{{ tag }}" class="tag">{{ tag }}</a>
		{% endautoescape %}
		{% if (session.loggedIn) and (group.getOwnerId() == playerDetails.id) %} 
		<img border="0" class="tag-delete-link" onMouseOver="this.src='{{ site.staticContentPath }}/web-gallery/images/buttons/tags/tag_button_delete_hi.gif'" onMouseOut="this.src='{{ site.staticContentPath }}/web-gallery/images/buttons/tags/tag_button_delete.gif'" src="{{ site.staticContentPath }}/web-gallery/images/buttons/tags/tag_button_delete.gif"/>
		{% else %}
		<img id="tag-img-added" border="0" class="tag-none-link" src="{{ site.staticContentPath }}/web-gallery/images/buttons/tags/tag_button_dim.gif" /> 
		{% endif %}
	</span>
	{% endfor %} 
{% endif %}
</div>

{% if session.loggedIn %}
	{% if group.getOwnerId() == playerDetails.getId() %}
	<script type="text/javascript">
		document.observe("dom:loaded", function() {
			TagHelper.setTexts({
				buttonText: "Ok",
				tagLimitText: "The limit is 20 tags!"
			});
		});
	</script>
	{% endif %}
{% endif %}