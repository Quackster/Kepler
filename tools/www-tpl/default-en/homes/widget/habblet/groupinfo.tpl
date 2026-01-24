<div class="groups-info-basic">
	<div class="groups-info-close-container"><a href="#" class="groups-info-close"></a></div>
	
	<div class="groups-info-icon"><a href="{{ group.generateClickLink() }}"><img src="{{ site.sitePath }}/habbo-imaging/badge/{{ group.getBadge() }}.gif" /></a></div>
	<h4><a href="{{ group.generateClickLink() }}"></a></h4>
	    <img id="groupname-{{ group.getId }}-report" class="report-button report-gn"
			alt="report"
			src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/buttons/report_button.gif"
			style="display: none;" />
{% autoescape 'html' %}
<h4>{{ group.getName() }}</h4>
<p>Group created:<br />
<b>{{ group.getCreatedDate() }}</b>
	</p>
	<div class="groups-info-description">{{ group.getDescription() }}</div>
	    <img id="groupdesc-{{ group.getId }}-report" class="report-button report-gd"
	        alt="report"
	        src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/buttons/report_button.gif"
            style="display: none;" />
</div>
{% endautoescape %}