{% set entries = sticker.getGuestbookEntries() %}
{% set hasDeletePermission = false %}

{% if session.loggedIn %}
{% set hasDeletePermission = sticker.canDeleteEntries(playerDetails.id) %}
{% endif %}

<div class="movable widget GuestbookWidget" id="widget-{{ sticker.getId() }}" style=" left: {{ sticker.getX() }}px; top: {{ sticker.getY() }}px; z-index: {{ sticker.getZ() }};">
<div class="w_skin_{{ sticker.getSkin() }}">
	<div class="widget-corner" id="widget-{{ sticker.getId() }}-handle">
		<div class="widget-headline"><h3>
		
		{% if editMode %}
		<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/icon_edit.gif" width="19" height="18" class="edit-button" id="widget-{{ sticker.getId() }}-edit" />
		<script language="JavaScript" type="text/javascript">
		Event.observe("widget-{{ sticker.getId() }}-edit", "click", function(e) { openEditMenu(e, {{ sticker.getId() }}, "widget", "widget-{{ sticker.getId() }}-edit"); }, false);
		</script>
		{% endif %}
		
		<span class="header-left">&nbsp;</span><span class="header-middle">My Guestbook(<span id="guestbook-size">{{ entries|length }}</span>) <span id="guestbook-type" class="{{ sticker.getGuestbookState() }}"><img src="{{ site.staticContentPath }}/web-gallery/images/groups/status_exclusive.gif" title="myhabbo.guestbook.unknown.private" alt="myhabbo.guestbook.unknown.private"/></span></span><span class="header-right">&nbsp;</span></h3>
		</div>	
	</div>
	<div class="widget-body">
		<div class="widget-content">
<div id="guestbook-wrapper" class="gb-{{ sticker.getGuestbookState() }}">
<ul class="guestbook-entries" id="guestbook-entry-container">
	
	{% if entries|length == 0 %}
	<div id="guestbook-empty-notes">This guestbook has no entries.</div>
	{% else %}
		{% for entry in entries %}
			{% include "homes/widget/guestbook/add.tpl" with {"entry": entry} %}
		{% endfor %}
	{% endif %}
	</ul></div>
{% if session.loggedIn and sticker.isPostingAllowed(playerDetails.id) %}
	{% if editMode == false %}
	<div class="guestbook-toolbar clearfix">
	<a href="#" class="new-button envelope-icon" id="guestbook-open-dialog">
	<b><span></span>Post new message</b><i></i>
	</a>
	</div>
{% endif %}
{% endif %}

<script type="text/javascript">	
	document.observe("dom:loaded", function() {
		var gb{{ sticker.getId() }} = new GuestbookWidget('{{ group.getId() }}', '{{ sticker.getId() }}', 500);
				var editMenuSection = $('guestbook-privacy-options');
		if (editMenuSection) {
			gb{{ sticker.getId() }}.updateOptionsList('public');
		}
	});
</script>
		<div class="clear"></div>
		</div>
	</div>
</div>
</div>