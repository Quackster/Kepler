{% autoescape 'html' %}
<div class="habblet" id="my-tags-list">
<ul class="tag-list make-clickable">

{% if tags|length > 0 %}
	{% for tag in tags %}
<li><a href="{{ site.sitePath }}/tag/{{ tag }}" class="tag" style="font-size: 10px;">{{ tag }}</a>
                        <a class="tag-remove-link" title="Remove tag" href="#"></a></li>
	{% endfor %}
{% endif %}							
</ul>

     <form method="post" action="{{ site.sitePath }}/myhabbo/tag/add" onsubmit="TagHelper.addFormTagToMe();return false;" >
    <div class="add-tag-form clearfix">
		<a class="new-button" href="#" id="add-tag-button" onclick="TagHelper.addFormTagToMe();return false;"><b>Add tag</b><i></i></a>
        <input type="text" id="add-tag-input" maxlength="20" style="float: right"/>
        <em class="tag-question">{{ tagRandomQuestion }}</em>
    </div>
    <div style="clear: both"></div> 
    </form>

</div>

<script type="text/javascript">
    TagHelper.setTexts({
        tagLimitText: "You've reached the tag limit - delete one of your tags if you want to add a new one.",
        invalidTagText: "Invalid tag, the tag must be less than 20 characters and composed only of alphanumeric characters.",
        buttonText: "OK"
    });
    TagHelper.bindEventsToTagLists();
</script>
{% endautoescape %}
