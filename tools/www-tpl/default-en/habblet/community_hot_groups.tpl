<head>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/moredata.js" type="text/javascript"></script>
</head>
<div id="hotgroups-habblet-list-container" class="habblet-list-container groups-list">
    <ul class="habblet-list two-cols clearfix">
		{% set position = "right" %}
						
		{% set i = 0 %}
		{% set lefts = 0 %}
		{% set rights = 0 %}
		{% for group in hotGroups %}				
				{% if i % 2 == 0 %}
					{% set position = "right" %}
					{% set rights = rights + 1 %}
				{% else %}
					{% set position = "left" %}
					{% set lefts = lefts + 1 %}
				{% endif %}
				
				{% if lefts % 2 == 0 %}
					{% set status = "odd" %}
				{% else %}
					{% set status = "even" %}
				{% endif %}
		<li class="{{ status }} {{ position }}" style="background-image: url({{ site.sitePath }}/habbo-imaging/badge/{{ group.badge }}.gif)">
			<a class="item" href="{{ group.generateClickLink() }}"><span class="index">{{ i + 1}}.</span> {% autoescape 'html' %}{{ group.name }}{% endautoescape %}</a>
		</li>
				{% set i = i + 1 %}
		{% endfor %}
	</ul>
    <div id="hotgroups-list-hidden-h122" style="display: none">
    <ul class="habblet-list two-cols clearfix">
		{% for group in hotHiddenGroups %}				
				{% if i % 2 == 0 %}
					{% set position = "right" %}
					{% set rights = rights + 1 %}
				{% else %}
					{% set position = "left" %}
					{% set lefts = lefts + 1 %}
				{% endif %}
				
				{% if lefts % 2 == 0 %}
					{% set status = "odd" %}
				{% else %}
					{% set status = "even" %}
				{% endif %}
		<li class="{{ status }} {{ position }}" style="background-image: url({{ site.sitePath }}/habbo-imaging/badge/{{ group.badge }}.gif)">
			<a class="item" href="{{ group.generateClickLink() }}"><span class="index">{{ i + 1 }}.</span> {% autoescape 'html' %}{{ group.name }}{% endautoescape %}</a>
		</li>
				{% set i = i + 1 %}
		{% endfor %}
	</ul>
</div>
    <div class="clearfix">
        <a href="#" class="hotgroups-toggle-more-data secondary" id="hotgroups-toggle-more-data-h122">Show more Groups</a>
    </div>
<script type="text/javascript">
L10N.put("show.more.groups", "Show more Groups");
L10N.put("show.less.groups", "Show less Groups");
var hotGroupsMoreDataHelper = new MoreDataHelper("hotgroups-toggle-more-data-h122", "hotgroups-list-hidden-h122","groups");
</script>
</div>
