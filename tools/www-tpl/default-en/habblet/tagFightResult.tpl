{% autoescape 'html' %}
<div id="fightResultCount" class="fight-result-count">
	{{ result }}<br />
	{{ resultTag1 }}
({{ resultHits1 }}) hits
<br/>
	{{ resultTag2 }}
({{ resultHits2 }}) hits
</div>
<div class="fight-image">
		<img src="{{ site.staticContentPath }}/web-gallery/images/tagfight/tagfight_end_{{ tagFightImage }}.gif" alt="" name="fightanimation" id="fightanimation" />
	<a id="tag-fight-button-new" href="#" class="new-button" onclick="TagFight.newFight(); return false;"><b>New Fight</b><i></i></a>
	<a id="tag-fight-button" href="#" style="display:none" class="new-button" onclick="TagFight.init(); return false;"><b>Fight</b><i></i></a>
</div>
{% endautoescape %}