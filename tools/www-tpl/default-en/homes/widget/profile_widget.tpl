{% autoescape 'html' %}

<div class="movable widget ProfileWidget" id="widget-{{ sticker.getId() }}" style="left: {{ sticker.getX() }}px; top: {{ sticker.getY() }}px; z-index: {{ sticker.getZ() }}">
<div class="w_skin_{{ sticker.getSkin() }}">
	<div class="widget-corner" id="widget-{{ sticker.getId() }}-handle">
		<div class="widget-headline"><h3>
{% if editMode %}
<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/icon_edit.gif" width="19" height="18" class="edit-button" id="widget-{{ sticker.getId() }}-edit" />
<script language="JavaScript" type="text/javascript">
Event.observe("widget-{{ sticker.getId() }}-edit", "click", function(e) { openEditMenu(e, {{ sticker.getId() }}, "widget", "widget-{{ sticker.getId() }}-edit"); }, false);
</script>
{% endif %}
		<span class="header-left">&nbsp;</span><span class="header-middle">MY PROFILE</span><span class="header-right">&nbsp;</span></h3>
		</div>	
	</div>
	<div class="widget-body">
		<div class="widget-content">
	<div class="profile-info">		
		{% autoescape 'html' %}
		<div class="name" style="float: left">
		<span class="name-text">{{ user.getName() }}</span>
							<img id="name-4-report" class="report-button report-n"
				alt="report"
				src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/buttons/report_button.gif"
				style="display: none;" />
				</div>

		<br class="clear" />
		
		{% if user.isOnline() and user.isProfileVisible() %}
					<img alt="online" src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/profile/habbo_online_anim.gif" />
		{% else %}
					<img alt="online" src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/profile/habbo_offline.gif" />
		{% endif %}
		<div class="birthday text">
			Created on:
		</div>
		<div class="birthday date">
			{{ user.getCreatedAt() }}		</div>

    	<div>
		{% if hasFavouriteGroup %}
		<a href="{{ group.generateClickLink() }}" title="{% autoescape 'html' %}{{ group.getName() }}{% endautoescape %}"><img src="{{ site.sitePath }}/habbo-imaging/badge/{{ group.getBadge() }}.gif" /></a>
		{% endif %}
		
		{% if hasBadge %}
		<img src="{{ site.staticContentPath }}/c_images/album1584/{{ badgeCode }}.gif" />
		{% endif %}
		</div>
		{% endautoescape %}
	</div>
	<div class="profile-figure">
			<img alt="{{ user.getName() }}" src="{{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ user.figure }}&size=b&direction=4&head_direction=4&crr=0&gesture=&frame=1" />
	</div>
	{% autoescape 'html' %}
		<div class="profile-motto">
		{{ user.motto }}						<img id="motto-4-report" class="report-button report-n"
			alt="report"
			src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/buttons/report_button.gif"
			style="display: none;" />
				<div class="clear"></div>
	</div>
	{% endautoescape %}
				{% if canAddFriend %}
				<div class="profile-friend-request clearfix">
						<a class="new-button" id="add-friend" style="float: left"><b>Add as friend</b><i></i></a>
					</div>
				{% endif %}
    <div id="profile-tags-panel">
    <div id="profile-tag-list">
{% include "homes/widget/habblet/taglist.tpl" %}

</div>
<div id="profile-tags-status-field">
 <div style="display: block;">
  <div class="content-red">
   <div class="content-red-body">
    <span id="tag-limit-message"><img src="{{ site.staticContentPath }}/web-gallery/images/register/icon_error.gif"/> The limit is 8 tags!</span>
    <span id="tag-invalid-message"><img src="{{ site.staticContentPath }}/web-gallery/images/register/icon_error.gif"/> Invalid tag.</span>
   </div>
  </div>
 <div class="content-red-bottom">
  <div class="content-red-bottom-body"></div>
 </div>
 </div>
</div>        {% if editMode == false %}{% if session.loggedIn and user.id == playerDetails.id %}<div class="profile-add-tag">
            <input type="text" id="profile-add-tag-input" maxlength="30"/><br clear="all"/>
            <a href="#" class="new-button" style="float:left;margin:5px 0 0 0;" id="profile-add-tag"><b>Add tag</b><i></i></a>
        </div>{% endif %}{% endif %}
    </div>
	{% if session.loggedIn %}
    <script type="text/javascript">
		document.observe("dom:loaded", function() {
			new ProfileWidget('{{ user.id }}', '{{ playerDetails.id }}', {
				headerText: "Are you sure?",
				messageText: "Are you sure you want to ask <strong\>{{ user.getName() }}</strong\> to be your friend? Think twice before you hit OK!",
				loginText: "You must sign in before sending a friend request.",
				buttonText: "Ok",
				cancelButtonText: "Cancel"
			});
		});
	</script>
	{% endif  %}
		<div class="clear"></div>
		</div>
	</div>
</div>
</div>
{% endautoescape %}