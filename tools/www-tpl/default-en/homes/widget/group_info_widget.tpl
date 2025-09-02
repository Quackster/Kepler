<div class="movable widget GroupInfoWidget" id="widget-{{ sticker.getId() }}" style=" left: {{ sticker.getX() }}px; top: {{ sticker.getY() }}px; z-index: {{ sticker.getZ() }};">
<div class="w_skin_{{ sticker.getSkin() }}">
	<div class="widget-corner" id="widget-{{ sticker.getId() }}-handle">
		<div class="widget-headline"><h3>
		
		{% if editMode %}
		<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/icon_edit.gif" width="19" height="18" class="edit-button" id="widget-{{ sticker.getId() }}-edit" />
		<script language="JavaScript" type="text/javascript">
		Event.observe("widget-{{ sticker.getId() }}-edit", "click", function(e) { openEditMenu(e, {{ sticker.getId() }}, "widget", "widget-{{ sticker.getId() }}-edit"); }, false);
		</script>
		{% endif %}
		
<span class="header-left">&nbsp;</span><span class="header-middle">Group info</span><span class="header-right">&nbsp;</span></h3>
		</div>	
	</div>
	<div class="widget-body">
		<div class="widget-content">
		{% autoescape 'html' %}
<div class="group-info-icon"><img src="{{ site.sitePath }}/habbo-imaging/badge/{{ group.getBadge() }}.gif" /></div>
	    <img id="groupname-{{ group.id }}-report" class="report-button report-gn"
			alt="report"
			src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/buttons/report_button.gif"
			style="display: none;" />
<h4>{{ group.getName() }}</h4>

<p>Group created: <b>{{ group.getCreatedDate() }}</b></p>
<p>{{ sticker.getMembersAmount() }}</b> members</p>
{% if group.getRoomId() > 0 %}<p><a href="{{ site.sitePath }}/client?forwardId=2&amp;roomId={{ room.getId() }}" onclick="HabboClient.roomForward(this, '{{ room.getId() }}', 'private'); return false;" target="client" class="group-info-room">{% autoescape 'html' %}{{ room.getData().getName() }}{% endautoescape %}</a></p>{% endif %}
<div class="group-info-description">{{ group.getDescription() }}</div>


    <div id="profile-tags-panel">
    <div id="profile-tag-list">
	{% include "../../groups/habblet/listgrouptags.tpl" %}
    </div>
		{% if session.loggedIn %}
{% if group.getOwnerId() == playerDetails.getId() %}
<div id="profile-tags-status-field">
 <div style="display: block;">
  <div class="content-red">
   <div class="content-red-body">
    <span id="tag-limit-message"><img src="{{ site.staticContentPath }}/web-gallery/images/register/icon_error.gif"/> The limit is 20 tags!</span>
    <span id="tag-invalid-message"><img src="{{ site.staticContentPath }}/web-gallery/images/register/icon_error.gif"/> Invalid tag.</span>
   </div>
  </div>
 <div class="content-red-bottom">
  <div class="content-red-bottom-body"></div>
 </div>
 </div>
</div>        <div class="profile-add-tag">
            <input type="text" id="profile-add-tag-input" maxlength="30"/><br clear="all"/>
            <a href="#" class="new-button" style="float:left;margin:5px 0 0 0;" id="profile-add-tag"><b>Add tag</b><i></i></a>
        </div>	{% endif %}{% endif %}
    </div>
<script type="text/javascript">
    document.observe("dom:loaded", function() {
        new GroupInfoWidget('{{ group.id }}', '1');
    });
</script>



	<img id="groupdesc-{{ sticker.id }}-report" class="report-button report-gd"
	    alt="report"
	    src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/buttons/report_button.gif"
        style="display: none;" />

		<div class="clear"></div>
		{% endautoescape %}
		</div>
	</div>
</div>
</div>