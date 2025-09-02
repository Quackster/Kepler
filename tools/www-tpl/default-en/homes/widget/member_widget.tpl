{% set membersList = sticker.getFirstMembersList() %}
{% set members = sticker.getMembersAmount() %}
{% set pages = sticker.getMembersPages() %}
{% set currentPage = 1 %}

<div class="movable widget MemberWidget" id="widget-{{ sticker.getId() }}" style=" left: {{ sticker.getX() }}px; top: {{ sticker.getY() }}px; z-index: {{ sticker.getZ() }};">
<div class="w_skin_{{ sticker.getSkin() }}">
	<div class="widget-corner" id="widget-{{ sticker.getId() }}-handle">
		<div class="widget-headline"><h3>
			{% if editMode %}
			<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/icon_edit.gif" width="19" height="18" class="edit-button" id="widget-{{ sticker.getId() }}-edit" />
			<script language="JavaScript" type="text/javascript">
			Event.observe("widget-{{ sticker.getId() }}-edit", "click", function(e) { openEditMenu(e, {{ sticker.getId() }}, "widget", "widget-{{ sticker.getId() }}-edit"); }, false);
			</script>
			{% endif %}
			<span class="header-left">&nbsp;</span><span class="header-middle">Members of this group (<span id="avatar-list-size">{{ members }}</span>)</span><span class="header-right">&nbsp;</span></h3>
		</div>	
	</div>
	<div class="widget-body">
		<div class="widget-content">
			<div id="avatar-list-search">
				<input type="text" style="float:left;" id="avatarlist-search-string"/>
				<a class="new-button" style="float:left;" id="avatarlist-search-button"><b>Search</b><i></i></a>
			</div>
			<br clear="all"/>
			{% include "homes/widget/habblet/membersearchpaging.tpl" %}
					<div class="clear"></div>
		</div>
	</div>
</div>
</div>
